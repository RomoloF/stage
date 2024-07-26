package pdf.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pdf.entity.DocumentiOriginalePdf;
import pdf.service.DocumentiOriginalePdfService;

@Controller
@RequestMapping("/documenti-originali-pdf")
public class DocumentiOriginalePdfController {

    //@Autowired
    private DocumentiOriginalePdfService documentiOriginalePdfService;

    public DocumentiOriginalePdfController(DocumentiOriginalePdfService documentiOriginalePdfService) {
        this.documentiOriginalePdfService = documentiOriginalePdfService;
    }

//    @GetMapping
//    public ResponseEntity<List<DocumentiOriginalePdf>> getAllDocumentiOriginaliPdf() {
//        List<DocumentiOriginalePdf> documentiOriginaliPdf = (List<DocumentiOriginalePdf>) documentiOriginalePdfService.findAll();
//        return new ResponseEntity<>(documentiOriginaliPdf, HttpStatus.OK);
//    }

    @GetMapping("/documenti-pdf")
    //@ResponseBody
    public String listDocumentiPdf(Model model) {
        List<DocumentiOriginalePdf> documenti = (List<DocumentiOriginalePdf>) documentiOriginalePdfService.findAll();
        model.addAttribute("documenti", documenti);
        return "documentipdflist";
    }


    @GetMapping("/{id}")
    public ResponseEntity<DocumentiOriginalePdf> getDocumentiOriginaliPdfById(@PathVariable int id) {
        Optional<DocumentiOriginalePdf> documentiOriginalePdfOptional = documentiOriginalePdfService.findById(id);
        return documentiOriginalePdfOptional.map(documentiOriginalePdf -> new ResponseEntity<>(documentiOriginalePdf, HttpStatus.OK))
                                            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
// *****************************************************

// @PostMapping
//    public ResponseEntity<DocumentiOriginalePdf> createDocumentiOriginaliPdf(@RequestBody DocumentiOriginalePdf documentiOriginalePdf) {
//        documentiOriginalePdf = documentiOriginalePdfService.save(documentiOriginalePdf);
//        return new ResponseEntity<>(documentiOriginalePdf, HttpStatus.CREATED);
//    }
//


//    @PostMapping
//    public ResponseEntity<DocumentiOriginalePdf> createDocumentiOriginalePdf(@RequestParam("file") MultipartFile file,
//        @RequestParam("nomeDocumento") String nomeDocumento) throws IOException {
//        DocumentiOriginalePdf documentiOriginalePdf = new DocumentiOriginalePdf();
//     // documentiOriginalePdf.setNomeDocumento(nomeDocumento);
//     // Salva il documento nel database
//       documentiOriginalePdf = documentiOriginalePdfService.save(documentiOriginalePdf);
//	return new ResponseEntity<>(documentiOriginalePdf, HttpStatus.CREATED);
//
//    }

    //************************************************************************
    /**
     * Gestisce il caricamento del documento PDF.
     *
     * @param nomeFile il nome del documento
     * @param file il file PDF da caricare
     * @return il nome della vista da restituire
     */

//    //Questi due metodi funzionano ma non reindirizzano alla vista
//    @PostMapping
//    public String caricaDocumento(@RequestParam("nomeDocumento") String nomeFile,
//                                  @RequestParam("file") MultipartFile file) {
//        try {
//            documentiOriginalePdfService.salvaDocumento(nomeFile, file);
//        } catch (IOException e) {
//            e.printStackTrace();
//            // gestire l'errore (es. ritornare una pagina di errore)
//        }
//        return "redirect:/documenti-originali-pdf/successo"; // reindirizzare alla vista di successo
//    }
//
//    @GetMapping("/successo")
//    public String mostraPaginaDiSuccesso() {
//        return "vistaSuccessoSalvataggio";
//    }
//



    //************************************************************************
    //
    @PostMapping ("/salva-documento")
    public String caricaDocumento(@RequestParam("nomeDocumento") String nomeFile,
                                  @RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) {
        try {

        	// Verifica se il file è un PDF
            String contentType = file.getContentType();
            if (!contentType.startsWith("application/pdf")) {
                redirectAttributes.addFlashAttribute("errore", "Il file caricato non è un PDF.");
                return "<!DOCTYPE html>\n"
                		+ "<html lang=\"it\">\n"
                		+ "<head>\n"
                		+ "    <meta charset=\"UTF-8\">\n"
                		+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                		+ "    <title>Errore caricamento file</title>\n"
                		+ "    <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css\">\n"
                		+ "</head>\n"
                		+ "<body>\n"
                		+ "    <div class=\"container mt-5\">\n"
                		+ "        <div class=\"card text-center\">\n"
                		+ "            <div class=\"card-body\">\n"
                		+ "                <h5 class=\"card-title\">Errore nel caricamento del file</h5>\n"
                		+ "                <p class=\"card-text\">Il file caricato non è un file PDF. Assicurati di caricare un file con estensione .pdf.</p>\n"
                		+ "                <a href=\"/\" class=\"btn btn-primary\">Torna alla Home</a>\n"
                		+ "            </div>\n"
                		+ "        </div>\n"
                		+ "    </div>\n"
                		+ "\n"
                		+ "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js\"></script>\n"
                		+ "</body>\n"
                		+ "</html>\n"
                		+ "";
            }

        	// Salva il documento (assumendo che documentiOriginalePdfService.salvaDocumento gestisca i PDF)
            documentiOriginalePdfService.salvaDocumento(nomeFile, file);
            //redirectAttributes.addFlashAttribute("successo", "Documento caricato con successo!");
            return "<!DOCTYPE html>\n"
            		+ "<html lang=\"en\">\n"
            		+ "<head>\n"
            		+ "    <meta charset=\"UTF-8\">\n"
            		+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
            		+ "    <title>Salvataggio Completato</title>\n"
            		+ "    <!-- Link to Bootstrap CSS -->\n"
            		+ "    <link href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\" rel=\"stylesheet\">\n"
            		+ "</head>\n"
            		+ "<body>\n"
            		+ "\n"
            		+ "<div class=\"container mt-5\">\n"
            		+ "    <div class=\"card\">\n"
            		+ "        <div class=\"card-header\">\n"
            		+ "            Salvataggio Completato\n"
            		+ "        </div>\n"
            		+ "        <div class=\"card-body\">\n"
            		+ "            <div class=\"alert alert-success\" role=\"alert\">\n"
            		+ "                Il documento è stato salvato con successo!\n"
            		+ "            </div>\n"
            		+ "            <a href=\"/index\" class=\"btn btn-primary\">Torna alla pagina principale</a>\n"
            		+ "        </div>\n"
            		+ "    </div>\n"
            		+ "</div>\n"
            		+ "\n"
            		+ "<!-- Link to Bootstrap JS and dependencies -->\n"
            		+ "<script src=\"https://code.jquery.com/jquery-3.5.1.slim.min.js\"></script>\n"
            		+ "<script src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js\"></script>\n"
            		+ "<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js\"></script>\n"
            		+ "</body>\n"
            		+ "</html>\n"
            		+ "";

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errore", "Errore durante il caricamento del documento: " + e.getMessage());
            return "redirect:/documenti-originali-pdf/errore";
        }
    }

    @GetMapping("/successo")
    public String mostraPaginaDiSuccesso(Model model) {
        return "vistaSuccessoSalvataggio";
    }

    @GetMapping("/errore")
    public String mostraPaginaDiErrore(Model model) {
        return "vistaErroreSalvataggio";
    }


//    @GetMapping("/successo")
//    public String mostraPaginaDiSuccesso(Model model) {
//        String messaggioSuccesso = (String) model.getAttribute("successo");
//        model.addAttribute("messaggio", messaggioSuccesso);
//        return "vistaSuccessoSalvataggio";
//    }
//
//    @GetMapping("/errore")
//    public String mostraPaginaDiErrore(Model model) {
//        String messaggioErrore = (String) model.getAttribute("errore");
//        model.addAttribute("messaggio", messaggioErrore);
//        return "vistaErroreSalvataggio";
//    }




//    @PostMapping
//    public ResponseEntity<DocumentiOriginalePdf> createDocumentiOriginalePdf(@RequestParam("file") MultipartFile file,
//        @RequestParam("nomeDocumento") String nomeDocumento) throws IOException {
//        // ... (codice esistente per salvare le altre informazioni del documento)
//
//        // Salva il file nel file system
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//        Path path = Paths.get(uploadDirectory + fileName);
//        file.transferTo(path);
//
//        // Imposta il percorso del file nel documento
//        documentiOriginalePdf.setPercorsoFile(fileName);
//
//        // Salva il documento nel database
//        documentiOriginalePdf = documentiOriginalePdfService.save(documentiOriginalePdf);
//
//        return new ResponseEntity<>(documentiOriginalePdf, HttpStatus.CREATED);
//    }
    //**************************************************************************

    @PutMapping("/{id}")
    public ResponseEntity<DocumentiOriginalePdf> updateDocumentiOriginaliPdf(@PathVariable int id, @RequestBody DocumentiOriginalePdf documentiOriginalePdf) {
        documentiOriginalePdf.setId(id);
        documentiOriginalePdf = documentiOriginalePdfService.save(documentiOriginalePdf);
        return new ResponseEntity<>(documentiOriginalePdf, HttpStatus.OK);
    }






    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentiOriginaliPdf(@PathVariable int id) {
        documentiOriginalePdfService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
