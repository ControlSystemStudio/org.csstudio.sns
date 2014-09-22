# This script demonstrates how to populate a table, write it to a file and read it back from a file.

import sys

from jarray import *
from java.lang import *
from java.io import *
from java.util import *

from org.csstudio.mps.sns.tools.data import *
from org.csstudio.mps.sns.tools.xml import *

# define a table schema and create the table
attributes = ArrayList()
attributes.add( DataAttribute("x", Double(0).getClass(), 0) )		# in Java just use Double.class
attributes.add( DataAttribute("y", String("").getClass(), 0) )		# in Java just use String.class
attributes.add( DataAttribute("z", Integer(0).getClass(), 1) )		# in Java just use Integer.class
table = DataTable("TEST_TABLE", attributes)

# An edit context may consist of one or more table groups.  A table group is simply a unique 
# string and has a one to one mapping with a file.  So an edit context can have many sources each one 
# represented by a table group.  A table group can have one or more tables associated with it. In this 
# way you can save multiple tables in a single XML file.

# create an edit context and assign the table to a table group
edit_context = EditContext()
edit_context.addTableToGroup(table, "TEST_GROUP")

# add some records to our table
record = GenericRecord(table)
record.setValueForKey(3.14, "x")
record.setValueForKey("test", "y")
record.setValueForKey(0, "z")
table.add(record)

record = GenericRecord(table)
record.setValueForKey(2.72, "x")
record.setValueForKey("second", "y")
record.setValueForKey(1, "z")
table.add(record)

record = GenericRecord(table)
record.setValueForKey(1.5, "x")
record.setValueForKey("third record", "y")
record.setValueForKey(2, "z")
table.add(record)

# write the table group to a file
XmlTableIO.writeTableGroupToFile(edit_context, "TEST_GROUP", File("xyz_table.tml"))


# read a table from the file
new_context = EditContext()
XmlTableIO.readTableGroupFromFile(new_context, "TEST_GROUP", File("xyz_table.tml"))
print "Table records read from file: ", table.records()


# exit the script
sys.exit()
