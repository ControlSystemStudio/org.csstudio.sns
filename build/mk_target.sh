#!/bin/sh
#
# Turn target_platform with plugins/ and features/ into P2 repo.

ECLIPSE=~/Eclipse/4.5.2/Eclipse.app/Contents/Eclipse

LAUNCHER=`echo "${ECLIPSE}/plugins/org.eclipse.equinox.launcher_*.jar"`

TARGET_PLATFORM=`pwd`/target_platform

echo "Creating P2 repository for $TARGET_PLATFORM"
rm -f $TARGET_PLATFORM/artifacts.xml $TARGET_PLATFORM/content.xml
java -jar $LAUNCHER \
   -application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher \
   -source $TARGET_PLATFORM \
   -metadataRepository file:/$TARGET_PLATFORM \
   -artifactRepository file:/$TARGET_PLATFORM \
   -publishArtifacts \
   -configs cocoa.macosx.x86_64

#   -compress \
#   -configs gtk.linux.x86

echo "Contents of repository:"
java -jar $LAUNCHER \
   -application org.eclipse.equinox.p2.director \
   -repository file:/$TARGET_PLATFORM \
   -list
