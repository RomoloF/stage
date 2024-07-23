package com.example.demo.model;

import com.spire.pdf.conversion.PdfStandardsConverter;

public class SpiretoPDFA {
    public static void main(String[] args) {
        String inputFile = "/home/romolofiorenza/git/pdf/pdf/src/main/java/com/example/demo/model/L7_SpringBoot.pdf";
        String outputFile = inputFile+"ConvertitoPDFA"+".pdf";
        PdfStandardsConverter ps = new PdfStandardsConverter(inputFile);
        ps.toPdfA3B(outputFile);

        System.out.println("Conversione completata!");
        System.out.println("Il file è >>>>  "+outputFile);
    }
}


