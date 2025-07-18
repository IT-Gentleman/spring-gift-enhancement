package gift.util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptEncryptor {

    public static String encrypt(String origin) {
        return BCrypt.hashpw(origin, BCrypt.gensalt());
    }

    public static boolean matches(String origin, String hashed) {
        try {
            return BCrypt.checkpw(origin, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}