package applet;

import checker.PrivateKeyChecker;
import java.applet.Applet;
import java.security.PrivateKey;

public class InfosecFilesApplet extends Applet {

    public static String getIndex(byte[] signature, byte[] envelope, byte[] file) {

        return "";
    }

    public static boolean getStatus(byte[] signature, byte[] envelope, byte[] file) {

        return true;
    }

    public static String getBase64DecryptedFile(byte[] signature, byte[] envelope, byte[] file) {

        return "";
    }

}
