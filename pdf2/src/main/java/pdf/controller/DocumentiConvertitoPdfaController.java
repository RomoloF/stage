package pdf.controller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pdf.entity.DocumentiConvertitoPdfa;
import pdf.entity.DocumentiOriginalePdf;
import pdf.service.DocumentiConvertitoPdfaService;
import pdf.service.PdfConversionService;

@Controller
@RequestMapping("/api/documenti-convertiti-pdfa")
public class DocumentiConvertitoPdfaController {

    @Autowired
    private DocumentiConvertitoPdfaService documentoConvertitoPdfaService;

    @Autowired
    private PdfConversionService pdfConversionService;

    /**
     * Carica e converte un file PDF in PDF/A, salva il file convertito nel filesystem
     * e nel database, e ritorna una vista di successo o di errore.
     *
     * @param file il file PDF da caricare
     * @return il nome della vista di successo o di errore
     */
    @PostMapping("/convertito-salvato-db")
    public String salvaDocumentoConvertitoPdfaVista(@RequestParam("file") MultipartFile file,Model model) {
        // Verifica se il file è vuoto
        if (file.isEmpty()) {            
            return "erroreCaricamento";
        }
        try {
            // Percorso di destinazione nel filesystem
            String destinazioneDir = "/home/romolofiorenza/git/pdf2/pdf2/src/main/resources/pdfconvertiti/";
            Path destinazionePath = Paths.get(destinazioneDir, file.getOriginalFilename());
            File destinazioneFile = destinazionePath.toFile();

            // Creare la directory di destinazione se non esiste
            if (!Files.exists(destinazionePath.getParent())) {
                Files.createDirectories(destinazionePath.getParent());
            }
           
            // Salva il file nel filesystem
            file.transferTo(destinazioneFile);
            System.out.println("Salvato file in > " + destinazioneFile);            
            // Percorso del file di output per la conversione
            String outputFilePath = destinazioneFile.getAbsolutePath().replace(".pdf", "_ConvertitoPDFA.pdf");

            //Salvo nel db l'originale 
            DocumentiOriginalePdf documentoOriginale=new DocumentiOriginalePdf();
            documentoOriginale.setNomeFile(outputFilePath);
            
            
            
            // Esegui la conversione
            pdfConversionService.convertToPdfA(destinazioneFile.getAbsolutePath(), outputFilePath);
            System.out.println("Ritorno dal service che ha eseguito la conversione.");

            // Creazione e salvataggio del documento convertito nel database entity (DocumentiConvertitoPdfa)
            DocumentiConvertitoPdfa documento = new DocumentiConvertitoPdfa();

            File fileConvertito = new File(outputFilePath);

            documento.setNomeFile(fileConvertito.getName());
            documento.setContenutoPdf(Files.readAllBytes(fileConvertito.toPath()));
            documento.setDataConvertito(new Date());
            documento.setDimensioneFile((int) fileConvertito.length());
            documento.setValidato(true); // TODO: Implementare il controllo del file PDF/A valido per il caricamento
           
            DocumentiConvertitoPdfa documentoSalvato = documentoConvertitoPdfaService.saveDocumentoConvertitoPdfa(documento);
            
            model.addAttribute("documento", documento);

            return "successoConversioneSalvataggio"; // Nome del file di vista (senza estensione)
        } catch (IOException e) {
            e.printStackTrace();
            return "erroreCaricamento";
        }
    }
    
    @GetMapping("/listaConvertiti")
    public String getDocumentiConvertitiPdfa(Model model) {
        List<DocumentiConvertitoPdfa> documentiConvertiti = (List<DocumentiConvertitoPdfa>) documentoConvertitoPdfaService.getAllDocumentiConvertitiPdfa();
        model.addAttribute("documentiConvertiti", documentiConvertiti);
 
        
        return "listaDocumentiConvertiti"; // Nome del file di vista (senza estensione)
    }
}
