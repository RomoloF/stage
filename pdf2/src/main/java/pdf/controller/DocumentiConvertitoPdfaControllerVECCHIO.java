package pdf.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
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
import org.springframework.web.multipart.MultipartFile;

import pdf.entity.DocumentiConvertitoPdfa;
import pdf.entity.DocumentiOriginalePdf;
import pdf.service.DocumentiConvertitoPdfaService;
import pdf.service.PdfConversionService;

//@Controller
@RequestMapping("/api/documenti-convertiti-pdfa")
public class DocumentiConvertitoPdfaControllerVECCHIO {

    @Autowired
    private DocumentiConvertitoPdfaService documentoConvertitoPdfaService;

    @Autowired
    private PdfConversionService pdfConversionService;

    /**
     * Salva un documento convertito in formato PDF/A.
     *
     * @param documentoConvertitoPdfa il documento da salvare
     * @return ResponseEntity contenente il documento salvato e lo stato HTTP
     */
    @PostMapping
    public ResponseEntity<DocumentiConvertitoPdfa> salvaDocumentoConvertitoPdfa(@RequestBody DocumentiConvertitoPdfa documentoConvertitoPdfa) {
        DocumentiConvertitoPdfa documentoSalvato = documentoConvertitoPdfaService.saveDocumentoConvertitoPdfa(documentoConvertitoPdfa);
        return new ResponseEntity<>(documentoSalvato, HttpStatus.CREATED);
    }

    /**
     * Carica e converte un file PDF in PDF/A, salva il file convertito nel filesystem
     * e nel database, e ritorna una vista di successo o di errore.
     *
     * @param file il file PDF da caricare
     * @param model il modello per aggiungere attributi alla vista
     * @return il nome della vista di successo o di errore
     */
    @PostMapping("/convertito-salvato-db")
    public String salvaDocumentoConvertitoPdfaVista(@RequestParam("file") MultipartFile file) {
        // Verifica se il file è vuoto
        if (file.isEmpty()) {
           // model.addAttribute("errore", "File vuoto, per favore carica un file PDF.");
            System.out.println("Il file caricato è vuoto ************");
            return "erroreCaricamento";
        }

        try {
            // Percorso di destinazione nel filesystem
            String destinazionePath = "/home/romolofiorenza/git/pdf2/pdf2/src/main/resources/pdfconvertiti/" + file.getOriginalFilename();
            File destinazioneFile = new File(destinazionePath);

            // Salva il file nel filesystem
            file.transferTo(destinazioneFile);
            System.out.println("Salvato file in >"+destinazioneFile);
            
//            // Percorso del file di output per la conversione
//            String outputFilePath = destinazionePath.replace(".pdf", "ConvertitoPDFA.pdf");
            	
            // Percorso del file di output per la conversione
            String outputFilePath = destinazioneFile.getAbsolutePath().replace(".pdf", "ConvertitoPDFA.pdf");
            
            
            
            
            // Esegui la conversione
            pdfConversionService.convertToPdfA(destinazionePath, outputFilePath);
            System.out.println("Ritorno dal service che ha esequito la conversione .");
            
            // Creazione e salvataggio del documento convertito nel database
            DocumentiConvertitoPdfa documento = new DocumentiConvertitoPdfa();
            System.out.println("Il documento convertito è > "+outputFilePath);
            ///***********************************************************
            //Ho un dubbio questo metodo ha aggiunto un metodo setFilePath
           // documentoConvertitoPdfa.setFilePath(outputFilePath);
            //************************************************************
                       
            File fileconvertito = new File(outputFilePath);
           

            //PDDocument document = PDDocument.load(file);
                
                documento.setNomeFile(fileconvertito.getName());
                documento.setContenutoPdf(file.getBytes());
                documento.setDataConvertito(new Date());                               
                documento.setDimensioneFile((int) file.getSize());
                                                                                   // documentiOriginalePdfRepository.save(documento);
                
            System.out.println("**************************************");
            System.out.println(" il documento caricato è");
            System.out.println(documento);
            System.out.println("**************************************");
            
            DocumentiConvertitoPdfa documentoSalvato = documentoConvertitoPdfaService.saveDocumentoConvertitoPdfa(documento);

            // Aggiungi il documento salvato al modello
         //   model.addAttribute("documentoConvertito", documento);

            return "successoConversioneSalvataggio"; // Nome del file di vista (senza estensione)
        } catch (IOException e) {
            e.printStackTrace();
       //     model.addAttribute("errore", "Errore durante il caricamento o la conversione del file: " + e.getMessage());
            return "erroreCaricamento";
        }
    }

    /**
     * Ottiene una lista di tutti i documenti convertiti in PDF/A e li aggiunge al modello.
     *
     * @param model il modello per aggiungere attributi alla vista
     * @return il nome della vista che mostra la lista dei documenti convertiti
     */
    @GetMapping("/listaConvertiti")
    public String getDocumentiConvertitiPdfa(Model model) {
        List<DocumentiConvertitoPdfa> documentiConvertiti = (List<DocumentiConvertitoPdfa>) documentoConvertitoPdfaService.getAllDocumentiConvertitiPdfa();
        model.addAttribute("documentiConvertiti", documentiConvertiti);

        System.out.println("Lista dei documenti convertiti" + documentiConvertiti);

        return "listaDocumentiConvertiti"; // Nome del file di vista (senza estensione)
    }
}
