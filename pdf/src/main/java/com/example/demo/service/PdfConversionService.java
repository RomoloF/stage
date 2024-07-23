package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.spire.pdf.conversion.PdfStandardsConverter;

@Service
public class PdfConversionService {

//    private final PdfStandardsConverter pdfStandardsConverter;
//
//    @Autowired
//    public PdfConversionService(PdfStandardsConverter pdfStandardsConverter) {
//        this.pdfStandardsConverter = pdfStandardsConverter;
//    }



	public void convertToPdfA(String inputFile, String outputFile) {
		PdfStandardsConverter pdfStandardsConverter = new PdfStandardsConverter(inputFile);
        //pdfStandardsConverter.load(inputFile);
        pdfStandardsConverter.toPdfA1B(outputFile);
        System.out.println("Conversione completata!");
        System.out.println("Il file è >>>>  " + outputFile);
    }
}
