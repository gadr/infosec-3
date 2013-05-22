#!/bin/bash
jar cfv public/infosecApplet.jar -C target/scala-2.10/classes/ applet/InfosecApplet.class -C target/scala-2.10/classes/ checker/PrivateKeyChecker.class -C target/scala-2.10/classes/ checker/DigitalSignatureChecker.class  -C target/scala-2.10/classes/ checker/EnvelopeChecker.class -C lib/commons-io-2.4/ .
