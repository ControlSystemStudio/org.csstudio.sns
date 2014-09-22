#!/usr/bin/env jython

# this script demonstrates how to load a windows from a bricks resource file, configure the components with data and display those windows

import sys

from jarray import *
from java.lang import *
from java.awt import *
from java.util import *
from java.lang.reflect import *
from javax.swing.event import *
from javax.swing import *
from java.awt.event import *
from java.net import *
from java.io import *

from org.csstudio.mps.sns.tools.bricks import WindowReference
from org.csstudio.mps.sns.tools.plot import *


# python Java additions
true = (1==1)
false = not true
null = None


# locate the sample folder and get the bricks file within it
sample_folder = File( sys.argv[0] ).getParentFile()
url = File( sample_folder, "test.bricks" ).toURL()
print url

# generate a window reference resource and pass the desired constructor arguments
window_ref = WindowReference( url, "MainWindow", ["Test Title"] )

# get the main window
window = window_ref.getWindow()

# get the magnet list and populate it with data
magnet_list = window_ref.getView( "MagnetList" );
magnets = Vector()
magnets.add( "Dipole" )
magnets.add( "Quadrupole" )
magnets.add( "Sextupole" )
magnets.add( "Octupole" )
magnets.add( "Skew Dipole" )
magnets.add( "Skew Quadrupole" )
magnets.add( "Skew Sextupole" )
magnet_list.setListData( magnets )

# get the plot component and add the data
plot = window_ref.getView( "SinePlot" )
graphData = BasicGraphData();
graphData.setGraphColor( Color.BLUE );
graphData.setGraphProperty( plot.getLegendKeyString(), "Sine" );
for x in range( 100 ):
	graphData.addPoint( x, Math.sin( 0.1 * x ) );
series = Vector(1);
series.add( graphData );
plot.addGraphData( series );


# define the dialog Okay action handler
class DialogOkayAction( ActionListener ):
	def __init__( self, dialog ):
		self.dialog = dialog
		
	def actionPerformed( self, event ):
		self.dialog.setVisible( false )


# define the quit button action handler
class QuitAction( ActionListener ):
	def actionPerformed( self, event ):
		sys.exit( 0 )


# define the run action handler
class RunAction( ActionListener ):
	def actionPerformed( self, event ):
		# create the dialog box with the main window as the dialog box's owner
		dialog_ref = WindowReference( url, "HelloDialog", [window] )

		dialog = dialog_ref.getWindow()
		
		button = dialog_ref.getView( "OkayButton" )
		button.addActionListener( DialogOkayAction( dialog ) )
		
		dialog.setLocationRelativeTo( window )
		dialog.setVisible( true )


# get the run button and configure it with the run action
runButton = window_ref.getView( "RunButton" )
runButton.addActionListener( RunAction() )

# get the quit button and configure it with the quit action
quitButton = window_ref.getView( "QuitButton" )
quitButton.addActionListener( QuitAction() )

# display the main window
window.setVisible( true )

