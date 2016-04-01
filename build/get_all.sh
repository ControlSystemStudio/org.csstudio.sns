GH=https://github.com/ControlSystemStudio/
for i in diirt maven-osgi-bundles cs-studio-thirdparty cs-studio org.csstudio.sns
do
  if [ -d $i ]
  then
    echo "==== Updating $i ===="
    (cd $i; git pull)
  else
    echo "==== Fetching $i ===="
    git clone $GH/$i
  fi
done


