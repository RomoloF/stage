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
        
//        return " <!DOCTYPE html>\n"
//        		+ "<html>\n"
//        		+ "<head>\n"
//        		+ "    <title>Caricamento File PDF</title>\n"
//        		+ "    <style>\n"
//        		+ "        body {\n"
//        		+ "            font-family: Arial, sans-serif;\n"
//        		+ "            text-align: center;\n"
//        		+ "            margin-top: 50px;\n"
//        		+ "        }\n"
//        		+ "        h1 {\n"
//        		+ "            color: #333;\n"
//        		+ "        }\n"
//        		+ "        p {\n"
//        		+ "            color: #666;\n"
//        		+ "        }\n"
//        		+ "        a {\n"
//        		+ "            display: inline-block;\n"
//        		+ "            padding: 10px 20px;\n"
//        		+ "            background-color: #4CAF50;\n"
//        		+ "            color: white;\n"
//        		+ "            text-decoration: none;\n"
//        		+ "            border-radius: 5px;\n"
//        		+ "        }\n"
//        		+ "    </style>\n"
//        		+ "</head>\n"
//        		+ "<body>\n"
//        		+ "    <h1>File PDF Caricato Correttamente!</h1>\n"
//        		+ "    <p>Il file è stato convertito in PDF/A-1b e salvato con successo.</p>\n"
//        		+ "    <a href=\"/\">Torna alla Home</a>\n"
//        		+ "</body>\n"
//        		+ "</html>\n"
//        		+ ""; 
//        }
}}
