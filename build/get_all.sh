GH=https://github.com/
for i in ControlSystemStudio/diirt ControlSystemStudio/maven-osgi-bundles ControlSystemStudio/cs-studio-thirdparty ControlSystemStudio/cs-studio kasemir/org.csstudio.display.builder ControlSystemStudio/org.csstudio.sns
do
  D=`basename $i`
  if [ -d $D ]
  then
    echo "==== Updating $D ===="
    (cd $D; git pull; git branch)
  else
    echo "==== Fetching $D ===="
    git clone $GH/$i
  fi
done


