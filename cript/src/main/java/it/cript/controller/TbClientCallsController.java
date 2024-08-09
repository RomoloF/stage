package it.cript.controller;



import it.cript.model.TbClientCalls;
import it.cript.service.TbClientCallsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.parameters.Parameter;


@RestController
@RequestMapping("/api/calls")
public class TbClientCallsController {

    @Autowired
    private TbClientCallsService tbClientCallsService;
    @Operation(summary = "Recupera tutte le chiamate", description = "Restituisce una lista di tutte le chiamate salvate nel database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operazione riuscita",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TbClientCalls.class))),
        @ApiResponse(responseCode = "500", description = "Errore interno del server",
            content = @Content)
    })
    @GetMapping
    public List<TbClientCalls> getAllCalls() {
        return tbClientCallsService.getAllCalls();
    }
    @Operation(summary = "Recupera una chiamata per ID", description = "Restituisce i dettagli di una singola chiamata basata sul suo ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operazione riuscita",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TbClientCalls.class))),
        @ApiResponse(responseCode = "404", description = "Chiamata non trovata",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Errore interno del server",
            content = @Content)
    })
    @GetMapping("/{id}")
    public TbClientCalls getCallById(@PathVariable int id) {
        return tbClientCallsService.getCallById(id);
    }
    @Operation(summary = "Salva una nuova chiamata", description = "Salva una nuova chiamata nel database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Chiamata salvata con successo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))),
        @ApiResponse(responseCode = "400", description = "Dati della chiamata non validi",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Errore interno del server",
            content = @Content)
    })
    @PostMapping
    public int saveCall(@RequestBody TbClientCalls call) {
        return tbClientCallsService.saveCall(call);
    }
    @Operation(summary = "Aggiorna una chiamata esistente", description = "Aggiorna i dettagli di una chiamata esistente nel database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chiamata aggiornata con successo",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))),
        @ApiResponse(responseCode = "400", description = "Dati della chiamata non validi",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Errore interno del server",
            content = @Content)
    })
    @PutMapping
    public int updateCall(@RequestBody TbClientCalls call) {
        return tbClientCallsService.updateCall(call);
    }
    @DeleteMapping("/{id}")
    public int deleteCall(
        @Parameter(description = "ID della chiamata da eliminare", required = true)
        @PathVariable int id) {
        return tbClientCallsService.deleteCall(id);
    }
//    @DeleteMapping("/{id}")
//    public int deleteCall2(@PathVariable int id) {
//        return tbClientCallsService.deleteCall(id);
//    }
}