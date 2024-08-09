package it.cript.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import it.cript.logicacripto.GeneratoreKey;

@RestController
@RequestMapping("/api/keys")
public class KeyController {
	
	private GeneratoreKey generatoreKey;

	public KeyController(GeneratoreKey generatoreKey) {
		super();
		this.generatoreKey = generatoreKey;
	}

    @Operation(summary = "Genera una chiave segreta codificata in Base64")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chiave segreta generata con successo", 
            content = {@Content(mediaType = "application/json",           
            schema = @Schema(implementation = String.class) 
            )}),
        	
    })
    @GetMapping("/generate")
    public String generateSecretKey() {
        try {
            return generatoreKey.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
