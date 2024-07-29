package pdf.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pdf.service.PdfConversionService;



@Controller
@RequestMapping("/api/pdf")
public class PdfConversionController {

    private final PdfConversionService pdfConversionService;


    public PdfConversionController(PdfConversionService pdfConversionService) {
        this.pdfConversionService = pdfConversionService;
    }

    @PostMapping("/convert")
    public String convertToPdfA(@RequestParam String inputFile) {
        String outputFile = inputFile + "ConvertitoPDFA" + ".pdf";
        pdfConversionService.convertToPdfA(inputFile, outputFile);
        return "documentipdflist";
        
//        
}}
