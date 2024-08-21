package it.cript.controller;

import it.cript.model.TbClients;
import it.cript.service.TbClientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.parameters.RequestBody as SwaggerRequestBody;
//import io.swagger.v3.oas.annotations.parameters.Parameter;

import java.util.List;




@RestController
@RequestMapping("/api/clients")
public class TbClientsController {

    private final TbClientsService tbClientsService;

   
    public TbClientsController(TbClientsService tbClientsService) {
        this.tbClientsService = tbClientsService;
    }
    @Operation(summary = "Recupera tutti i client", description = "Restituisce una lista di tutti i client presenti nel database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operazione riuscita", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TbClients.class))),
        @ApiResponse(responseCode = "500", description = "Errore interno del server", 
            content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TbClients>> findAll() {
        List<TbClients> clients = tbClientsService.findAll();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }
    @Operation(summary = "Recupera un client per ID", description = "Restituisce i dettagli di un singolo client basato sul suo ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operazione riuscita", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TbClients.class))),
        @ApiResponse(responseCode = "404", description = "Client non trovato", 
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Errore interno del server", 
            content = @Content)
    })
    // Endpoint per recuperare un client per ID  (GET) /api/clients/{clientID}  e (DELETE) /api/clients/{clientID}  (PUT) /api/clients/{clientID}  (POST) /api/clients  (GET) /api/clients  (GET) /api/clients/{clientID}  (DELETE) /api/clients/{clientID}  (PUT) /api/clients/{clientID}  ()
    @GetMapping("/{clientID}")
    public ResponseEntity<TbClients> findById(@PathVariable String clientID) {
        TbClients client = tbClientsService.findById(clientID);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }
    @Operation(summary = "Crea un nuovo client", description = "Aggiunge un nuovo client al database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Client creato con successo", 
            content = @Content),
        @ApiResponse(responseCode = "400", description = "Dati del client non validi", 
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Errore interno del server", 
            content = @Content)
    })
    // Endpoint per salvare un client vuole un body 
    @PostMapping("/createClient")
    public ResponseEntity<Void> createClient(@RequestBody TbClients client) {
        tbClientsService.saveClient(client);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

//    @PostMapping
//    public ResponseEntity<TbClients> save(@RequestBody TbClients client) {
//        TbClients savedClient = tbClientsService.save(client);
//        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
//    }

//    @PutMapping("/{clientID}")
//    public ResponseEntity<TbClients> update(@PathVariable String clientID, @RequestBody TbClients client) {
//        client.setClientID(clientID);
//        TbClients updatedClient = tbClientsService.saveClient(client);
//        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
//    }
    @Operation(summary = "Elimina un client per ID", description = "Elimina un client specifico dal database basato sul suo ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Client eliminato con successo", 
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Client non trovato", 
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Errore interno del server", 
            content = @Content)
    })
    @DeleteMapping("/delete/{clientID}")
    public ResponseEntity<Void> deleteById(@PathVariable String clientID) {
        tbClientsService.deleteById(clientID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}