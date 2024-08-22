package it.cript.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	private String secretKeyStrig;
	
	public EncryptStringController() throws Exception {
		// Genera una chiave AES all'avvio del controller (in una applicazione reale la
		// chiave andrebbe gestita in modo sicuro)
		this.secretKey = KeyGenerator.getInstance(ALGORITHM).generateKey();
		this.secretKeyStrig = secretKeyToString(secretKey);
		
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
			System.out.println(encryptForm.getPlainText());
			// Recupera i parametri passati nel form
			String plainText = encryptForm.getPlainText();
			String clientID = encryptForm.getClientID();

//		//  Aggiungi il testo crittografato al modello per la visualizzazione
//			model.addAttribute("encryptedText", encryptedText);
//			model.addAttribute("plainText", plainText);
//			model.addAttribute("clientID", clientID);
//			model.addAttribute("secretKey", secretKey);
			
			// Reindirizza alla vista con il testo crittografato e altri parametri
			return "redirect:/crypto/encrypt?encryptedText=" + encryptedText + "&secretKey=" + secretKeyStrig
					+ "&clientID=" + clientID + "&plainText=" + plainText;

			// Se si verifica un errore durante la crittografia, aggiungi un messaggio di
			// errore al modello e reindirizza alla vista
		} catch (Exception e) {
			model.addAttribute("error", "Errore durante la crittografia: " + e.getMessage());
			return "encrypt";
		}
	}

	@GetMapping("/decrypt")
	public String decryptForm(Model model) {
		model.addAttribute("decryptForm", new DecryptForm());
		System.out.println("Sono entrato in decript adesso devo visualizzare decryptTymeleaf.html");
		return "decryptTymeleaf";
	}

	@PostMapping("/decrypt")
	public String decrypt(@ModelAttribute DecryptForm decryptForm, Model model) {
		try {
			// Esegui la decrittografia del testo crittografato
			
			String secretKeyImput=decryptForm.getKeyString();
			System.out.println("sono entrato in decrypt provengo dal post");
			System.out.println("Stampo secretKey =  "+secretKeyImput);
			System.out.println("Stampo EncryptedText =  "+decryptForm.getEncryptedText());
			System.out.println();
			String decryptedText = decrypt(decryptForm.getEncryptedText());
			System.out.println("Stampo decryptedText =  "+decryptedText);
			
			System.out.println("Text decriptedText =  "+decryptedText);
			// Aggiungi il testo decrittografato al modello per la visualizzazione
			model.addAttribute("decryptedText", decryptedText);
			model.addAttribute("encryptedText", decryptForm.getEncryptedText());
			//model.addAttribute("clientID", decryptForm.getClientID());

			// Reindirizza alla vista con il testo decrittografato
			//return "redirect:/crypto/decrypted?decryptedText=" + decryptedText;
			return "redirect:/crypto/decrypted";
			
		} catch (Exception e) {
			model.addAttribute("error", "Errore durante la decrittografia: " + e.getMessage());
			return "decryptTymeleaf";
		}
	}

	/**
	 * Metodo per crittografare il testo in chiaro.
	 *
	 * @param plainText Il testo in chiaro da crittografare
	 * @return Il testo crittografato
	 * @throws Exception In caso di errore durante la crittografia
	 */
	private String encrypt(String plainText) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

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
		
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(decodedBytes));
		
		return new String(decryptedBytes);
	}

	
	private String decodeBase64(String encodedData) {
		try {
			// Rimuovi gli spazi bianchi e i caratteri di ritorno a capo
			encodedData = encodedData.replaceAll("\\s", "");
			
            // Decodifica i dati Base64
            byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
            // Converte i byte decodificati in una stringa
            return new String(decodedBytes);
	}catch (IllegalArgumentException e) {
        // Se i dati non sono validi Base64, lancia un'eccezione
        throw new IllegalArgumentException("Errore nella decodifica dei dati Base64: " + e.getMessage(), e);
    }
	}
	
	
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
}
