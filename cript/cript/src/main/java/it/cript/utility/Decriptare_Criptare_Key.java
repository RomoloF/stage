package it.cript.utility;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
@Service
public class Decriptare_Criptare_Key {
	
	private static final String ALGORITHM = "AES";
	private static final int KEY_SIZE = 256;

// Generate a DES key GENERO LA KEY
private static SecretKey generateKey() throws Exception {
    KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
    keyGen.init(KEY_SIZE, new SecureRandom());
    return keyGen.generateKey();
}

// Encrypt a string
public static String encrypt(String plainText, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
    return Base64.getUrlEncoder().encodeToString(encryptedBytes);
}

// Decrypt a string
public static String decrypt(String encryptedText, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] decryptedBytes = Base64.getUrlDecoder().decode(encryptedText);
    return new String(cipher.doFinal(decryptedBytes));
}


// Convert a string key to a SecretKey object
public static SecretKey getKeyFromString(String keyString) throws Exception {
    byte[] decodedKey = Base64.getDecoder().decode(keyString);
    return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
}

// Convert a SecretKey object to a string
public static String getStringFromKey(SecretKey key) throws Exception {
    byte[] encodedKey = key.getEncoded();
    return Base64.getEncoder().encodeToString(encodedKey);
}
}

/**
@GetMapping("/generateClientIdAndSecret")
public Response generateClientIdAndSecret() {
    Response response = new Response();
    Response response = new Response();
    SecretKey key;
    String keyString;
    String uuid;
    try {
        key = generateKey();
        keyString = getStringFromKey(key);
        log.info("Key: " + keyString);
        uuid = generateUUID();
        log.info("UUID: " + uuid);
        
        Map<String, String> coppia = new HashMap<String, String>();
        coppia.put("clientId", uuid);
        coppia.put("clientSecret", keyString);
        
        response.setData(coppia);
        response.setStatus(200);
        response.setMessage("ClientId e ClientSecret generato");
    } catch (Exception e) {
    log.error("Errore generazione clientId e clientSecret", e);
    response.setData(null);
    response.setStatus(500);
    response.setInternalMessage(e.getMessage());
    response.setMessage("ClientId e ClientSecret non generato");
	}
    return response;
	}


//qui arriva json decriptato
@GetMapping("/encrypt")
public Response encrypt(@RequestParam String plainText) {
    Response response = new Response();
    try {
        Map<String, Object> coppia = jdbcTemplate.queryForMap(SQL_S_CLIENTS_BY_CLIENTID, "a682d4cc-64fe-4ae5-bbfc-6fdbccfe9320");
        String keyString = coppia.get("clientSecret").toString();

        SecretKey key = getKeyFromString(keyString);
        String encryptText = encrypt(plainText, key);

        log.info("Encrypt testo: " + plainText + " -> " + encryptText);

        response.setData(encryptText);
        response.setStatus(200);
        response.setMessage("Encrypt effettuato");
    } catch (Exception e) {
        log.error("Errore encrypt", e);
        response.setData(null);
        response.setStatus(500);
        response.setInternalMessage(e.getMessage());
        response.setMessage("Errore encrypt");
    }
    return response;
}



@GetMapping("/encryptJson")
public Response encryptJson(@RequestParam String base64Text {
    Response response = new Response();
    try {
        Map<String, Object> coppia = jdbcTemplate.queryForMap(SQL_S_CLIENTS_BY_CLIENTID, "a682d4cc-64fe-4ae5-bbfc-6fdbccfe9320";
        String keyString = coppia.get("clientSecret").toString();
        
        SecretKey key = getKeyFromString(keyString);
        
        String plainText = new String(Base64.getDecoder().decode(base64Text.getBytes());
        
        String encryptText = encrypt(plainText, key);
        
        log.info("Encrypt testo: " + plainText + " -> " + encryptText);
        
        response.setData(encryptText);
        response.setStatus(200);
        response.setMessage("Encrypt effettuato");
    } catch (Exception e {
        log.error("Errore encrypt", e);
       
		response.setData(null);
        response.setStatus(500);
        response.setInternalMessage(e.getMessage());
        response.setMessage("Errore encrypt");
    }
    return response;
}



@GetMapping("/decrypt")
public Response decrypt(@RequestParam String encryptText) {
    Response response = new Response();
    try {
        Map<String, Object> coppia = jdbcTemplate.queryForMap(SQL_S_CLIENTS_BY_CLIENTID, "a682d4cc-64fe-4ae5-bbfc-6fdbccfe9320");
        String keyString = coppia.get("clientSecret").toString();

        SecretKey key = getKeyFromString(keyString);
        String plainText = decrypt(encryptText, key);
        log.info("Decrypt testo: " + encryptText + " -> " + plainText);

        response.setData(plainText);
        response.setStatus(200);
        response.setMessage("Decrypt effettuato");
    } catch (Exception e) {
        log.error("Errore decrypt", e);
        response.setData(null);
        response.setStatus(500);
        response.setInternalMessage(e.getMessage());
        response.setMessage("Errore decrypt");
    }
    return response;
}

}
*/