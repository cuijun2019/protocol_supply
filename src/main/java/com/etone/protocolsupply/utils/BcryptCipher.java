package com.etone.protocolsupply.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class BcryptCipher {
    // generate salt seed
    private static final int SALT_SEED = 12;

    // the head fo salt
    private static final String SALT_STARTSWITH = "$2a$12";
    public static final  String SALT_KEY        = "salt";
    public static final  String CIPHER_KEY      = "cipher";

    /**
     * Bcrypt encryption algorithm method
     *
     * @param encryptSource need to encrypt the string
     * @return Map , two values in Map , salt and cipher
     */
    public static Map<String, String> Bcrypt(final String encryptSource) {
        String salt = BCrypt.gensalt(SALT_SEED);
        Map<String, String> bcryptResult = Bcrypt(salt, encryptSource);
        return bcryptResult;
    }

    /**
     * @param salt          encrypt salt, Must conform to the rules
     * @param encryptSource
     * @return
     */
    public static Map<String, String> Bcrypt(final String salt, final String encryptSource) {
        if ("".equals(encryptSource) || encryptSource == null) {
            throw new RuntimeException("Bcrypt encrypt input params can not be empty");
        }
        if (("".equals(salt) || salt == null) || salt.length() != 29) {
            throw new RuntimeException("Salt can't be empty and length must be to 29");
        }
        if (!salt.startsWith(SALT_STARTSWITH)) {
            throw new RuntimeException("Invalid salt version, salt version is $2a$12");
        }

        String cipher = BCrypt.hashpw(encryptSource, salt);
        Map<String, String> bcryptResult = new HashMap<>();
        bcryptResult.put(SALT_KEY, salt);
        bcryptResult.put(CIPHER_KEY, cipher);
        return bcryptResult;
    }
}
