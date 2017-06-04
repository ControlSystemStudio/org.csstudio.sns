# Build all the SNS products
#
# Requires that these repositories have all been checked out from https://github.com/ControlSystemStudio
# into the same parent directory:
#
#   for repo in diirt maven-osgi-bundles cs-studio-thirdparty cs-studio org.csstudio.sns
#   do
#        git clone https://github.com/ControlSystemStudio/${repo}.git
#   done
#
#
# In addition, the environment varaibles shown in setup.sh need to be set.
#
# Invoking this file will then build CSS using local maven settings.xml
# and a P2 composite repo built as a result of compiling diirt etc. locally.

if [ ! -x "$JAVA_HOME/bin/java" ]
then
    echo "Missing JAVA_HOME"
    exit 1
fi

if [ ! -x "$M2_HOME/bin/mvn" ]
then
    echo "Missing M2_HOME"
    exit 1
fi

if [ ! -d "$SRC_DIR/org.csstudio.sns/css_repo" ]
then
    echo "Missing SRC_DIR"
    exit 1
fi

MSET="`pwd`/settings.xml"
if [ ! -r $MSET ]
then
    echo "Missing maven settings"
    exit 1
fi

export M2=$M2_HOME/bin
export PATH=$M2:$JAVA_HOME/bin:$PATH

OPTS="-s $MSET --offline clean verify"
OPTS="-s $MSET clean verify"

mvn -version

# sleep 5

cd ../..
TOP=`pwd`

# rm -rf ~/.m2/repository
# rm -f 0_diirt.log 1_maven-osgi-bundles.log 2_cs-studio-thirdparty.log  3_core.log 4_applications.log 5_display_builder.log 6_sns.log

# With download: Total time: 02:40 min
# (cd diirt; time  mvn $OPTS ) 2>&1 | tee 0_diirt.log

# With download: Total time: 04:36 min
# (cd maven-osgi-bundles; time  mvn $OPTS ) 2>&1 | tee 1_maven-osgi-bundles.log

# With download: Total time: 03:00 min
# (cd cs-studio-thirdparty; time  mvn $OPTS ) 2>&1 | tee 2_cs-studio-thirdparty.log

# With download: Total time: 07:12 min
# (cd cs-studio/core; time  mvn $OPTS ) 2>&1 | tee 3_core.log

# With download: Total time: 12:24 min
# (cd cs-studio/applications; time  mvn $OPTS ) 2>&1 | tee 4_applications.log

# display.builder pom.xml looks for $CSS_REPO
# CSS_REPO=file:$TOP/org.csstudio.sns/css_repo
# (cd org.csstudio.display.builder; time mvn -s $MSET -Dcss-repo=$CSS_REPO clean verify ) 2>&1 | tee 5_display_builder.log

# With download: 
(cd org.csstudio.sns; time  mvn $OPTS ) 2>&1 | tee 6_sns.log

# Products now under org.csstudio.sns/repository/target/products/*
# Feature repo under org.csstudio.sns/repository/target/repository

