# This file demonstrates how to use the XmlDataAdaptor class to create a new XML
# document with nested nodes representing a set of data, write the document to a file 
# and then read the document back into data structures.
# The data used in the example is just for demonstration, only.  The data is not official or current and 
# no attempt was made to verify its accuracy.  The source of the data is the 1994 Particle Physics Booklet
# published by the Particle Data Group.

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
particle_adaptor = doc_adaptor.createChild("particles")		# there can only be one child of the document

# add the data to the data adaptor
boson_adaptor = particle_adaptor.createChild("statistics")
boson_adaptor.setValue("kind", "boson")
boson_adaptor.setValue("spin", 1.0)
fermion_adaptor = particle_adaptor.createChild("statistics")
fermion_adaptor.setValue("kind", "fermion")
fermion_adaptor.setValue("spin", "0.5")

# define the Guage bosons
wp_adaptor = boson_adaptor.createChild("boson")
wp_adaptor.setValue("name", "W+")
wp_adaptor.setValue("mass", 80.2)
wp_adaptor.setValue("charge", 1)
wn_adaptor = boson_adaptor.createChild("boson")
wn_adaptor.setValue("name", "W-")
wn_adaptor.setValue("mass", 80.2)
wn_adaptor.setValue("charge", -1)
z_adaptor = boson_adaptor.createChild("boson")
z_adaptor.setValue("name", "Z")
z_adaptor.setValue("mass", 91.2)
z_adaptor.setValue("charge", 0)
photon_adaptor = boson_adaptor.createChild("boson")
photon_adaptor.setValue("name", "photon")
photon_adaptor.setValue("mass", 0)
photon_adaptor.setValue("charge", 0)
gluon_adaptor = boson_adaptor.createChild("boson")
gluon_adaptor.setValue("name", "gluon")
gluon_adaptor.setValue("mass", 0)
gluon_adaptor.setValue("charge", 0)

# define fermions
quark_adaptor = fermion_adaptor.createChild("fermion")
quark_adaptor.setValue("type", "quark")
lepton_adaptor = fermion_adaptor.createChild("fermion")
lepton_adaptor.setValue("type", "lepton")

# define quarks
top_adaptor = quark_adaptor.createChild("quark")
top_adaptor.setValue("name", "top")
bottom_adaptor = quark_adaptor.createChild("quark")
bottom_adaptor.setValue("name", "bottom")
strange_adaptor = quark_adaptor.createChild("quark")
strange_adaptor.setValue("name", "strange")
charm_adaptor = quark_adaptor.createChild("quark")
charm_adaptor.setValue("name", "charm")
up_adaptor = quark_adaptor.createChild("quark")
up_adaptor.setValue("name", "up")
down_adaptor = quark_adaptor.createChild("quark")
down_adaptor.setValue("name", "down")

# define leptons
e_adaptor = lepton_adaptor.createChild("lepton")
e_adaptor.setValue("name", "electron")
muon_adaptor = lepton_adaptor.createChild("lepton")
muon_adaptor.setValue("name", "muon")
tau_adaptor = lepton_adaptor.createChild("lepton")
tau_adaptor.setValue("name", "tau")
e_nu_adaptor = lepton_adaptor.createChild("lepton")
e_nu_adaptor.setValue("name", "electron neutrino")
muon_nu_adaptor = lepton_adaptor.createChild("lepton")
muon_nu_adaptor.setValue("name", "muon neutrino")
tau_nu_adaptor = lepton_adaptor.createChild("lepton")
tau_nu_adaptor.setValue("name", "tau neutrino")


# write the XML document to a file
doc_adaptor.writeTo( File("particles.xml") )

# read an XML document from a file without DTD validation
source = XmlDataAdaptor.adaptorForFile( File("particles.xml"), false )
# fetch the quarks and print their names
particles = source.childAdaptor("particles")
stats = particles.childAdaptors("statistics")
for stat in stats:
	fermions = stat.childAdaptors("fermion")
	for fermion in fermions:
		quarks = fermion.childAdaptors("quark")
		for quark in quarks:
			print "read quark: ", quark.stringValue("name")

# exit the script
sys.exit()
