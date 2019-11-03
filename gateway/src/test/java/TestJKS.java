import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.security.*;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-21 15:53
 * @Description:
 */
public class TestJKS {

    @Test
    public void readPublicKey(){
        try{
            File file = new File("/Users/liuweikai/Projects/juaner-cloud/authorization/src/main/resources/keystore.jks");
            FileInputStream is = new FileInputStream(file);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            String alias = "juaner";
            String password = "juaner-cloud";
            char[] passwd = password.toCharArray();
            keystore.load(is, passwd);
            PublicKey pubKey = keystore.getCertificate(alias).getPublicKey();
            String publicKeyString = Base64.encodeBase64String(pubKey.getEncoded());
            System.out.println(publicKeyString);

            System.out.println(publicKeyString);
            is.close();
        } catch (Exception e){

        }
    }
}
