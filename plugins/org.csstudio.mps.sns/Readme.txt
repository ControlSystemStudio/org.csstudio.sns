This used to be Jeri/XAL code that is no longer maintained.
-> A current version of XAL can be found in the Open XAL project, http://xaldev.sourceforge.net.
   See there for details on its BSD-type license.

It was copied pretty much "as is" and now can be started via a CSS menu entry.
It is, however, still AWT/SWING code that simply runs in parallel to CSS,
using the SWT/AWT bridge.
On Mac OS X, it will not run at all.

The database connection is configured in the file 
src/org/csstudio/mps/sns/resources/rdb.properties
which is handled by the code in JeriDocument#makeMainWindow()

