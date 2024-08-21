package it.cript.logicacripto;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
@Component
/**
 * Questa classe se si chiama il suo metodo statico generateKey ritorna una Stringa
 * codificata in base64.
 * Inoltre deve essere utilizzata solo per la creazione della secretKey.
 * Lo annotata come component per poterla injettare in un controller di Spring.
 */
public class GeneratoreKey {
	// Algoritmo da utilizzare per la creazione del secretKey
	private static final String ALGORITHM = "AES";
	// Generare una chiave valida
	public static String generateKey() throws Exception {
		// Generatore di chiavi per AES-256
		KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
		// Inizializzare il generatore di chiavi per AES-256
		keyGen.init(256); // AES-256	
		// Generare una chiave segreta
		SecretKey secretKey = keyGen.generateKey();
		 // Codifica la chiave in Base64
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        // Ritorna la chiave codificata in Base64
		return encodedKey;   //secretKey
	}

}
