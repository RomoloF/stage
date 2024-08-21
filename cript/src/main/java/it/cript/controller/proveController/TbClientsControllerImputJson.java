package it.cript.controller.proveController;

import it.cript.model.TbClients;
import it.cript.service.TbClientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@RestController
@RequestMapping("/api/clients")
public class TbClientsControllerImputJson {

    @Autowired
    private TbClientsService tbClientsService;

    @PostMapping("/create")
    public ResponseEntity<String> createClient(@RequestParam String nome ,@RequestBody String jsonToEncrypt) {
        try {
            // Generazione della chiave
            SecretKey secretKey = generateKey();
            String keyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            // Criptazione del JSON
            String encryptedJson = encryptJson(jsonToEncrypt, secretKey);

            // Creazione del nuovo client
            TbClients newClient = new TbClients();
            //newClient.setClientID(generateClientID()); // Implementa una funzione per generare un ID unico
            newClient.setNome(nome);
            newClient.setClientSecret(keyString);
            newClient.setJson_originale(jsonToEncrypt);
            newClient.setJson_criptato(encryptedJson);

            // Salvataggio nel database
            tbClientsService.saveClient(newClient);

            return new ResponseEntity<>("Client creato con successo!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore nella creazione del client: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Funzione per generare una chiave segreta
    private SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // Puoi usare anche 192 o 256 bit
        return keyGen.generateKey();
    }

    // Funzione per criptare il JSON
    private String encryptJson(String json, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(json.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Implementa una funzione per generare un client ID unico
    private String generateClientID() {
        // Puoi usare UUID o un altro metodo per generare un ID unico
        return java.util.UUID.randomUUID().toString();
    }
    
    @PostMapping("/decrypt")
    public ResponseEntity<String> decryptClientJson(@RequestBody String clientID) {
        try {
            // Recupera il client dal database usando l'ID
            TbClients client = tbClientsService.findById(clientID);
            
            if (client == null) {
                return new ResponseEntity<>("Client non trovato", HttpStatus.NOT_FOUND);
            }

            // Recupera la chiave e il JSON criptato
            String keyString = client.getClientSecret();
            String encryptedJson = client.getJson_criptato();

            // Converte la stringa della chiave in un oggetto SecretKey
            SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(keyString), "AES");

            // Decripta il JSON
            String decryptedJson = decryptJson(encryptedJson, secretKey);

            // Restituisce il JSON originale
            return new ResponseEntity<>(decryptedJson, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la decriptazione: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Funzione per decriptare il JSON
    private String decryptJson(String encryptedJson, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedJson));
        return new String(decryptedBytes);
    }

}

