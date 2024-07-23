package com.example.demo.controller;

import com.example.demo.service.PdfValidator4Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * Controller per gestire le richieste di validazione dei file PDF/A.
 */
@Controller
@RequestMapping("/pdf")
public class PdfValidationController {

    @Autowired
    private PdfValidator4Service pdfValidator4Service;

    /**
     * Metodo per gestire l'upload e la validazione di un file PDF/A.
     *
     * @param file il file PDF caricato.
     * @return una vista con il risultato della validazione.
     */

    
    @PostMapping("/validate")
    public ModelAndView validatePdf(@RequestParam("file") MultipartFile file) throws IOException {
        String validationMessage = "";
        validationMessage = pdfValidator4Service.validatePdfA(file);
        ModelAndView modelAndView = new ModelAndView("validationResult");
        modelAndView.addObject("message", validationMessage);
        return modelAndView;
    }
    
}
