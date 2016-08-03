/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.sns.pvnames;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.autocomplete.AutoCompleteHelper;
import org.csstudio.autocomplete.AutoCompleteResult;
import org.csstudio.autocomplete.IAutoCompleteProvider;
import org.csstudio.autocomplete.parser.ContentDescriptor;
import org.csstudio.autocomplete.parser.ContentType;
import org.csstudio.autocomplete.proposals.Proposal;
import org.csstudio.autocomplete.proposals.ProposalStyle;
import org.csstudio.platform.utility.rdb.RDBCache;

/** PV Name lookup for SNS 'signal' database
 *
 *  <p>AutoCompleteService will re-use one instance of this class
 *  for all lookups, calling <code>listResult</code> whenever
 *  the user types a new character, using a new thread for each lookup.
 *  Before starting a new lookup, however, <code>cancel()</code> is invoked.
 *  This means there are never multiple concurrent lookups started on purpose,
 *  but a previously started lookup may still continue in its thread
 *  in case <code>cancel()</code> has no immediate effect.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSPVListProvider implements IAutoCompleteProvider
{
    private static final Logger logger = Logger.getLogger(SNSPVListProvider.class.getName());

    /** Cached RDB connection */
    private RDBCache cache = null;

    /** Currently executed statement.
     *  SYNC on this for access
     */
    private PreparedStatement current_statement = null;
    private String current_pattern = "";

    private synchronized void setCurrentStatement(final PreparedStatement statement, final String pattern)
    {
        current_statement = statement;
        current_pattern = pattern;
	}

	/** {@inheritDoc} */
	@Override
	public boolean accept(final ContentType type)
	{
	    logger.log(Level.FINE, "Accept {0}?", type);
	    return type == ContentType.PVName ||
		       type == ContentType.PV ||
		       type == ContentType.Undefined;
	}

	/** {@inheritDoc} */
	@Override
	public AutoCompleteResult listResult(final ContentDescriptor desc, final int limit)
    {
	    final AutoCompleteResult pvs = new AutoCompleteResult();

	    // Would expect autocomplete framework to cancel previous requests,
	    // but it doesn't always do this, so cancel any ongoing request before
	    // starting a new one:
	    cancel();

	    final String content = desc.getValue();
	    // Don't bother with RDB lookup for only a few characters:
	    // Takes a lot of time and list will be far to long anyway.
	    final int min_request_length = Preferences.getMinRequestLength();
        if (content.length() < min_request_length)
	    {
	        logger.log(Level.FINE, "Ignoring request for {0}, need at least {1} characters",
	                   new Object[] { content, min_request_length });
	        return pvs;
	    }
		final String type = desc.getAutoCompleteType().value();
		logger.log(Level.FINE, ">>> Lookup {0} (type {1}, limit {2})",
		        new Object[] { content, type, limit });


		// Support partial matches:
		// Lookup of "DTL" will actually look for "DTL*".
		// Could also expand that to "*DTL*", but since SNS RDB is slow enough,
		// require user to explicitly enter "*.." for a fully non-anchored search.
		String search_pattern = content;
		if (! search_pattern.endsWith("*"))
		    search_pattern += "*";

        // Create RDB pattern from *, ? wildcards
    	final String like = AutoCompleteHelper.convertToSQL(search_pattern);

        try
        {
            if (cache == null)
                cache = new RDBCache("SNSPVListProvider",
                        Preferences.getURL(), Preferences.getUser(), Preferences.getPassword(),
                        2, TimeUnit.MINUTES);
            lookup(pvs, like, limit);
        }
        catch (Throwable ex)
        {
            // Suppress error resulting from call to cancel()
            final String message = ex.getMessage();
            if (message != null  &&
                (message.startsWith("ORA-01013")  ||  message.startsWith("ORA-01001")))
                logger.log(Level.FINE, "<<< Canceled lookup for {0}", content);
            else
                logger.log(Level.WARNING, "<<< Failed Lookup for " + content, ex);
            return pvs;
        }

        // Mark, i.e. highlight the original search pattern within each result
        final Pattern namePattern = AutoCompleteHelper.convertToPattern(content);
        for (Proposal p : pvs.getProposals())
        {
            final Matcher m = namePattern.matcher(p.getValue());
            if (m.find())
                p.addStyle(ProposalStyle.getDefault(m.start(), m.end()-1));
        }

        if (logger.isLoggable(Level.FINER))
            logger.log(Level.FINER, "<<< PVs for {0} ({1}): {2}", new Object[] { content, pvs.getCount(), pvs.getProposalsAsString() });
        return pvs;
    }

    /** Perform lookup
     *
     *  @param pvs Where to store result
     *  @param like SQL 'LIKE' pattern
     *  @param limit Maximum number of PVs to return
     *  @throws Exception on error
     */
    private void lookup(final AutoCompleteResult pvs, final String like, final int limit) throws Exception
    {
        // Initially, "SELECT COUNT(*) .." obtained count, then fetched actual names in second query.
        // jProfiler showed that the count took longer (3x !!) than fetching the names.
        // Even considering that a second query for similar information is likely faster because of caching,
        // having only one query, counting all but only keeping names up to 'limit', is overall faster.
        int count = 0;
        try
        (
            final PreparedStatement statement =
                cache.getConnection().prepareStatement(
                    "SELECT SGNL_ID FROM EPICS.SGNL_REC WHERE SGNL_ID LIKE ? ORDER BY SGNL_ID");
        )
        {
            setCurrentStatement(statement, like);
            statement.setString(1, like);
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {
                if (++count <= limit)
                    pvs.addProposal(new Proposal(result.getString(1), false));
            }
            result.close();
        }
        finally
        {
            cache.releaseConnection();
            setCurrentStatement(null, null);
            pvs.setCount(count);
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void cancel()
    {
        if (current_statement == null)
            return;
        try
        {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "--- Cancelling ongoing lookup for {0}", current_pattern);
            current_statement.cancel();
            current_statement = null;
        }
        catch (Throwable ex)
        {
            // Ignore
        }
    }

}
