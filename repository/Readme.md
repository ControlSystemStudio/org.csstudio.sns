Maven/Tycho Build
=================

Products work OK..


Eclipse IDE
===========

As of Sept. 2016, the products should run by simply
opening the *.product file, then pressing "Synchronize"
and "Launch an Eclipse application".

In the past, product's Run Configuration often needed manual adjustments,
which can be obtained by comparing Help/About/../plugins of
the Maven-generated product:

 * org.eclipse.core.jobs - Select 3.6.0 instead of 3.6.1
 * javax.servlet - Select 3.0.0 (and the already selected 3.1.0)
 * org.eclipse.jetty.servlet - 8.1.14 (NOT 8.1.16)
 * Select all *fx* plugins


Quirk
-----

Once saw org.csstudio.opibuilder.rcp included, but start level wasn't "default".
De-selected, then re-selected.
