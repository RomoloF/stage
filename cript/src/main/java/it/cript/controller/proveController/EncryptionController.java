package it.cript.controller.proveController;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.cript.controller.KeyController;
import it.cript.logicacripto.*;
import it.cript.model.TbClients;
import it.cript.repository.TbClientCallsRepository;
import it.cript.model.TbClientCalls;
import it.cript.model.Response;
import it.cript.service.TbClientCallsService;
import it.cript.service.TbClientsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class EncryptionController {

    private final KeyController keyController;
    private final CryptoService cryptoService;
    
    private final TbClientsService tbClientsService; 
    private final TbClientCallsService tbClientCallsService;
       
    public EncryptionController(KeyController keyController, CryptoService cryptoService,
    	TbClientsService tbClientsService, TbClientCallsService tbClientCallsService ) {
        this.keyController = keyController;
        this.cryptoService = cryptoService;
        this.tbClientsService=tbClientsService;
		this.tbClientCallsService = tbClientCallsService;        
    }
    @Operation(summary = "Cripta un JSON fornito", description = "Questo endpoint cripta un JSON fornito utilizzando una chiave generata e restituisce il JSON criptato.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "JSON criptato con successo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "500", description = "Errore durante la criptazione del JSON",
            content = @Content)
    })
    @PostMapping("/encryptJson")
    public String encryptJson(@RequestBody String jsonOriginale) {
        try {
            // Ottenere la chiave dal KeyController
            String secretKeyBase64 = keyController.generateSecretKey();

            // Cifrare il JSON usando il CryptoService
            String encryptedJson = cryptoService.encrypt(jsonOriginale, secretKeyBase64);

            // Prepara la risposta
           Response response = new Response();
            response.setEncryptedJson(encryptedJson);
            response.setMessage("JSON criptato con successo.");
            return "CIAO FUNZIONA La Key è >>"+secretKeyBase64+"\nIl json criptato è >>"+encryptedJson+"\n \n La response è \n"+response;
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setMessage("Errore durante la criptazione del JSON.");
            return encryptJson(null);
        }
    }
    //**********
    @Operation(summary = "Salva un JSON criptato su TbClientCalls e aggiunge un nuovo cliente", description = "Questo endpoint salva un JSON criptato su TbClientCalls e aggiunge \n un nuovo cliente nella tabella TbClients con una chiave segreta fornita.\nSi aspetta un clienteID ,e la secretKey")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dati salvati con successo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "500", description = "Errore durante il salvataggio",
            content = @Content)
    })
    @PostMapping("/saveClientAndCall")
    public String saveClientAndCall(@RequestParam String clientID,
    								@RequestParam String nomeNuovo,
                                    @RequestParam String secretKey,
                                    @RequestBody String jsonOriginale) {
        try {
            // Creazione di un nuovo cliente
            TbClients newClient = new TbClients();
            //newClient.setClientID(clientID);
            newClient.setClientSecret(secretKey);
            newClient.setNome(nomeNuovo);
            // Salva il nuovo cliente nel database
            tbClientsService.saveClient(newClient);

            // Cripta il JSON usando la chiave segreta fornita
            String encryptedJson = cryptoService.encrypt(jsonOriginale, secretKey);
            
            // Creazione di un nuovo record TbClientCalls
            TbClientCalls tbClientCalls = new TbClientCalls();
           
            tbClientCalls.setClientID(clientID);
            tbClientCalls.setDataChiamata(new java.util.Date()); // Usa la data corrente
            tbClientCalls.setMetadataRichiesta(jsonOriginale);
            tbClientCalls.setMetadataRisposta(encryptedJson);
            tbClientCalls.setStato("Salvato sulla tabella tbclientCalls");
            
            // Salva il record TbClientCalls nel database
            tbClientCallsService.saveCall(tbClientCalls);

            // Prepara la risposta
            Response response = new Response();
            response.setEncryptedJson_originale(jsonOriginale);
            response.setEncryptedJson(encryptedJson);
            response.setMessage("Cliente e JSON criptato salvati con successo.");
            response.setStato("Salvato su tbclientCalls e aggiunto nuovo cliente su tbClients");

            return "Cliente e JSON criptato salvati con successo.\n" +
                   "La Key è >> " + secretKey + "\n" +
                   "Il JSON criptato è >> " + encryptedJson + "\n" +
                   "La response è \n" + response;
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setMessage("Errore durante il salvataggio dei dati.");
            return response.toString();
        }
    }

    //**********
    @Operation(summary = "Cripta e salva un JSON nel database", description = "Questo endpoint cripta un JSON fornito, salva i dettagli nel database e restituisce il JSON criptato.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "JSON criptato e salvato con successo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "500", description = "Errore durante la criptazione o il salvataggio nel database",
            content = @Content)
    })
    @PostMapping("/encryptJsonInDbOutDb")
    public String encryptJsonInDbOutDb(@RequestParam String clientID , @RequestBody String jsonOriginale) {
        try {
            // Ottenere la secretKey dalla tabella tbclients in base al clientID
        	
        	 TbClients client = tbClientsService.findById(clientID);
            String secretKeyBase64 = client.getClientSecret();

            // Cifrare il JSON usando il CryptoService
            String encryptedJson = cryptoService.encrypt(jsonOriginale, secretKeyBase64);
            
            // Creare una nuova istanza di TbClientCalls e popolala con i dati
            TbClientCalls tbClientCalls = new TbClientCalls();
            tbClientCalls.setClientID(clientID);
            tbClientCalls.setDataChiamata(new java.util.Date()); // Usa la data corrente
            tbClientCalls.setMetadataRichiesta(jsonOriginale);
            tbClientCalls.setMetadataRisposta(encryptedJson);
            tbClientCalls.setStato("Salvato sulla tabella tbclientCalls");

            // Salva il record nel database
            tbClientCallsService.saveCall(tbClientCalls);

            
            
            
            // Prepara la risposta
           Response response = new Response();
           	response.setEncryptedJson_originale(jsonOriginale);
            response.setEncryptedJson(encryptedJson);
            response.setMessage("JSON criptato con successo.");
            response.setStato("Salvato sulla tabella tbclientCalls");
            return "CIAO FUNZIONA La Key è >>"+secretKeyBase64+"\nIl json criptato è >>"+encryptedJson+"\n \n La response è \n"+response;
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setMessage("Errore durante la criptazione del JSON.");
            return encryptJson(null);
        }
    }
  
}

