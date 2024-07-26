package pdf.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.activation.FileDataSource;

import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfValidatorService {

    public String validatePdfA(MultipartFile file) {
        ValidationResult result = null;
        File tempFile = null;
        PreflightParser parser = null;

        try {
            // Creare un file temporaneo
            tempFile = File.createTempFile("tempPdf", ".pdf");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }

            FileDataSource fd = new FileDataSource(tempFile);
            parser = new PreflightParser(fd);

            parser.parse();

            PreflightDocument document = parser.getPreflightDocument();
            document.validate();

            // Ottieni il risultato della validazione
            result = document.getResult();
            document.close();

        } catch (IOException e) {
            return "Errore durante la lettura del file: " + e.getMessage();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }

        // Mostra il risultato della validazione
        if (result.isValid()) {
            return "Il file è un file PDF/A valido";
        } else {
//            StringBuilder errorMessage = new StringBuilder("Il file non è valido, errore(i) :\n \n \n");
//            for (ValidationError error : result.getErrorsList()) {
//                errorMessage.append("\n");
//                errorMessage.append("Codice errore: ").append(error.getErrorCode()).append("\n");
//                errorMessage.append("Dettagli: ").append(error.getDetails()).append("\n");
//            }
        	 String errorMessage = "Il file non è valido, errore(i) :\n \n \n";
        	    for (ValidationError error : result.getErrorsList()) {
        	    	System.out.println();
        	        errorMessage += "\n";
        	        System.out.println("Codice errore:   "+error.getErrorCode()+"\n");
        	        errorMessage += "Codice errore: " + error.getErrorCode() + "\n";
        	        System.out.println("Dettagli:    "+error.getDetails()+"\n");
        	        errorMessage += "Dettagli: " + error.getDetails() + "\n";
        	    }

            return errorMessage.toString();
        }
    }
}
