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

   
    @PostMapping ("/salva-documento")
    public String caricaDocumento(@RequestParam("nomeDocumento") String nomeFile,
                                  @RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) {
        try {

        	// Verifica se il file è un PDF
            String contentType = file.getContentType();
            if (!contentType.startsWith("application/pdf")) {
                redirectAttributes.addFlashAttribute("errore", "Il file caricato non è un PDF.");
                return "errore";
            }

        	// Salva il documento (assumendo che documentiOriginalePdfService.salvaDocumento gestisca i PDF)
            documentiOriginalePdfService.salvaDocumento(nomeFile, file);
            //redirectAttributes.addFlashAttribute("successo", "Documento caricato con successo!");
            return "vistaSuccessoSalvataggio";

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
