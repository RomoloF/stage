package it.cript.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.cript.model.Response;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Controller
@RequestMapping("/crypto")
public class EncryptStringController {

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
	private static final int KEY_SIZE = 256;
	private SecretKey secretKey;
	private String secretKeyString;
	private String decryptedText;
	public String redirectString;
	public EncryptStringController() throws Exception {
		// Genera una chiave AES all'avvio del controller (in una applicazione reale la
		// chiave andrebbe gestita in modo sicuro)
		this.secretKey = KeyGenerator.getInstance(ALGORITHM).generateKey();
		this.secretKeyString = secretKeyToString(secretKey);
		
	}
	 /**
     * Converte una SecretKey in una stringa Base64.
     *
     * @param secretKey la chiave segreta da convertire
     * @return la rappresentazione Base64 della chiave
     */
    public String secretKeyToString(SecretKey secretKey) {
        // Ottieni i byte grezzi della chiave
        byte[] rawData = secretKey.getEncoded();
        
        // Converte i byte in una stringa Base64
        return Base64.getEncoder().encodeToString(rawData);
    }
	@GetMapping("/encrypt")
	public String encryptForm(@RequestParam(value = "encryptedText", required = false) String encryptedText,
			@RequestParam(value = "secretKey", required = false) String secretKey,
			@RequestParam(value = "clientID", required = false) String clientID,
			@RequestParam(value = "plainText", required = false) String plainText, Model model) {
		model.addAttribute("encryptForm", new EncryptForm());
		// Se il testo crittografato è presente nel parametro di query, aggiungilo al
		// modello
		if (encryptedText != null && !encryptedText.isEmpty()) {
			model.addAttribute("encryptedText", encryptedText);
		}

		// Se la chiave AES è presente nel parametro di query, aggiungila al modello
		if (secretKey != null && !secretKey.isEmpty()) {
			model.addAttribute("secretKey", secretKey);
		}

		// Se l'ID del client è presente nel parametro di query, aggiungilo al modello
		if (clientID != null && !clientID.isEmpty()) {
			model.addAttribute("clientID", clientID);
		}

		if (plainText != null && !plainText.isEmpty()) {
			model.addAttribute("plainText", plainText);
		}
		model.addAttribute("plainText", plainText);
		
		// Assicurati che questo corrisponda al nome del file.html
		return "encryptTymeleaf";// Assicurati che questo corrisponda al nome del file .html
	}

	@PostMapping("/encrypt")
	public String encrypt(@ModelAttribute EncryptForm encryptForm, Model model) {
		try {
			
			
			// Esegui la crittografia del testo in chiaro
			String encryptedText = encrypt(encryptForm.getPlainText());
			// Stampa il testo crittografato
			System.out.println(encryptForm.getPlainText());
			// Recupera i parametri passati nel form
			String plainText = encryptForm.getPlainText();
			String clientID = encryptForm.getClientID();
		
			// Reindirizza alla vista con il testo crittografato e altri parametri
			return "redirect:/crypto/encrypt?encryptedText=" + encryptedText + "&secretKey=" + secretKeyString
					+ "&clientID=" + clientID + "&plainText=" + plainText;

			// Se si verifica un errore durante la crittografia, aggiungi un messaggio di
			// errore al modello e reindirizza alla vista
		} catch (Exception e) {
			model.addAttribute("error", "Errore durante la crittografia: " + e.getMessage());
			return "encrypt";
		}
	}
//********************************************************************************************************************************
//********************************************************************************************************************************	
	// Decrittazione  GET
	
	@GetMapping("/decrypt")
	public String decryptForm(
			@RequestParam(value = "encryptedText", required = false)String encryptText, 
			@RequestParam(value = "keyString", required = false) String keyString,           
            @RequestParam(value = "decryptedText", required = false) String decryptedText, 
            Model model) {
		model.addAttribute("decryptForm", new DecryptForm());
		
		// Aggiungi gli attributi al modello solo se non nulli e non vuoti
		if (encryptText!= null && !encryptText.isEmpty()) {
            model.addAttribute("encryptedText", encryptText);
        }
		if (keyString!= null &&!keyString.isEmpty()) {
            model.addAttribute("keyString", keyString);
        }
		if (decryptedText!= null && !decryptedText.isEmpty()) {
            model.addAttribute("decryptedText", decryptedText);
        }
		 // Log per il debug
		System.out.println("Sono entrato in decript adesso devo visualizzare decryptTymeleaf.html");
		System.out.println("Questi sono i parametri ricevuti \n ");
		System.out.println("encryptedText: "+encryptText);
		System.out.println("keyString: "+keyString);
		System.out.println("decryptedText: "+decryptedText);
		System.out.println("redirectString: "+redirectString);
		
		// Log per vedere cosa c'è nel modello
	    System.out.println("Attributi nel modello: " + model.asMap());
	    
		return "decryptTymeleaf";// Assicurati che questo template esista in src/main/resources/templates/
	}

	 // Endpoint per decriptare un testo
	@PostMapping(value = "/decrypt", produces = "application/json")
	public String decrypt(@ModelAttribute DecryptForm decryptForm, Model model) {
	    try {
	        String base64String = decryptForm.getEncryptedText().replaceAll("\\s", "");
	        
	        // Aggiunge padding se mancante
	        if (base64String.length() % 4 != 0) {
	            base64String = base64String + "=".repeat(4 - (base64String.length() % 4));
	        }
	        
	        // Decodifica la stringa Base64
	        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
	        
	        // Inizializza il cipher per la decrittazione
	        SecretKey key = getKeyFromString(decryptForm.getKeyString());
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        
	        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
	        String decryptedText = new String(decryptedBytes);

	        // Restituisce il risultato con redirect
	        return "redirect:/crypto/decrypt?"
	            + "encryptedText=" + decryptForm.getEncryptedText() 
	            + "&keyString=" + secretKeyToString(key)       
	            + "&decryptedText=" + decryptedText;

	    } catch (IllegalArgumentException e) {
	        System.err.println("Errore durante la decodifica Base64: " + e.getMessage());
	        model.addAttribute("errorMessage", "Errore durante la decodifica del testo criptato.");
	        return "redirect:/crypto/decrypt";
	    } catch (Exception e) {
	        System.err.println("Errore durante la decrittazione: " + e.getMessage());
	        model.addAttribute("errorMessage", "Errore durante la decrittazione del testo.");
	        return "redirect:/crypto/decrypt";
	    }
	}

	
//********************************************************************************************************
	
	
	
 // Metodo per convertire una stringa Base64 in una chiave AES
    private SecretKey getKeyFromString(String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
     // private static final String ALGORITHM = "AES";
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }
	//********************************************************************************************************	
	/**
	 * Metodo per crittografare il testo in chiaro.
	 *
	 * @param plainText Il testo in chiaro da crittografare
	 * @return Il testo crittografato
	 * @throws Exception In caso di errore durante la crittografia
	 */
	private String encrypt(String plainText) throws Exception {
		// Creo una chiave AES da stringa Base64
    	//SecretKey key = getKeyFromString(keyString);
		
		// private static final String ALGORITHM = "AES";
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}
	
	
//************************************************************************************************
	
	
	//Commentato
	/**
	 * Metodo per decrittografare il testo crittografato.
	 *
	 * @param encryptedText Il testo crittografato da decrittografare
	 * @return Il testo decrittografato
	 * @throws Exception In caso di errore durante la decrittografia
	 */
	private String decrypt(String encryptedText) throws Exception {
		
		String encodedData = "CorruptedOrInvalidBase64Data"; // Dati Base64 non validi
	    try {
	        String decodedData = decodeBase64(encryptedText);
	        
	        System.out.println("Decoded Data: " + decodedData);
	    } catch (IllegalArgumentException e) {
	        System.err.println("Error decoding Base64 data: " + e.getMessage());
	    }
	    
		//private static final String ALGORITHM = "AES";
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		
		byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
		
		
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(decodedBytes));
		
		
		return new String(decryptedBytes);
	}
///**********************************************************************************************
	private String decodeBase64(String encodedData) {
		try {
			// Rimuovi gli spazi bianchi e i caratteri di ritorno a capo
			//encodedData = encodedData.replaceAll("\\s", "");
			
            // Decodifica i dati Base64
            byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
            // Converte i byte decodificati in una stringa
            return new String(decodedBytes);
            
	}catch (IllegalArgumentException e) {
        // Se i dati non sono validi Base64, lancia un'eccezione
        throw new IllegalArgumentException("Errore nella decodifica dei dati Base64: " + e.getMessage(), e);
    }
	}
	
//	private String decrypt(String encryptedText, String keyString) throws Exception {
//	    // Decodifica la chiave fornita dall'utente
//	    byte[] decodedKey = Base64.getDecoder().decode(keyString);
//	    
//	    // Crea una chiave segreta da utilizzare per la decrittografia
//	    SecretKey secretKey = new SecretKeySpec(decodedKey, ALGORITHM);
//	    
//	    Cipher cipher = Cipher.getInstance(ALGORITHM);
//	    cipher.init(Cipher.DECRYPT_MODE, secretKey);
//	    
//	    byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
//	    byte[] decryptedBytes = cipher.doFinal(decodedBytes);
//	    
//	    return new String(decryptedBytes);
//	}
//**************************************************************************************************************	
	// Classe EncryptForm
	
	public static class EncryptForm {
		private String clientID;
		private String plainText;

		// Getters e Setters
		public String getClientID() {
			return clientID;
		}

		public void setClientID(String clientID) {
			this.clientID = clientID;
		}

		public String getPlainText() {
			return plainText;
		}

		public void setPlainText(String plainText) {
			this.plainText = plainText;
		}
	}
//**************************************************************************************************************
	// Classe DecryptForm
	
	public static class DecryptForm {
		private String encryptedText;
		private String keyString; // In un'applicazione reale, si potrebbe passare la chiave come stringa base64 o
									// da un'altra fonte

		// Getters e Setters
		public String getEncryptedText() {
			return encryptedText;
		}

		public void setEncryptedText(String encryptedText) {
			this.encryptedText = encryptedText;
		}

		public String getKeyString() {
			return keyString;
		}

		public void setKeyString(String keyString) {
			this.keyString = keyString;
		}
	}
//****************************************************************************************************************	
}
