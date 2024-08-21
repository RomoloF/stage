package it.cript.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CryptoViewController {
	
	// Questo controller si occupa di mostrare le pagine HTML per la criptazione, decriptazione e la generazione chiave.
	@GetMapping("/")
	public String showIndexPage() {
	    return "index";
	}
	 
    @GetMapping("/encrypt")
    public String showEncryptPage() {
        return "encrypt";
    }
    @PostMapping("/encrypt")
    public String handleEncrypt(
            @RequestParam String clientID, 
            @RequestParam String plainText, 
            Model model) {
      //  CryptoControllerFunzionanteSalvoSuTbClientCalls response = new CryptoControllerFunzionanteSalvoSuTbClientCalls();
        
        // Qui puoi chiamare il servizio per criptare il testo e popolare l'oggetto response
        // response = ...

        model.addAttribute("response", "CIAO VENGO DA ENCRYPT");
        return "encrypt";
    }

    @GetMapping("/decrypt")
    public String showDecryptPage() {
        return "decrypt";
    }

    @GetMapping("/generateKey")
    public String showGenerateKeyPage() {
        return "generate_key";
    }
}

// Queste pagine sono usate per visualizzare le form di criptazione, decriptazione e generazione chiave.