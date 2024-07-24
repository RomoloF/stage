package pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import pdf.service.PdfValidatorService;



@Controller
@RequestMapping("/pdf2")
public class PdfValidationController2 {

    @Autowired
    private PdfValidatorService pdfValidatorService;



    @PostMapping("/validate")
    public ModelAndView validatePdf(@RequestParam("file") MultipartFile file) {
        String validationMessage = pdfValidatorService.validatePdfA(file);
        ModelAndView modelAndView = new ModelAndView("validationResult");
        modelAndView.addObject("message", validationMessage);
        return modelAndView;
    }
}
