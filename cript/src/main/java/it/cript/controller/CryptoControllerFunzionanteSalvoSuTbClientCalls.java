package it.cript.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import it.cript.model.Response;
import it.cript.model.TbClients;
import it.cript.service.TbClientsService;
import it.cript.model.TbClientCalls;
import it.cript.service.TbClientCallsService;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Questi endPoint funzionano correttamente.Ma
 * non salvano nel DB tranne uno.
 */
@RestController
@RequestMapping("/cryptoSalvoDB")
public class CryptoControllerFunzionanteSalvoSuTbClientCalls {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    
    //Query pronte per Client
    private static final String SQL_S_CLIENTS_BY_CLIENTID = "SELECT clientSecret FROM clients WHERE clientId = ?";
    private static final String SQL_S_CLIENTS_BY_SECRET = "SELECT clientId FROM clients WHERE clientSecret =?";
    private static final String SQL_I_CLIENTS = "INSERT INTO clients (clientId, clientSecret) VALUES (?,?)";
    private static final String SQL_U_CLIENTS = "UPDATE clients SET clientSecret =?, dataAgg = NOW(), uteAgg =? WHERE clientId =?";
    private static final String SQL_D_CLIENTS = "DELETE FROM clients WHERE clientId =?";
    private static final String SQL_GET_CLIENTS = "SELECT * FROM clients";
    
    //Query pronte per ClientCalls
    private static final String SQL_S_CLIENT_CALLS_BY_CLIENT_ID = "SELECT * FROM clientCalls WHERE clientId =?";
    private static final String SQL_I_CLIENT_CALLS = "INSERT INTO clientCalls (clientId, callId, timestamp) VALUES (?,?, NOW())";
    private static final String SQL_U_CLIENT_CALLS = "UPDATE clientCalls SET timestamp = NOW(), uteAgg =? WHERE callId =?";
    private static final String SQL_D_CLIENT_CALLS = "DELETE FROM clientCalls WHERE callId =?";
    
    
    @Autowired
    JdbcTemplate jdbcTemplate;
        
    @Autowired
    TbClientsService tbClientsService;
    
    @Autowired
    TbClientCallsService tbClientCallsService;
    
    //Costruttore e inject
    // Il template JDBC e i servizi dei clienti e delle chiamate sono necessari per interagire con il DB
    // Il servizio dei clienti è necessario per salvare i clienti nel database quando vengono creati
    // Il servizio delle chiamate è necessario per salvare le chiamate nel database quando vengono salvate
    
    // Costruttore con parametri
    public CryptoControllerFunzionanteSalvoSuTbClientCalls(JdbcTemplate jdbcTemplate, TbClientsService tbClientsService,
			TbClientCallsService tbClientCallsService) {
		super();
		this.jdbcTemplate = jdbcTemplate;
		this.tbClientsService = tbClientsService;
		this.tbClientCallsService = tbClientCallsService;
	}
    

	// Metodo per generare una chiave AES
    private SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    // Metodo per convertire una stringa Base64 in una chiave AES
    private SecretKey getKeyFromString(String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    // Metodo per convertire una chiave AES in stringa Base64
    private String getStringFromKey(SecretKey key) {
        byte[] encodedKey = key.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }
    
    
    
    
    // Endpoint per criptare un testo richiede un ClientID
    @PostMapping("/encrypt")
    
    //Sostituisco Response con String per far visualizzare sulla stessa vista del form.
    public Response encrypt(@RequestParam String clientID, @RequestParam String plainText, Model model) {
    	//String keyString=clientID;
    	
        Response response = new Response();
        try {
        	System.out.println("Stampo clienteID :" + clientID + " Stampo da  criptare:" + plainText);
        	
        	// Controllo esistenza di utente su db
            TbClients client = tbClientsService.findById(clientID);
            if (client == null) {
            	response.setMessage("Client con ID ="+clientID+"  non trovato ...");
            	//Sostituisco Response con String per far visualizzare sulla stessa vista del form.
            	return response;
            }	            
        	String keyString=client.getClientSecret();
            SecretKey key = getKeyFromString(keyString);
            
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
            model.addAttribute("encryptedText", encryptedText);
            
            
         // Creazione di un nuovo record TbClientCalls e lo riempio
            TbClientCalls tbClientCalls = new TbClientCalls();
           
            tbClientCalls.setClientID(clientID);
            tbClientCalls.setDataChiamata(new java.util.Date()); // Usa la data corrente
            tbClientCalls.setMetadataRichiesta(plainText);
            tbClientCalls.setMetadataRisposta(encryptedText);
            tbClientCalls.setStato("Salvato sulla tabella tbclientCalls");            
            tbClientCalls.setNomeClient(client.getNome());
            System.out.println(client.getNome());
            tbClientCalls.setSecretKey(client.getClientSecret());
            System.out.println(client.getClientSecret());
         // Salva il record TbClientCalls nel database
            tbClientCallsService.saveCall(tbClientCalls);

           
            response.setSecretKey(keyString);
            response.setJsonOriginale(plainText);
            response.setJsonCriptato(encryptedText);
            response.setData(encryptedText);
            response.setStatus(200);
            response.setMessage("Testo criptato con successo.");
        } catch (Exception e) {
            response.setStatus(500);
            response.setMessage("Errore durante la criptazione del testo.");
            response.setInternalMessage(e.getMessage());
        }
        
        
        
        
      //Sostituisco Response con String per far visualizzare sulla stessa vista del form.
        return    response;
    }
//*******************************************
    
    
    
    
//*******************************************   
    // Endpoint per decriptare un testo
    @PostMapping("/decrypt")
    public Response decrypt(@RequestParam String encryptedText, @RequestParam String keyString) {
        Response response = new Response();
        try {
        	System.out.println("Stringa Base64 ricevuta per la decrittazione: "+encryptedText);
            SecretKey key = getKeyFromString(keyString);
            // Aggiungi un controllo per la lunghezza della stringa Base64
            System.out.println("Lunghezza della stringa Base64: " + encryptedText.length());
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);// Qui può verificarsi l'errore
            System.out.println("Decodifica completata.");
            System.out.println("Questo è il json originale ");
            
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            String decryptedText = new String(decryptedBytes);
			System.out.println(decryptedText);
            response.setData(decryptedText);
            response.setStatus(200);
            response.setMessage("Testo decriptato con successo.");
        } catch (Exception e) {
            response.setStatus(500);
            response.setMessage("Errore durante la decriptazione del testo.");
            response.setInternalMessage(e.getMessage());
        }
        return response;
        
        
        
        
        
    }
    // Endpoint per generare una nuova chiave AES
    @GetMapping("/generateKey")
    public Response generateKeyEndpoint() {
        Response response = new Response();
        try {
            SecretKey key = generateKey();
            String keyString = getStringFromKey(key);

            response.setData(keyString);
            response.setStatus(200);
            response.setMessage("Chiave AES generata con successo.");
        } catch (Exception e) {
            response.setStatus(500);
            response.setMessage("Errore durante la generazione della chiave AES.");
            response.setInternalMessage(e.getMessage());
        }
        return response;
    }
    // Genera clientId e clientSecret
    @GetMapping("/generaClientIdAndSecretNuovoSalvaDB")
    public Response generateClientIdAndSecret(@RequestParam String nomeCliente) {
        Response response = new Response();
        try {
            SecretKey key = generateKey();
            String keyString = getStringFromKey(key);
            System.out.println("Key: " + keyString);
            String uuid = generateUUID();
            System.out.println("UUID: " + uuid);
            System.out.println("Nome Client: " + nomeCliente);

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
            System.out.println("Errore generazione clientId e clientSecret >>>"+e);
            response.setData(null);
            response.setStatus(500);
            response.setInternalMessage(e.getMessage());
            response.setMessage("ClientId e ClientSecret non generati");
        }
        return response;
    }

    private String generateUUID() {
        // Genera un nuovo UUID
        UUID uuid = UUID.randomUUID();
        
        // Restituisce l'UUID come stringa
        return uuid.toString();
    }

//	public class Response {
//        private int status;
//        private String message;
//        private String internalMessage;
//        private Object data;
//
//        // Getters e Setters
//        public int getStatus() {
//            return status;
//        }
//
//        public void setSecretKey(String keyString) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		public void setStatus(int status) {
//            this.status = status;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        public void setMessage(String message) {
//            this.message = message;
//        }
//
//        public String getInternalMessage() {
//            return internalMessage;
//        }
//
//        public void setInternalMessage(String internalMessage) {
//            this.internalMessage = internalMessage;
//        }
//
//        public Object getData() {
//            return data;
//        }
//
//        public void setData(Object data) {
//            this.data = data;
//        }
//    }

}
