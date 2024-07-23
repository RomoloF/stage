package com.example.demo.model;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.parser.PreflightParser;

public class ValidatePDFA {
    public static void main(String[] args) {
        String pdfPath = "/home/romolofiorenza/git/pdf/pdf/src/main/java/com/example/demo/model/L7_SpringBootOfficePdf-a1b.pdf";

        File pdfFile = new File(pdfPath);
        PreflightParser parser = null;
		try {
			parser = new PreflightParser(pdfFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
            parser.parse();
            PreflightDocument document = parser.getPreflightDocument();
            document.validate();
            ValidationResult result = document.getResult();
            if (result.isValid()) {
                System.out.println("The document is a valid PDF/A-1b.");
            } else {
                System.out.println("The document is not a valid PDF/A-1b.");
                for (ValidationResult.ValidationError error : result.getErrorsList()) {
                    System.out.println(error.getErrorCode() + ": " + error.getDetails());
                }
            }
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
