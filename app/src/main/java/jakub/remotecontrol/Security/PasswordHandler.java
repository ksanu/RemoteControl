package jakub.remotecontrol.Security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
//import org.apache.commons.codec.binary.Base64;
import android.util.Base64;

/**
 * @Source https://stackoverflow.com/questions/2860943/how-can-i-hash-a-password-in-java
 */
public class PasswordHandler {
    // The higher the number of iterations the more
    // expensive computing the hash is for us and
    // also for an attacker.
    private static final int iterations = 20*1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 128;

    /** Computes a salted PBKDF2 hash of given plaintext password
     suitable for storing in a database.
     Empty passwords are not supported. */
    public static String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        return new String(Base64.encode(salt, Base64.NO_WRAP)) + "$" + hash(password, salt).replaceAll("\\s+","");
    }

    /** Checks whether given plaintext password corresponds
     to a stored salted hash of the password. */
    public static boolean check(String password, String stored) throws Exception{
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException(
                    "The stored password have the form 'salt$hash'");
        }
        String hashOfInput = hash(password, Base64.decode(saltAndPass[0],Base64.NO_WRAP));
        return hashOfInput.equals(saltAndPass[1]);
    }

    public static String getSaltedHash(String password, String salt) throws Exception {
        byte[] mySalt =  Base64.decode(salt, Base64.NO_WRAP);
        return hash(password, mySalt);
    }
    public static String getStoredSalt(String stored)
    {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException(
                    "The stored password have the form 'salt$hash'");
        }
        return saltAndPass[0];
    }
    // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
    // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
    private static String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, iterations, desiredKeyLen)
        );
        return new String(Base64.encode(key.getEncoded(), Base64.NO_WRAP)).replaceAll("\\s+","");
    }
}
