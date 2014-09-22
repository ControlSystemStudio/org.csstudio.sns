/*
 *  ConnectionDialog.java
 *
 *  Created on Fri Feb 20 15:15:21 EST 2004
 *
 *  Copyright (c) 2004 Spallation Neutron Source
 *  Oak Ridge National Laboratory
 *  Oak Ridge, TN 37830
 */
package org.csstudio.mps.sns.tools.database;

import java.util.logging.*;
import java.io.File;
import java.io.IOException;
import java.net.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.*;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.sql.Connection;


/**
 * ConnectionDialog displays a dialog allowing the user to supply the database URL, their user
 * ID and their password. A connection dictionary is returned to the user based on their input.
 * @author   tap
 */
public class ConnectionDialog extends JDialog {
	/** label for the submit button */
	protected final String SUBMIT_LABEL;

	/** the connection dictionary selected by the user */
	protected ConnectionDictionary _dictionary;
	
	/** file chooser for browsing to a connection dictionary */
	protected JFileChooser _dictionaryBrowser;
	
	/** field for entering the adaptor specification */
	protected JTextField _adaptorField;
	
	/** field for entering the database URL */
	protected JTextField _URLField;
	
	/** field for entering the user's ID */
	protected JTextField _userField;
	
	/** field for entering the user's Password */
	protected JPasswordField _passwordField;


	/**
	 * Primary Constructor
	 * @param owner        The frame which owns this dialog window.
	 * @param dictionary   The initial connection dictionary.
	 * @param submitLabel  The label to use for the submit button.
	 */
	protected ConnectionDialog( Frame owner, final ConnectionDictionary dictionary, final String submitLabel ) {
		super( owner, "Connection Dialog", true );

		SUBMIT_LABEL = submitLabel;
		setup( dictionary );
	}


	/**
	 * Constructor with a default submit button label of "Connect".
	 * @param owner       The frame which owns this dialog window.
	 * @param dictionary  The initial connection dictionary.
	 */
	protected ConnectionDialog( Frame owner, final ConnectionDictionary dictionary ) {
		this( owner, dictionary, "Submit" );
	}


	/**
	 * Constructor with the default submit button label and an empty connection dictionary.
	 * @param owner  The frame which owns this dialog window.
	 */
	protected ConnectionDialog( Frame owner ) {
		this( owner, new ConnectionDictionary() );
	}


	/**
	 * Primary Constructor
	 * @param owner        The dialog which owns this dialog window.
	 * @param dictionary   The initial connection dictionary.
	 * @param submitLabel  The label to use for the submit button.
	 */
	protected ConnectionDialog( Dialog owner, final ConnectionDictionary dictionary, final String submitLabel ) {
		super( owner, "Connection Dialog", true );
		
		SUBMIT_LABEL = submitLabel;
		setup( dictionary );
	}


	/**
	 * Constructor with a default submit button label of "Connect".
	 * @param owner        The dialog which owns this dialog window.
	 * @param dictionary  The initial connection dictionary.
	 */
	protected ConnectionDialog( Dialog owner, final ConnectionDictionary dictionary ) {
		this( owner, dictionary, "Submit" );
	}


	/**
	 * Constructor with the default submit button label and an empty connection dictionary.
	 * @param owner        The dialog which owns this dialog window.
	 */
	protected ConnectionDialog( Dialog owner ) {
		this( owner, new ConnectionDictionary() );
	}
	
	
	/**
	 * Common initialization.
	 * @param dictionary   The initial connection dictionary.
	 */
	protected void setup( final ConnectionDictionary dictionary ) {
		_dictionary = null;

		final ConnectionDictionary baseDictionary = ( dictionary != null ) ? dictionary : new ConnectionDictionary();

		makeContent();
		loadDictionary( baseDictionary );
	}
	
	
	/**
	 * Get the connection dictionary file browser and make one if one doesn't already exist.
	 * @return the connection dictionary file browser
	 */
	protected JFileChooser getDictionaryBrowser() {
		if ( _dictionaryBrowser == null ) {
			_dictionaryBrowser = new JFileChooser();
			_dictionaryBrowser.setMultiSelectionEnabled( false );
			_dictionaryBrowser.setFileFilter( new FileFilter() {
				public boolean accept( final File file ) {
					return file.toString().endsWith( ".properties" );
				}
				public String getDescription() {
					return "Connection Dictionary";
				}
			});
			
			try {
				final URL defaultURL = ConnectionDictionary.getDefaultDictionaryURL();
				if ( defaultURL != null ) {
					_dictionaryBrowser.setSelectedFile( new File( defaultURL.getPath() ) );
				}				
			}
			catch( MalformedURLException exception ) {
				exception.printStackTrace();
			}
		}
		
		return _dictionaryBrowser;
	}
	
	
	/** Display the connection dictionary browser */
	protected void showDictionaryBrowser() {
		final JFileChooser browser = getDictionaryBrowser();
		final int status = browser.showOpenDialog( this );
		switch( status ) {
			case JFileChooser.CANCEL_OPTION:
				break;
			case JFileChooser.APPROVE_OPTION:
				final File selection = browser.getSelectedFile();
				if ( selection != null ) {
					try {
						loadDictionary( new ConnectionDictionary( selection ) );
					}
					catch( IOException exception ) {
						exception.printStackTrace();
					}
				}
				break;
			default:
				break;
		}
	}
	
	
	/**
	 * Load the specified connection dictionary.
	 * @param dictionary the connection dictionary to load
	 */
	public void loadDictionary( final ConnectionDictionary dictionary ) {
		String adaptorClass = null;
		try {
			final DatabaseAdaptor adaptor = dictionary.getDatabaseAdaptor();
			adaptorClass = ( adaptor != null ) ? adaptor.getClass().getName() : null;
		}
		catch ( Exception exception ) {
			Logger.getLogger("global").log( Level.SEVERE, "Error constructing dialog contents.", exception );
		}
		
		_adaptorField.setText( adaptorClass );		
		_URLField.setText( dictionary.getURLSpec() );
		_userField.setText( dictionary.getUser() );
		_passwordField.setText( dictionary.getPassword() );
	}


	/**
	 * Get the connection user's dictionary.
	 * @return   the user's connection dictionary
	 */
	public ConnectionDictionary getConnectionDictionary() {
		return _dictionary;
	}


	/**
	 * Show the connection dialog
	 * @return   The connection dictionary based on user input
	 */
	protected ConnectionDictionary showDialog() {
		setLocationRelativeTo( getOwner() );
		setVisible( true );
		return _dictionary;
	}


	/**
	 * Attempt to connect to the database using the supplied database adaptor and the connection
	 * dictionary specified by the user via the dialog box.
	 * @param databaseAdaptor  the database adaptor to use for the connection
	 * @return                 the new connection or null if the user canceled the dialog
	 */
	public Connection showConnectionDialog( final DatabaseAdaptor databaseAdaptor ) {
		ConnectionDictionary dictionary = showDialog();

		// check if the user cancelled the dialog
		if ( dictionary == null ) {
			return null;
		}

		try {
			return databaseAdaptor.getConnection( dictionary );
		}
		catch ( Exception exception ) {
			JOptionPane.showMessageDialog( getOwner(), exception.getMessage(), "Connection Error!", JOptionPane.ERROR_MESSAGE );
			Logger.getLogger("global").log( Level.SEVERE, "Database connection error.", exception );
			return showConnectionDialog( (JFrame)getOwner(), databaseAdaptor, _dictionary );
		}
	}


	/**
	 * Display the dialog and return the connection dictionary.
	 * @param owner  The window that owns dialog box
	 * @return       The connection dictionary based on user input
	 */
	public static ConnectionDictionary showDialog( final Frame owner ) {
		return new ConnectionDialog( owner ).showDialog();
	}


	/**
	 * Display the dialog and return the connection dictionary. Initialize the new connection
	 * dictionary with the supplied one except that we ignore the password.
	 * @param owner       The window that owns dialog box
	 * @param dictionary  The dictionary from which to initialize the new connection dictionary
	 * @return            The connection dictionary based on user input
	 */
	public static ConnectionDictionary showDialog( final Frame owner, final ConnectionDictionary dictionary ) {
		return new ConnectionDialog( owner, dictionary ).showDialog();
	}


	/**
	 * Display the dialog and return the connection dictionary. Initialize the new connection
	 * dictionary with the supplied one except that we ignore the password.
	 * @param owner        The window that owns dialog box
	 * @param dictionary   The dictionary from which to initialize the new connection dictionary
	 * @param submitLabel  The label to use for the submit button
	 * @return             The connection dictionary based on user input
	 */
	public static ConnectionDictionary showDialog( final Frame owner, final ConnectionDictionary dictionary, final String submitLabel ) {
		return new ConnectionDialog( owner, dictionary, submitLabel ).showDialog();
	}


	/**
	 * Display the dialog and return the connection dictionary. Initialize the new connection
	 * dictionary with the supplied one except that we ignore the password.
	 * @param owner            The window that owns dialog box
	 * @param databaseAdaptor  The database adaptor to use to make the connection
	 * @param dictionary       The connection dictionary from which to initialize the new connection dictionary
	 * @return                 The connection dictionary based on user input
	 */
	public static Connection showConnectionDialog( final Frame owner, final DatabaseAdaptor databaseAdaptor, final ConnectionDictionary dictionary ) {
		return getInstance( owner, dictionary ).showConnectionDialog( databaseAdaptor );
	}


	/**
	 * Display the dialog and return the connection dictionary. Start with an empty connection dictionary.
	 * @param owner            The window that owns dialog box
	 * @param databaseAdaptor  The database adaptor to use to make the connection
	 * @return                 The connection dictionary based on user input
	 */
	public static Connection showConnectionDialog( final Frame owner, final DatabaseAdaptor databaseAdaptor ) {
		return showConnectionDialog( owner, databaseAdaptor, new ConnectionDictionary() );
	}


	/**
	 * Get a new instance of the connection dialog.
	 * @param owner       The window that owns the new connection dialog box
	 * @param dictionary  The connection dictionary from which to initialize the new connection dictionary
	 * @return            A new instance of the connection dialog
	 */
	public static ConnectionDialog getInstance( final Frame owner, final ConnectionDictionary dictionary ) {
		return new ConnectionDialog( owner, dictionary, "Connect" );
	}


	/**
	 * Get a new instance of the connection dialog.
	 * @param owner       The window that owns the new connection dialog box
	 * @param dictionary  The connection dictionary from which to initialize the new connection dictionary
	 * @return            A new instance of the connection dialog
	 */
	public static ConnectionDialog getInstance( final Dialog owner, final ConnectionDictionary dictionary ) {
		return new ConnectionDialog( owner, dictionary, "Connect" );
	}


	/** Make the Dialog content */
	protected void makeContent() {
		setSize( 400, 130 );
		getContentPane().setLayout( new BorderLayout() );
		Box mainView = new Box( BoxLayout.Y_AXIS );
		getContentPane().add( mainView );

		Dimension fieldSize;

		Box adaptorBox = new Box( BoxLayout.X_AXIS );
		_adaptorField = new JTextField( 30 );
		fieldSize = _adaptorField.getPreferredSize();
		_adaptorField.setMinimumSize( fieldSize );
		_adaptorField.setMaximumSize( fieldSize );
		adaptorBox.add( Box.createGlue() );
		adaptorBox.add( new JLabel( "Adaptor (optional): " ) );
		adaptorBox.add( _adaptorField );
		mainView.add( adaptorBox );

		Box urlBox = new Box( BoxLayout.X_AXIS );
		_URLField = new JTextField( 30 );
		fieldSize = _URLField.getPreferredSize();
		_URLField.setMinimumSize( fieldSize );
		_URLField.setMaximumSize( fieldSize );
		urlBox.add( Box.createGlue() );
		urlBox.add( new JLabel( "URL: " ) );
		urlBox.add( _URLField );
		mainView.add( urlBox );

		Box userBox = new Box( BoxLayout.X_AXIS );
		_userField = new JTextField( 20 );
		_userField.setMinimumSize( fieldSize );
		_userField.setMaximumSize( fieldSize );
		userBox.add( Box.createGlue() );
		userBox.add( new JLabel( "User: " ) );
		userBox.add( _userField );
		mainView.add( userBox );

		Box passBox = new Box( BoxLayout.X_AXIS );
		passBox.add( Box.createGlue() );
		passBox.add( new JLabel( "Password: " ) );
		_passwordField = new JPasswordField( 20 );
		fieldSize = _passwordField.getPreferredSize();
		_passwordField.setMinimumSize( fieldSize );
		_passwordField.setMaximumSize( fieldSize );
		passBox.add( _passwordField );
		mainView.add( passBox );
		mainView.add( Box.createGlue() );

		Box buttonBox = new Box( BoxLayout.X_AXIS );
		mainView.add( buttonBox );
		buttonBox.add( Box.createGlue() );

		JButton cancelButton = new JButton( "Cancel" );
		buttonBox.add( cancelButton );
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( final ActionEvent event ) {
				setVisible( false );
				dispose();
			}
		} );
		
		JButton browseButton = new JButton( "Browse..." );
		buttonBox.add( browseButton );
		browseButton.addActionListener( new ActionListener() {
			public void actionPerformed( final ActionEvent event ) {
				showDictionaryBrowser();
			}
		});
		
		JButton submitButton = new JButton( SUBMIT_LABEL );
		getRootPane().setDefaultButton( submitButton );
		buttonBox.add( submitButton );
		submitButton.addActionListener( new ActionListener() {
			public void actionPerformed( final ActionEvent event ) {
				_dictionary = new ConnectionDictionary();

				if ( _userField.getText() != null ) {
					_dictionary.setUser( _userField.getText() );
				}
				if ( _passwordField.getPassword() != null ) {
					_dictionary.setPassword( String.valueOf( _passwordField.getPassword() ) );
				}
				if ( _URLField.getText() != null ) {
					_dictionary.setURLSpec( _URLField.getText() );
				}
				if ( _adaptorField.getText() != null ) {
					_dictionary.setDatabaseAdaptorClass( _adaptorField.getText() );
				}
				setVisible( false );
				dispose();
			}
		} );

		pack();
		setResizable(false);
	}
}

