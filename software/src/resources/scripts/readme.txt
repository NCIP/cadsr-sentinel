This folder contains any command scripts need by the product. The SH are shell scripts for UNIX based environments and the CMD are for Windows based environments.

The scripts depend on some property file like DSRAlert.properties which is built by ant, thus it is
mandatory to have the build completed properly/done before running the scripts.

To build:

ant build-product

or just

ant