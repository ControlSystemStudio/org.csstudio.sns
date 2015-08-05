This used to be Jeri/XAL code that is no longer maintained.
-> A current version of XAL can be found in the Open XAL project, http://xaldev.sourceforge.net.
   See there for details on its BSD-type license.

It was copied pretty much "as is" and now can be started via a CSS menu entry.
It is, however, still AWT/SWING code that simply runs in parallel to CSS,
using the SWT/AWT bridge.
On Mac OS X, it will not run at all.

The database connection is configured in the file 
src/org/csstudio/mps/sns/resources/rdb.properties
which is handled by the code in org.csstudio.mps.sns.JeriDocument#makeMainWindow()
and JeriDocument#findAdaptor().

The setup uses Service Names (originally, it used Database Names).

The standalone mpsbrowser.jar can be created by once running
org.csstudio.mps.sns/src/org/csstudio/mps/sns/model/MPSModel.java
in the Eclipse IDE, then 'Export'ing it as a runnable jar file.
org.csstudio.mps.sns