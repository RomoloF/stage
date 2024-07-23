package spire;

import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;

import java.io.File;
import java.io.IOException;

public class ValidatePDFA {
    public static void main(String[] args) {
        String pdfPath = "/home/romolofiorenza/stage/spire/src/main/java/pdf/Mod Ries-ac-contr-20191024.pdfConvertitoPDFA.pdf";

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
