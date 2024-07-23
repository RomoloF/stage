package spire;

import com.spire.pdf.*;
import com.spire.pdf.conversion.PdfStandardsConverter;
import com.spire.pdf.graphics.PdfMargins;

import java.awt.geom.Dimension2D;

public class SpiretoPDFA {
    public static void main(String[] args) {
        String inputFile = "/home/romolofiorenza/stage/spire/src/main/java/pdf/Mod Ries-ac-contr-20191024.pdf";
        String outputFile = inputFile+"ConvertitoPDFA"+".pdf";
        PdfStandardsConverter ps = new PdfStandardsConverter(inputFile);
        ps.toPdfA3B(outputFile);
    }
}
    

// Usage: java SpiretoPDFA <input-pdf-file> <output-pdf-file>