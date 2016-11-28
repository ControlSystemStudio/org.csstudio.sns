cs-studio-SNS
=============

SNS specific additions and build for CS-Studio


Example for compiling the SNS products
--------------------------------------

 1) Somewhere on your computer, execute what's in build/get_all.sh .
    As a result, you should have a directory with these sub-dirs:

    diirt
    maven-osgi-bundles
    cs-studio-thirdparty
    cs-studio
    org.csstudio.sns

 2) `cd org.csstudio.sns/build`
 
 3) Edit the file `setup.sh` to define your `JAVA_HOME` and `M2_HOME`
 
 4) `sh make.sh`
   Note that this build setup is self-contained.
   You can start out without any `~/.m2` directory.
   Maven is invoked with the `settings.xml` from `org.csstudio.sns/build`,
   which in turn enable a composite repository from `org.csstudio.sns/css_repo`
   that is configured as the combination of all the repositories that
   we are about to build locally: diirt, maven-osgi-bundles, etc.

As a result, you should find log files for the various build steps in the original directory:
`0_diirt.log`, `_maven-osgi-bundles.log`, `2_cs-studio-thirdparty.log`, `3_core.log`, `4_applications.log`, `5_sns.log`.

The directory `org.csstudio.sns/repository/target/products` will contain the binary products.
One of the UI product's /plugin folder can be used as a target platform for the Eclipse IDE.
`org.csstudio.sns/repository/target/repository` is a P2 repository from which the UI product
can install optional features.
