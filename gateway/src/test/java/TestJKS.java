import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;

/**
 * @Auther: liuweikai
 * @Date: 2019-09-21 15:53
 * @Description:
 */
@Slf4j
public class TestJKS {

    private static final String JKS_FILE = "/Users/liuweikai/Projects/juaner-cloud/authorization/src/main/resources/keystore.jks";

    @Test
    public void readPublicKey(){
        FileInputStream is = null;
        try{
            File file = new File(JKS_FILE);
            is = new FileInputStream(file);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            String alias = "juaner";
            String password = "juaner-cloud";
            char[] passwd = password.toCharArray();
            keystore.load(is, passwd);
            PublicKey pubKey = keystore.getCertificate(alias).getPublicKey();
            String publicKeyString = Base64.encodeBase64String(pubKey.getEncoded());
            System.out.println(publicKeyString);
            is.close();
        } catch (Exception e){
            log.error("calc public error.",e);
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
