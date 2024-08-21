package it.cript.logicacripto;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CryptoService {

    public String encrypt(String jsonOriginale, String secretKeyBase64) throws Exception {
        // Decodifica della chiave da Base64
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
        SecretKeySpec secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        // Cifrare il JSON originale con la chiave ottenuta
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(jsonOriginale.getBytes());

        // Codifica il testo cifrato in Base64 per restituirlo come stringa
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
