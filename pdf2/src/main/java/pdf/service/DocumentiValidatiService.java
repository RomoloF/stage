package pdf.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pdf.entity.DocumentiValidati;
import pdf.repository.DocumentiValidatiRepository;


//@Service
public class DocumentiValidatiService {

    private final DocumentiValidatiRepository documentiValidatiRepository;

    public DocumentiValidatiService(DocumentiValidatiRepository documentiValidatiRepository) {
        this.documentiValidatiRepository = documentiValidatiRepository;
    }

    public Iterable<DocumentiValidati> getAllDocumentiValidati() {
        return documentiValidatiRepository.findAll();
    }

   

    public DocumentiValidati saveDocumentoValidato(DocumentiValidati documentiValidati) {
        return documentiValidatiRepository.save(documentiValidati);
    }

    // Si possono aggiungere altri metodi specifici per la logica di business
}
