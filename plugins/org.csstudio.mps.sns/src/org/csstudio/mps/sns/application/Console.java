/*
 * Console.java
 *
 * Created on March 18, 2003, 1:42 PM
 */

package org.csstudio.mps.sns.application;

import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Container;


/**
 * The Console captures standard output and standard error streams.  Both are 
 * displayed in a console window.  One console serves the entire application.
 * Standard output appears in black text while standard error appears in red text.
 *
 * @author  t6p
 */
class Console {
    /** the console instance */
    final static protected Console _console;
    
    // stream variables
    final protected PrintStream _standardOut;
    final protected PrintStream _standardErr;
    final protected ConsoleOutHandler _outStream;
    final protected ConsoleErrHandler _errStream;
    
    // view variables
    protected JFrame _frame;
    protected boolean _neverShown;
    protected JTextPane _textView;
    protected Style _outStyle;
    protected Style _errStyle;
    protected DefaultStyledDocument _document;
    
	
    static {
        _console = new Console();
    }
    
	
    /** Constructor */
    public Console() {
        _neverShown = true;
        _outStream = new ConsoleOutHandler();
        _errStream = new ConsoleErrHandler();
        _standardOut = System.out;
        _standardErr = System.err;
		
        makeTextView();
        makeFrame();		
    }
    
    
    /** Make the frame for the console. */
    private void makeFrame() {
        _frame = new JFrame( "Console" );
        _frame.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
        _frame.setSize( 400, 300 );
        _frame.setTitle( Application.getAdaptor().applicationName() + " - Console" );
        _frame.getContentPane().setLayout( new BorderLayout() );
		
		generateContentsFor( _frame.getContentPane() );
    }
	
	
	/**
	 * Make contents and add them to the container.
	 * @param containter the container to which the contents are added.
	 */
	private void generateContentsFor( final Container container ) {
        final Box buttonBar = new Box( BoxLayout.X_AXIS );
        buttonBar.add( Box.createGlue() );
        JButton clearButton = new JButton( "Clear" );
		
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent event ) {
                try {
                    _document.remove( 0, _document.getLength() );
                }
                catch( Exception exception ) {
                }
            }
        });
		
        buttonBar.add( clearButton );
        container.add( buttonBar, "North" );
        
        Box box = new Box( BoxLayout.Y_AXIS );
        container.add( box, "Center" );
        
        JScrollPane scrollPane = new JScrollPane( _textView );
        box.add( scrollPane, "Center" );		
	}
    
    
    /**
     * Make the view that holds the text.  The text is set to be un-editable.
     */
    private void makeTextView() {
        StyleContext context = new StyleContext();
        _document = new DefaultStyledDocument( context );
        _textView = new JTextPane( _document );
        _textView.setEditable( false );
        
        _outStyle = context.addStyle( null, null );
        StyleConstants.setForeground( _outStyle, Color.black );
        
        _errStyle = context.addStyle( null, null );
        StyleConstants.setForeground( _errStyle, Color.red );
    }
    
    
    /**
     * Sets the console to capture standard output.
     */
    static void captureOutput() {
        System.setOut( new PrintStream( _console._outStream ) );
    }
    
    
    /**
     * Sets the console to capture standard error.
     */
    static void captureErr() {
        System.setErr( new PrintStream( _console._errStream ) );
    }
    
    
    /**
     * Show the console.  If the console has never been shown before, place it relative
     * to the sender, otherwise simply show it where it was last placed by the user.
     * However, if the console window is on a different screen than the sender, bring 
     * the console window back and display it relative to the sender.
	 * @param sender The component relative to which the console should be positioned
     */
    static void showNear( final java.awt.Component sender ) {
        if ( _console._neverShown ) {
            _console._frame.setLocationRelativeTo( sender );
            _console._neverShown = false;
        }
        else if ( !sender.getGraphicsConfiguration().getDevice().getIDstring().equals(_console._frame.getGraphicsConfiguration().getDevice().getIDstring()) ) {
            // if the console window is on a different screen bring it to the same screen as the sender
            _console._frame.setVisible( false );
            _console._frame.setLocationRelativeTo( sender );
        }
        _console._frame.setVisible( true );
    }
    
    
    /**
     * Hide the console.
     */
    static void hide() {
        _console._frame.setVisible( false );
    }
    
    
    
    /**
     * The internal class whose instance handles the output stream.  The output is 
     * inserted into the text pane's document as black text.
     */
    protected class ConsoleOutHandler extends OutputStream {
        /**
         * Write output to both standard out and the Console view
         * @param character The character to write
         */
        public void write( final int character ) {
            try {
                _standardOut.write( character );
                _document.insertString( _document.getLength(), String.valueOf( (char)character ), _outStyle );
            }
            catch( Exception excpt ) {
            }
        }
    }
    
    
    /**
     * The internal class whose instance handles the error stream.  The output is 
     * inserted into the text pane's document as red text.
     */
    protected class ConsoleErrHandler extends OutputStream {
        /**
         * Write output to both standard err and the Console view
         * @param character The character to write
         */
        public void write( final int character ) {
            try {
                _standardErr.write(character);
                _document.insertString( _document.getLength(), String.valueOf( (char)character ), _errStyle );
            }
            catch( Exception excpt ) {
            }
        }
    }
}
