/*******************************************************************************
 * Copyright (c) 2012-2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.logbook.sns.elog.ELog;
import org.csstudio.logbook.sns.elog.ELogPriority;

/** Class for creating a runnable jar file that writes to the SNS logbook
 *
 *  <p>Easiest way to create the jar file:
 *  After running once from with Eclipse IDE,
 *  assert that the 'URL' is set to the correct one,
 *  then use File, Export, Java, Runnable JAR File,
 *  selecting the run configuration, enter Jar file name.
 *
 *  @author Delphy Armstrong
 */
public class SendToElog
{
	// input arguments
	private static String body;
	private static String title;
	private static String uid = "";
	private static String passwd = "";
	private static String LogBook;

	/** URL from Office */
	private static final String OFFICE_URL =
	    "jdbc:oracle:thin:@" +
	    "(DESCRIPTION=(LOAD_BALANCE=OFF)(FAILOVER=ON)" +
	    "(ADDRESS=(PROTOCOL=TCP)(HOST=snsappa.sns.ornl.gov)(PORT=1610))" +
	    "(ADDRESS=(PROTOCOL=TCP)(HOST=snsappb.sns.ornl.gov)(PORT=1610))" +
	    "(CONNECT_DATA=(SERVICE_NAME=prod_controls)))";

	/** URL in ICS */
	private static final String ICS_URL =
	    "jdbc:oracle:thin:@//ics-srv-exa-scan:1521/prod_controls";

	private static String URL = ICS_URL;

	public static void main(final String[] args)
	{
		String[] inputCmds = args;
		// if no arguments are input, throw an Exception
		if (inputCmds.length<1)
		    throw new IllegalArgumentException("Usage: sendToElog -url URL -logbook logbookName -title LogTitle -body \"body of the log message\" -uid \"optional user id\" -password \"password for uid\" -file \"image or text filename\" -caption \"file caption\" ... \nOR\nsendToElog \"filename containing above commands\"");
		Map<String, Integer> images = new HashMap<String, Integer>();
		Map<String, Integer> captions = new HashMap<String, Integer>();


		String[] items = null;
		// check for an input file of commands
		if(inputCmds.length==1)
		{
			final File file = new File(inputCmds[0]);
			StringBuffer contents = new StringBuffer();
			BufferedReader reader = null;
			//Read File Line By Line
			try {
				reader = new BufferedReader(new FileReader(file));
				String text = null;
				// repeat until all lines is read
				while ((text = reader.readLine()) != null)
				{
					contents.append(text).append(System.getProperty("line.separator"));
				}
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (FileNotFoundException ex)
			{
				System.out.println(ex.getMessage());
				return;
			}
			catch (IOException ex)
			{
				System.out.println(ex.getMessage());
				return;
			}
			// read the words from the input file
			final String[] list = contents.toString().split("\\ ");
			final String[] entry = new String[list.length];
			int j=0;
			// parse the input lines read from the input file
			for(int i=0;i<list.length;i++)
			{
				// remove trailing new lines
				if(list[i].endsWith("\n")) list[i]=list[i].substring(0,list[i].length()-1);
				if(list[i].startsWith("\"") && list[i].endsWith("\"")) entry[j++]=list[i].substring(1,list[i].length()-1);
				// find the ending item of the string, looking for the closed quote
				else if(list[i].startsWith("\""))
				{
					entry[j]=list[i++].substring(1);
					if(entry[j].endsWith("\""))
					{
						j++;
						continue;
					}
					while(i<list.length && !list[i].endsWith("\"")) entry[j]=entry[j]+" " +list[i++];
					if(i==list.length) continue;
					if(i<list.length && list[i].endsWith("\"")) entry[j]=entry[j]+" " +list[i].substring(0, list[i].length()-1);
					j++;
				}
				// words with a leading hyphen, not separated by double quotes, are input options
				else if(list[i].startsWith("-")) entry[j++]=list[i];
			}
		    int len=0;
		    for(len=0;entry[len]!=null;len++);

		    items = new String[len];
		}
		// if no filename is given, just store the input arguments
		else
		{
			items=args;
		}

		int c=0, m=0;
		// parse the input command line arguments
		for (int i=0;i<items.length&&items[i]!=null;i++)
		{
            if(items[i].equals("-url"))
            {
                i++;
                URL=items[i];
            }
            else if(items[i].equals("-logbook"))
			{
				i++;
				LogBook=items[i];
			}
			else if(items[i].equals("-title"))
			{
				i++;
				title=items[i];
			}
			else if(items[i].equals("-body"))
			{
				i++;
				body=items[i];
			}
			else if(items[i].equals("-uid"))
			{
				i++;
				uid=items[i];
			}
			else if(items[i].equals("-password"))
			{
				i++;
				passwd=items[i];
			}
			else if(items[i].equals("-file"))
			{
				i++;
				final String image =items[i];
			      // Check if image is already in HashMap
                if (images.containsKey(image)) {
                    // get number of occurrences for this word
                    // increment it
                    // and put back again with dup appended
                    images.put(image+"dup",m++);
                }
				// store the image names in a hashmap to coincide with the captions
                else images.put(image, m++);
			}
			else if(items[i].equals("-caption"))
			{
				i++;
				final String text=items[i];
				// Store the captions in a hashmap to coincide with the image names.  If an does not have a caption,
				// null is entered.
				//while(c<m) captions.put(null,c++);
				captions.put(text,c++);
			}
		}
		try
		{
			// if the uid and password are not entered on the command line read it from a file
			if(uid.length()==0 || passwd.length()==0)
			{
				// find needed rdb values
				final FileReader fr = new FileReader(System.getenv("OPI_COMMON") +"/.rdb_vars");

				BufferedReader br = new BufferedReader(fr);
				String record = new String();
				try
				{
					record = br.readLine();
				}
				catch (IOException ex)
				{
					System.out.println(ex.getMessage());
					return;
				}
				// retrieve the uid and password
				if(record != null)  {
					uid = record.substring(0,record.indexOf(" "));
					passwd= record.substring(record.indexOf(" ")+1);
				}

			}
		     // First we're getting values array
	        ArrayList<Integer> values = new ArrayList<Integer>();
	        values.addAll(images.values());

			final String[] imageKeys = new String[images.size()];
			final String[] captionKeys=new String[captions.size()];;

			int cndx=0;
			// store the input image names and captions
			if(images.size()>0)
			{
				Iterator<Entry<String, Integer>> iterator = images.entrySet().iterator();
				while(iterator.hasNext())
				{
					Map.Entry e = (Map.Entry)iterator.next() ;
					final String key = ltrim((String) e.getKey());
					String imageKey=key;
					Integer val = (Integer) e.getValue();
                    if(key.substring(key.length()-3).equals("dup"))
                    	imageKey=key.substring(0,key.length()-3);
					if(val==images.size() && val>0)  imageKeys[val-1]=imageKey;
					else imageKeys[val]=imageKey;

					if(cndx < captions.size())
					{
						Iterator<Entry<String, Integer>> Citerator = captions.entrySet().iterator();
						while(Citerator.hasNext())
						{
							Map.Entry cEnt = (Map.Entry)Citerator.next() ;
							String Ckey = (String) cEnt.getKey();
							Integer Cval = (Integer) cEnt.getValue();

							if(Cval==val)
							{
								if(Ckey!=null) Ckey=ltrim(Ckey);
								captionKeys[Cval]=Ckey;
								cndx++;
							}
						}
					}
				}
			}

			// send the entry to the elog
			sendToElog(LogBook, uid, passwd, title, body, imageKeys, captionKeys);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return;
		}

	}

    /* remove leading whitespace */
	private static String ltrim(String source)
	{
		return source.replaceAll("^\\s+", "");
	}

	private static void sendToElog(final String LogBook, final String uid, final String passwd,
			final String title, final String body, final String images[], final String captions[]) throws Exception
	{
	    try
	    (
            final ELog elog = new ELog(URL, uid, passwd);
        )
	    {
    	    long entry = elog.createEntry(LogBook, title, body, ELogPriority.Normal);
    	    int pics = Math.min(images.length, captions.length);
    	    for (int i=0; i<pics; ++i)
    	        elog.addAttachment(entry, images[i], captions[i], new FileInputStream(images[i]));
	    }
	}

}
