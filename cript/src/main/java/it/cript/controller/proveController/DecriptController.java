package it.cript.controller.proveController;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import it.cript.model.Response;
import it.cript.model.TbClientCalls;
import it.cript.service.TbClientCallsService;
import it.cript.model.TbClients;

//import it.cript.repository.TbClientsRepository;
import it.cript.service.TbClientsService;
import it.cript.utility.DecryptApplication;
@RestController
@RequestMapping("/api/decripta_Json_Imput_ID")
public class DecriptController {
	@Autowired
	private TbClientCallsService tbClientsCallsService;
	@Autowired
	private TbClientsService tbClientsService;
//	@Autowired
//	private TbClientsService tbClientsRepository;
//	@Autowired
//	private TbClients tbClients;
	
	 @Operation(summary = "Avvia la decriptazione per un client specifico tramite il suo ID ")
	    @ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Decriptazione avviata con successo",
	                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
	        @ApiResponse(responseCode = "404", description = "Client non trovato"),
	        @ApiResponse(responseCode = "500", description = "Errore interno del server")
	    })   
	 @PostMapping("/decripta_Json_Imput_ID")
	 /**
	  * 
	  * @param clientID
	  * @param metadata (JSON criptato)
	  * @return
	  */
	    public ResponseEntity<String> startProtocollo(@RequestParam String clientID, @RequestParam String metadata) {
	        try {
	            //Date today = Date();
	            String stato = "";
	            
	            System.out.println("Stampo clienteID :" + clientID + " Stampo metadata criptato :" + metadata);
	            
	            // Controllo esistenza di utente su db
	            TbClients client = tbClientsService.findById(clientID);
	            if (client == null) {
	                return ResponseEntity.status(404).body("Cliente non trovato");
	            }	 
	            
	            
	            String clientSecret = client.getClientSecret();
	            System.out.println("Stampo il clientSecret recuperato nel db >>> " + clientSecret);
	            System.out.println("Stampo il metadata ricevuto :" + metadata);	            
	            // Decrypt del metadata
	            String jsonDecriptato = DecryptApplication.decrypt(clientSecret, metadata);
	            System.out.println("Questo è il json decriptato >>>> " + jsonDecriptato);	            
	            // Registrazione chiamata su db
	            TbClientCalls call = new TbClientCalls();
	            call.setClientID(clientID);
	           // call.setDataChiamata(today);
	            call.setMetadataRichiesta(metadata);
	            call.setMetadataRisposta(jsonDecriptato);
	            call.setStato(stato);
	            tbClientsCallsService.saveCall(call);
	            
	            return ResponseEntity.ok(jsonDecriptato);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body("Errore durante il protocollo: " + e.getMessage());
	        }
	    }
	}

