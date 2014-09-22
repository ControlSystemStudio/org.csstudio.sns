# This file demonstrates how to use the XmlDataAdaptor class to create a new simple, single 
# node XML document from a set of data, write it to file and then read the data back.
# This script is ideal for beginners as it demonstrates just the very basics for creating, 
# writing and reading XML documents.

import sys

from jarray import *
from java.lang import *
from java.util import *
from java.io import *

from org.csstudio.mps.sns.tools.xml import *

# java definitions
false = 0
true = not false

# create a new XML data adaptor
doc_adaptor = XmlDataAdaptor.newEmptyDocumentAdaptor()

# add the data to the data adaptor
data = doc_adaptor.createChild("test")
data.setValue("x", 10)
data.setValue("y", "abc")
data.setValue("z", 3.14)

# write the XML document to a file
doc_adaptor.writeTo( File("xyz_test.xml") )

# read an XML document from a file without DTD validation
read_adaptor = XmlDataAdaptor.adaptorForFile( File("xyz_test.xml"), false )
read_data = read_adaptor.childAdaptor("test")
print "x value read: ", read_data.intValue("x")
print "y value read: ", read_data.stringValue("y")
print "z value read: ", read_data.doubleValue("z")

# exit the script
sys.exit()
