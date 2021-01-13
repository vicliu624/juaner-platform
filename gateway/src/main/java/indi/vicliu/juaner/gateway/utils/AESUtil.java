package indi.vicliu.juaner.gateway.utils;


import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
public class AESUtil {

    public static byte[] AESEncrypt(byte[] text,byte[] key,String encryptMode) throws Exception {
        try {
            if (key == null) {
                log.error("没有加密需要的key");
                return null;
            }
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher=Cipher.getInstance("AES/"+encryptMode+"/PKCS5Padding");
            if(encryptMode.equalsIgnoreCase("ECB")) {
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            } else {
                IvParameterSpec iv = new IvParameterSpec(key);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            }
            try {
                return cipher.doFinal(text);
            } catch (Exception e) {
                log.error("解密出錯",e);
                return null;
            }
        } catch (Exception ex) {
            log.error("解密出錯",ex);
            throw new Exception("解密出錯" + ex.getMessage());
        }      
    }

    /**
     * AES解密
     *
     * @param cipertext 密文
     * @param key 密钥
     * @param encryptMode AES加密模式，CBC或ECB
     * @return 该密文的明文
     */
    public static byte[] AESDecrypt(Object cipertext, byte[] key,String encryptMode) throws Exception {
        String cipherText=null;
        try {
            cipherText = cipertext.toString();
            // 判断Key是否正确
            if (key == null) {
                log.error("没有解密需要的key");
                return null;
            }
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher=Cipher.getInstance("AES/"+encryptMode+"/PKCS5Padding");
            if(encryptMode.equalsIgnoreCase("ECB")) {
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            } else {
                IvParameterSpec iv = new IvParameterSpec(key);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            }
            byte[] encrypted1 = Base64.getDecoder().decode(cipherText);//先用base64解密
            try {
                return cipher.doFinal(encrypted1);
            } catch (Exception e) {
                log.error("解密出錯",e);
                return null;
            }
        } catch (Exception ex) {
            log.error("解密出錯",ex);
            throw new Exception("解密出錯" + ex.getMessage());
        }
    }
}
