#!/bin/sh
#
# Example for collecting the files for a "target" platform,
# i.e. the same files which we now get via maven - on a good day.

mkdir -p tgt
cd tgt

# Collect all plugins, for all architectures
cp -r ../../repository/target/products/org.csstudio.sns.product.product/linux/gtk/x86_64/sns-css-4.2.0/plugins/* .
cp -r ../../repository/target/products/org.csstudio.sns.product.product/linux/gtk/x86/sns-css-4.2.0/plugins/* .
cp -r ../../repository/target/products/org.csstudio.sns.product.product/win32/win32/x86/sns-css-4.2.0/plugins/* .
cp -r ../../repository/target/products/org.csstudio.sns.product.product/macosx/cocoa/x86_64/Css4.2.0.app/Contents/Eclipse/plugins/* .

for p in *
do
  if [ -r ~/Eclipse/4.5.2/Eclipse.app/Contents/Eclipse/plugins/$p ]
  then
     echo "$p - IDE"
     rm -rf $p
  fi
  
  C=`find ../../../cs-studio -name $p | wc -l`
  if [ $C -gt 0 ]
  then
      echo "$p - cs-studio"
      rm -rf $p
  fi

  case $p
  in
  *sns*)
      echo "$p - SNS"
      rm -rf $p
  ;;
  org.csstudio.diag.pvutil_*)
      echo "$p - SNS"
      rm -rf $p
  ;;
  org.csstudio.diag.rack_*)
      echo "$p - SNS"
      rm -rf $p
  ;;
  esac
done