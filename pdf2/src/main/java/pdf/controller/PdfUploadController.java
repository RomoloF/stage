package pdf.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pdf.service.PdfConversionService;



@Controller
@RequestMapping("/api/pdf")
public class PdfUploadController {

    private final PdfConversionService pdfConversionService;


    public PdfUploadController(PdfConversionService pdfConversionService) {
        this.pdfConversionService = pdfConversionService;
    }

    @PostMapping("/caricaPdf")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
    	System.out.println("File caricato ed è >>>> "+file);
    	
        if (file.isEmpty()) {
            return "File vuoto, per favore carica un file PDF.";
        }

        try {
            // Percorso di destinazione nel filesystem
            String destinazionePath = "/home/romolofiorenza/git/pdf2/pdf2/src/main/resources/pdfconvertiti/" + file.getOriginalFilename();
            File destinazioneFile = new File(destinazionePath);

            // Salva il file nel filesystem
            file.transferTo(destinazioneFile);

            // Percorso del file di output per la conversione
            String outputFilePath = destinazionePath + "ConvertitoPDFA" + ".pdf";

            // Esegui la conversione
            pdfConversionService.convertToPdfA(destinazionePath, outputFilePath);

           // return "Conversione completata! Il file convertito è: " + outputFilePath;
            
            return "vistaSuccessoSalvataggio";
            		
            		
            
            
            
        } catch (IOException e) {
            e.printStackTrace();
            return "Errore durante il caricamento o la conversione del file: " + e.getMessage();
        }
    }
}

