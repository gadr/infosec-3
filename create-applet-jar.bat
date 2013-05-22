#!/bin/bash
jar cfv public/signatureApplet.jar -C target/scala-2.10/classes/ applet/InfosecApplet.class -C target/scala-2.10/classes/ checker/PrivateKeyChecker.class -C lib/commons-io-2.4/ .
jar cfv public/filesApplet.jar -C target/scala-2.10/classes/ applet/InfosecFilesApplet.class -C target/scala-2.10/classes/ checker/PrivateKeyChecker.class -C lib/commons-io-2.4/ .
