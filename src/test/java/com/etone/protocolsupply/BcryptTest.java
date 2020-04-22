package com.etone.protocolsupply;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Map;

public class BcryptTest {

    @Test
    public void test() {
        String string = "123456";

        Map<String, String> bcrypt = BcryptCipher.Bcrypt(string);

        System.out.println(bcrypt.keySet()); //[cipher, salt]



        System.out.println(bcrypt.get("cipher")); //$2a$12$ylb92Z84gqlrSfzIztlCV.dK0xNbw.pOv3UwXXA76llOsNRTJsE/.

        System.out.println(bcrypt.get("salt")); //$2a$12$ylb92Z84gqlrSfzIztlCV.



        Map<String, String> bcrypt2 = BcryptCipher.Bcrypt(bcrypt.get("salt"),string);

        System.out.println(bcrypt2.get("SALT_KEY")); //null

        System.out.println(bcrypt2.get("CIPHER_KEY")); //null

        System.out.println(BCrypt.hashpw("123456", "$2a$12$2f4hHmvpiRjdxYYEnPjluOpdBnpKfJPI0NFlQlSx50zJG53hpMQPS"));
    }
}
