package spire;

import java.io.IOException;
import java.util.List;

import javax.activation.FileDataSource;

import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;

/**
 * Classe per la validazione di documenti PDF/A.
 */
public class PdfValidator4 {

    /**
     * Valida se un file PDF è conforme allo standard PDF/A.
     *
     * @param filePath il percorso del file PDF da validare.
     * @return una stringa che descrive il risultato della validazione.
     */
    public static String validatePdfA(String filePath) {
        ValidationResult result = null;
        FileDataSource fd = new FileDataSource(filePath);
        PreflightParser parser = null;
		try {
			parser = new PreflightParser(fd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {           
            parser.parse();

            // Una volta che la convalida di sintassi è fatta,
            // Il parser può fornire un PreflightDocument
            // (Che eredita da PDDocument)
            // Questo documento viene lavorato alla fine della convalida PDF/A 
            
            
            PreflightDocument document = parser.getPreflightDocument();
            
            
            document.validate();

            // Ottieni il risultato della validazione
            result = document.getResult();
            System.out.println(">>>>>>non c'è errore>>>>>   "+result);
            document.close();
            
        } catch (SyntaxValidationException e) {
            // Il metodo parse può lanciare una SyntaxValidationException
            // Se il file PDF non può essere analizzato.
            // In questo caso, l'eccezione contiene un'istanza di ValidationResult
            result = e.getResult();
            System.out.println("result >>> "+result);
        } catch (IOException e) {
            return "Errore durante la lettura del file: " + e.getMessage();
        }

        // mostra il risultato della validazione
        if (result.isValid()) {
        	System.out.println("Result "+result);  // Mostra il risultato della validazione
            return "Il file " + filePath + " è un file PDF/A valido";
        } else {
            StringBuilder errorMessage = new StringBuilder("Il file " + filePath + " non è valido, errore(i) :\n");
            List<ValidationError> errors = result.getErrorsList();
            for (ValidationError error : errors) {
                errorMessage.append(error.getErrorCode()).append(" : ").append(error.getDetails()).append("\n");
                
                System.out.println(errorMessage);
                
            }
            return errorMessage.toString();
        }
    }

    /**
     * Metodo principale per eseguire la validazione di un file PDF/A.
     *
     * @param args i parametri della riga di comando.
     */
    public static void main(String[] args) {
//        //if (args.length != 1) {
//        //    System.err.println("Utilizzo: java PdfAValidator <percorso-del-file-pdf>");
//            System.exit(1);
//        }

        String filePath = "/home/romolofiorenza/stage/spire/src/main/java/pdf/Mod Ries-ac-contr-20191024.pdfConvertitoPDFA.pdf";   //args[0];
        System.out.println(validatePdfA(filePath));
    }
}
