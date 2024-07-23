package it.pdf;


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
public class PdfValidatior3 {

    /**
     * Valida se un file PDF è conforme allo standard PDF/A.
     *
     * @param filePath il percorso del file PDF da validare.
     * @return una stringa che descrive il risultato della validazione.
     * @throws IOException 
     */
    public static String validatePdfA(String filePath) throws IOException {
        ValidationResult result = null;
        FileDataSource fd = new FileDataSource(filePath);
        PreflightParser parser = new PreflightParser(fd);
        System.out.println("Stampo parser >>> "+parser);
        try {
            // Analizzare il file PDF con PreflightParser
            // che eredita dalla NonSequentialParser.
            // Alcuni controlli addizionali sono presenti 
            // per verificare una serie di requisiti PDF/A.
            // (Consistenza lunghezza Stream, EOL dopo qualche Keyword...)
            parser.parse();
            System.out.println("parser.parse()");
            
            // Una volta che la convalida di sintassi è fatta,
            // Il parser può fornire un PreflightDocument
            // (Che eredita da PDDocument)
            // Questo documento viene lavorato alla fine della convalida PDF/A 
            PreflightDocument document = parser.getPreflightDocument();
            System.out.println("Stampo document >>>  "+document);
            
            // Valida il documento PDF/A
               document.validate();
               System.out.println(document.getVersion());
            // Ottieni il risultato della validazione
            result = document.getResult();
            System.out.println("Result"+result);
            document.close();
        } catch (SyntaxValidationException e) {
            // Il metodo parse può lanciare una SyntaxValidationException
            // Se il file PDF non può essere analizzato.
            // In questo caso, l'eccezione contiene un'istanza di ValidationResult
            result = e.getResult();
        } catch (IOException e) {
            return "Errore durante la lettura del file: " + e.getMessage();
        }

        // mostra il risultato della validazione
        if (result.isValid()) {
            return "Il file " + filePath + " è un file PDF/A valido";
        } else {
            StringBuilder errorMessage = new StringBuilder("Il file " + filePath + " non è valido, errore(i) :\n");
            List<ValidationError> errors = result.getErrorsList();
            for (ValidationError error : errors) {
                errorMessage.append(error.getErrorCode()).append(" : ").append(error.getDetails()).append("\n");
            }
            return errorMessage.toString();
        }
    }

    /**
     * Metodo principale per eseguire la validazione di un file PDF/A.
     *
     * @param args i parametri della riga di comando.
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
       // if (args.length != 1) {
            System.err.println("Utilizzo: java PdfAValidator <percorso-del-file-pdf>");
           // System.exit(1);
       // }

        String filePath ="/home/romolofiorenza/stage/it.pdf/src/main/java/it/pdf/pdf/Allegato 1 - modello_dichiarazione_accessibilità_privatiPDFA.pdf"; //args[0];
        System.out.println(validatePdfA(filePath));
    }
}
