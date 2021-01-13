import indi.vicliu.juaner.gateway.utils.AESUtil;
import indi.vicliu.juaner.gateway.utils.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TestRSA {

    @Test
    public void testRSA() throws Exception {

        //生成公私钥
        Map<Integer, String> keyMap = new HashMap<Integer, String>();
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = Base64.getEncoder().encodeToString((privateKey.getEncoded()));
        // 将公钥和私钥保存到Map
        keyMap.put(0,publicKeyString);  //0表示公钥
        keyMap.put(1,privateKeyString);  //1表示私钥
        System.out.println("公钥:" + publicKeyString);
        System.out.println("私钥:" + privateKeyString);

        String aesKey = "0123456789abcdef";
        System.out.println("随机生成的公钥为:" + keyMap.get(0));
        System.out.println("随机生成的私钥为:" + keyMap.get(1));
        byte[] aesKeyByte = RSAUtil.encrypt(aesKey,keyMap.get(0) );
        System.out.println(aesKey + "\t加密后的字符串为:" + new String(aesKeyByte, StandardCharsets.UTF_8));
        byte[] messageDe = RSAUtil.decrypt(new String(aesKeyByte, StandardCharsets.UTF_8),keyMap.get(1));
        System.out.println("还原后的字符串为:" + new String(messageDe, StandardCharsets.UTF_8) );

        aesKeyByte = RSAUtil.encrypt(aesKey,"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgY9SHbkGYmwmwer+Oa89+IxJrsuQplL+qW5dnJAtlpPJOmLnXqZ8OhN5NTPpOWxj7XV0bDXWh3QBAftreaFJtkWNtpIUPZSq96PAI9f2h+Ing9UpoNXTWs4NQk6hX/0EQYHfhzeGK4g9YrC8vsYbQXA6waJwLFB+LMe5g2OT7VXOhxppA1wMIdhmsIE3BO8q0OTecatXYfTz2rFvMLpc4U735cB58T4PFUwB5mXtBgacoB+KGy/VwY9DmQ3/hqR6nRUMHEM6YOJ+BtayMrCLmoV0ilhLQhGqtZaPqESnXAw9PNWSifL0QJyMMknpgr3kpbK3NkgrMgH0b/qYgtQnqQIDAQAB");
        System.out.println(aesKey + "\t加密后的字符串为:" + new String(aesKeyByte, StandardCharsets.UTF_8));

        String cipherText = "dyHRdVRfCCNpj7kj7sSfectKyl+ZsGsI0CtO9Jhvbl8EYfLbhWD5zn5CeRq9HfEU7PBsW9xOWTksxhg2EkTs4oA8TPYhg3kov+HX1eIqNAYDAArrT/UdKRiPguShYqH2";
        System.out.println("解密内容:" + new String(AESUtil.AESDecrypt(cipherText,aesKey.getBytes(StandardCharsets.UTF_8),"ECB")));
    }
}
