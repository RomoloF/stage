package pdf.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pdf.entity.DocumentiConvertitoPdfa;
import pdf.service.DocumentiConvertitoPdfaService;
import pdf.service.PdfConversionService;

//@Controller
@RequestMapping("/api/documenti-convertiti-pdfa")
public class DocumentiConvertitoPdfaControllerERRORE {

    @Autowired
    private DocumentiConvertitoPdfaService documentoConvertitoPdfaService;
    @Autowired
    private PdfConversionService pdfConversionService;
    
    

    @PostMapping
    public ResponseEntity<DocumentiConvertitoPdfa> salvaDocumentoConvertitoPdfa(@RequestBody DocumentiConvertitoPdfa documentoConvertitoPdfa) {
        DocumentiConvertitoPdfa documentoSalvato = documentoConvertitoPdfaService.saveDocumentoConvertitoPdfa(documentoConvertitoPdfa);
        return new ResponseEntity<>(documentoSalvato, HttpStatus.CREATED);
    }

    // Metodo POST salvato nel db e ritorna una vista 
    @PostMapping
    @RequestMapping("/convertito-salvato-db")
    public String salvaDocumentoConvertitoPdfaVista(@RequestParam("file") MultipartFile file , Model model) {    	
    	System.out.println("File caricato ed è >>>> "+file);  
    	
        // Verifica se il file è vuoto
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
           // return "vistaSuccessoSalvataggio";
        } catch (IOException e) {
            e.printStackTrace();
            return "Errore durante il caricamento o la conversione del file: " + e.getMessage();
        }
    	    	
        DocumentiConvertitoPdfa documentoConvertitoPdfa;
		//DocumentiConvertitoPdfa documentoSalvato = documentoConvertitoPdfaService.saveDocumentoConvertitoPdfa(documentoConvertitoPdfa);
        //model.addAttribute("documentoConvertito", documentoSalvato);
        
        
        
        
     // Salva il file PDF/A nel database
        DocumentiConvertitoPdfa documentoConvertitoPdfaSalvato = new DocumentiConvertitoPdfa();
        //documentoConvertitoPdfaSalvato.setFilePdf(outputStream.toByteArray());
        documentoConvertitoPdfaSalvato = documentoConvertitoPdfaService.saveDocumentoConvertitoPdfa(documentoConvertitoPdfaSalvato);
        
        
        return "successoConversioneSalvataggio"; // Nome del file di vista (senza estensione)
    }
    //**********************************************************************************************************************************
    //@PostMapping("/caricaPdf")
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
    
    
    //**********************************************************************************************************************************
    @GetMapping("/listaConvertiti")
    public String getDocumentiConvertitiPdfa(Model model) {
        List<DocumentiConvertitoPdfa> documentiConvertiti = (List<DocumentiConvertitoPdfa>) documentoConvertitoPdfaService.getAllDocumentiConvertitiPdfa();
        model.addAttribute("documentiConvertiti", documentiConvertiti);
        
        System.out.println("Lista dei documenti convertiti"+documentiConvertiti);
        
        return "listaDocumentiConvertiti"; // Nome del file di vista (senza estensione)
    }
    
    
}

