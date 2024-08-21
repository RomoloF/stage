package it.cript.utility;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class CryptoUtils {

    private static final String ALGORITHM = "AES";

   
    public static String encrypt(String plainText, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(getKeyBytes(key), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedValue = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedValue);
    }

   
//    public static String decrypt(String encryptedText, String key) throws Exception {
//        SecretKeySpec secretKey = new SecretKeySpec(getKeyBytes(key), ALGORITHM);
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] decodedValue = Base64.getDecoder().decode(encryptedText);
//        byte[] decryptedValue = cipher.doFinal(decodedValue);
//        return new String(decryptedValue);
//    }
    
    public static String decrypt(String encryptedText, String key) throws Exception {
    	
    	String base64String = encryptedText;
    	System.out.println("Ristampo encryptedText :"+encryptedText);
    	
    	
    	
    	
        try {
        	System.out.println("Sono entrato nella classe CryptoUtils metodo encriptedText "); 
        	
            SecretKeySpec secretKey = new SecretKeySpec(getKeyBytes(key), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            System.out.println("Stampo secreteKey :"+secretKey);
            System.out.println("    Stampo cipher :"+cipher);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            System.out.println("Ristampo cipher initializzato >>> "+cipher);
            // Logging the encrypted text for debugging purposes
           // logger.debug("Encrypted text: {}", encryptedText);
           // System.out.println("Stampo encryptedText >>>"+(encryptedText.replaceAll("\\s+", "")));
            
            byte[] decodedValue = Base64.getDecoder().decode(encryptedText);
            
            System.out.println("Stampo l'array >>>"+decodedValue);
            byte[] decryptedValue = cipher.doFinal(decodedValue);
            
            return new String(decryptedValue);
        } catch (IllegalArgumentException e) {
           // logger.error("Failed to decode Base64 string: {}", encryptedText, e);
            throw new Exception("Invalid Base64 string", e);
        } catch (Exception e) {
          //  logger.error("Decryption failed", e);
            throw new Exception("Decryption error", e);
        }
    }
    
    
    // Generare una chiave valida
    public static String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256); // AES-256
        SecretKey secretKey = keyGen.generateKey();
      
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

   // questo è cambiato 
    private static byte[] getKeyBytes(String key) {
        byte[] keyBytes = new byte[32]; // Use 32 bytes for AES-256
        byte[] parameterKeyBytes = key.getBytes();
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
        return keyBytes;
    }
}
