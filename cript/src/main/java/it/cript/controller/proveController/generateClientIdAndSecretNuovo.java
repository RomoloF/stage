package it.cript.controller.proveController;



import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.cript.model.Response;
import it.cript.model.TbClients;
import it.cript.service.TbClientsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class generateClientIdAndSecretNuovo {
	
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private static final Logger log = LoggerFactory.getLogger(generateClientIdAndSecretNuovo.class);
    private static final String SQL_S_CLIENTS_BY_CLIENTID = "SELECT clientSecret FROM clients WHERE clientId = ?";
   
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    TbClientsService tbClientsService;
    
    
    public generateClientIdAndSecretNuovo(JdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}

	// Genera un nuovo UUID
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    // Genera una chiave AES
    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE, new SecureRandom());
        return keyGen.generateKey();
    }

    // Cripta una stringa
    public static String encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getUrlEncoder().encodeToString(encryptedBytes);
    }

    // Decripta una stringa
    public static String decrypt(String encryptedText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = Base64.getUrlDecoder().decode(encryptedText);
        return new String(cipher.doFinal(decryptedBytes));
    }

    // Converte una stringa in un oggetto SecretKey
    public static SecretKey getKeyFromString(String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    // Converte un oggetto SecretKey in una stringa
    public static String getStringFromKey(SecretKey key) throws Exception {
        byte[] encodedKey = key.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }
    
    // Genera clientId e clientSecret
    @GetMapping("/generateClientIdAndSecretNuovo")
    public Response generateClientIdAndSecret(@RequestParam String nomeCliente) {
        Response response = new Response();
        try {
            SecretKey key = generateKey();
            String keyString = getStringFromKey(key);
            log.info("Key: " + keyString);
            String uuid = generateUUID();
            log.info("UUID: " + uuid);

            Map<String, String> coppia = new HashMap<>();
            coppia.put("clientId", uuid);
            coppia.put("clientSecret", keyString);
            coppia.put("nomeCliente", nomeCliente);
            
            // Salva il client nel database
            TbClients client=new TbClients();
            client.setClientID(uuid);
            client.setNome(nomeCliente);
            client.setClientSecret(keyString);
            tbClientsService.saveClient(client);
            
            response.setSecretKey(keyString);
            response.setData(coppia);
            response.setStatus(200);
            response.setMessage("ClientId="+uuid+"*** e ClientSecret="+keyString+"*** generati e lo ho anche salvato ne DB ...");
        } catch (Exception e) {
            log.error("Errore generazione clientId e clientSecret", e);
            response.setData(null);
            response.setStatus(500);
            response.setInternalMessage(e.getMessage());
            response.setMessage("ClientId e ClientSecret non generati");
        }
        return response;
    }

//    // Cripta un testo
//    @GetMapping("/encryptUnJson")
//    public Response encrypt1(@RequestBody String plainText) {
//        Response response = new Response();
//        try {
//           // Map<String, Object> coppia = jdbcTemplate.queryForMap(SQL_S_CLIENTS_BY_CLIENTID, key);
//           // String keyString = coppia.get("clientSecret").toString();
//
//            SecretKey key1 = getKeyFromString("PROVA");
//            String encryptText = encrypt(plainText, key1);
//
//            log.info("Encrypt testo: " + plainText + " -> " + encryptText);
//            //response.setSecretKey(key1)
//            response.setData("Text criptato >>>"+encryptText);
//            response.setStatus(200);
//            response.setMessage("Encrypt effettuato");
//        } catch (Exception e) {
//            log.error("Errore encrypt", e);
//            response.setData(null);
//            response.setStatus(500);
//            response.setInternalMessage(e.getMessage());
//            response.setMessage("Errore encrypt");
//        }
//        return response;
//    }

    // Cripta un testo in base64
    @PostMapping("/encryptJsonPost")
    public Response encryptJson(@RequestParam String Key,@RequestBody String base64Text) {
        Response response = new Response();
        try {
           // Map<String, Object> coppia = jdbcTemplate.queryForMap(SQL_S_CLIENTS_BY_CLIENTID, "a682d4cc-64fe-4ae5-bbfc-6fdbccfe9320");
            String keyString = Key;//coppia.get("clientSecret").toString();

            SecretKey key1 = getKeyFromString(keyString);

            String plainText = new String(Base64.getDecoder().decode(base64Text.getBytes()));

            String encryptText = encrypt(plainText, key1);

            log.info("Encrypt testo: " + plainText + " -> " + encryptText);
            response.setSecretKey(keyString);
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

    // Decripta un testo criptato
    @PostMapping("/decrypt")
    public Response decrypt(@RequestParam String encryptText,@RequestParam String clientSecret) {
        Response response = new Response();
        try {
            //Map<String, Object> coppia = jdbcTemplate.queryForMap(SQL_S_CLIENTS_BY_CLIENTID, "a682d4cc-64fe-4ae5-bbfc-6fdbccfe9320");
            String keyString = clientSecret;//coppia.get("clientSecret").toString();

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
