package applet;

import checker.PrivateKeyChecker;
import java.applet.Applet;
import java.security.PrivateKey;

public class InfosecApplet extends Applet {
    public static byte[] sign(String password, byte[] privateKeyBytes, byte[] randomBytes) {
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        System.out.println("Password: " + password);
        System.out.println("File: " + new String(privateKeyBytes));
        System.out.println("Random: " + new String(randomBytes));
        PrivateKey privateKey;
        String signature = "";
        try {
            privateKey = privateKeyChecker.decryptPrivateKey(privateKeyBytes, password);
            System.out.println("PrivateKey: " + privateKey.toString());
            return privateKeyChecker.sign(privateKey, randomBytes);
        } catch (Exception e) {
            System.out.println("Error!");
            e.printStackTrace();
        }
        return new byte[]{0};
    }
}
