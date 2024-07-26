package pdf.controller;

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
import org.springframework.web.bind.annotation.RestController;

import pdf.entity.DocumentiConvertitoPdfa;
import pdf.service.DocumentiConvertitoPdfaService;

@Controller
@RequestMapping("/api/documenti-convertiti-pdfa")
public class DocumentiConvertitoPdfaController {

    @Autowired
    private DocumentiConvertitoPdfaService documentoConvertitoPdfaService;

    @PostMapping
    public ResponseEntity<DocumentiConvertitoPdfa> salvaDocumentoConvertitoPdfa(@RequestBody DocumentiConvertitoPdfa documentoConvertitoPdfa) {
        DocumentiConvertitoPdfa documentoSalvato = documentoConvertitoPdfaService.saveDocumentoConvertitoPdfa(documentoConvertitoPdfa);
        return new ResponseEntity<>(documentoSalvato, HttpStatus.CREATED);
    }

    // Metodo POST salvato nel db e ritorna una vista 
    @PostMapping
    @RequestMapping("/api/documenti-convertiti-pdfa/convertito-salvato")
    public String salvaDocumentoConvertitoPdfaVista(@RequestBody DocumentiConvertitoPdfa documentoConvertitoPdfa, Model model) {
        DocumentiConvertitoPdfa documentoSalvato = documentoConvertitoPdfaService.saveDocumentoConvertitoPdfa(documentoConvertitoPdfa);
        model.addAttribute("documentoConvertito", documentoSalvato);
        return "successoConversioneSalvataggio"; // Nome del file di vista (senza estensione)
    }
    @GetMapping("/listaConvertiti")
    public String getDocumentiConvertitiPdfa(Model model) {
        List<DocumentiConvertitoPdfa> documentiConvertiti = (List<DocumentiConvertitoPdfa>) documentoConvertitoPdfaService.getAllDocumentiConvertitiPdfa();
        model.addAttribute("documentiConvertiti", documentiConvertiti);
        
        System.out.println("Lista dei documenti convertiti"+documentiConvertiti);
        
        return "listaDocumentiConvertiti"; // Nome del file di vista (senza estensione)
    }
}

