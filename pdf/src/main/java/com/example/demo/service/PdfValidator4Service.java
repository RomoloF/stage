package com.example.demo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.activation.FileDataSource;

import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Classe per la validazione di documenti PDF/A.
 */
@Service
public class PdfValidator4Service {

    /**
     * Valida se un file PDF è conforme allo standard PDF/A.
     *
     * @param file il file PDF da validare.
     * @return una stringa che descrive il risultato della validazione.
     */
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

        } catch (SyntaxValidationException e) {
            result = e.getResult();
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
            StringBuilder errorMessage = new StringBuilder("Il file non è valido, errore(i) :\n");
            List<ValidationError> errors = result.getErrorsList();
            for (ValidationError error : errors) {
                errorMessage.append(error.getErrorCode()).append(" : ").append(error.getDetails()).append("\n");
            }
            return errorMessage.toString();
        }
    }
}
