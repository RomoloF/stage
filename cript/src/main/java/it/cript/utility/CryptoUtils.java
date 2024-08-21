package it.cript.utility;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CryptoUtils {

    private static final Logger logger = LoggerFactory.getLogger(CryptoUtils.class);
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    public static String encrypt(String plainText, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(getKeyBytes(key), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedValue = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedValue);
    }
    public static String decrypt(String encryptedText, String key) throws Exception {
        try {
        	System.out.println("Stringa Base64 ricevuta per la decrittazione: "+encryptedText);
        	// Tronca la chiave fornita a 32 byte
           // SecretKeySpec secretKey = new SecretKeySpec(getKeyBytes(key), ALGORITHM);
            
            SecretKey secretkey = getKeyFromString(key);
            // Aggiungi un controllo per la lunghezza della stringa Base64
            System.out.println("Lunghezza della stringa Base64: " + encryptedText.length());
            
            
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretkey);
            byte[] decodedByte = Base64.getDecoder().decode(encryptedText);// Qui può verificarsi l'errore
            System.out.println("Decodifica completata.");            
            byte[] decryptedValue = cipher.doFinal(decodedByte);
            System.out.println("Questo è il json originale ");
            return new String(decryptedValue);
            
        } catch (IllegalArgumentException e) {
            logger.error("Failed to decode Base64 string: {}", encryptedText, e);
            throw new Exception("Invalid Base64 string", e);
        } catch (Exception e) {
            logger.error("Decryption failed", e);
            throw new Exception("Decryption error", e);
        }
    }

    private static SecretKey getKeyFromString(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	public static String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256); // AES-256
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

//    private static byte[] getKeyBytes(String key) throws Exception {
//        if (key.length() != 32) {
//            throw new Exception("Invalid key length: AES-256 requires a 32-byte key");
//        }
//        return key.getBytes();
//    }
    private static byte[] getKeyBytes(String key) {
        byte[] keyBytes = new byte[32]; // 32 bytes for AES-256
        byte[] parameterKeyBytes = key.getBytes();

        // Copia la chiave fornita nei primi 32 byte, troncando o riempiendo con zero se necessario
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));

        return keyBytes;
    }

    
    
}
