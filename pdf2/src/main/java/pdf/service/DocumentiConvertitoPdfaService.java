package pdf.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pdf.entity.DocumentiConvertitoPdfa;
import pdf.repository.DocumentiConvertitoPdfaRepository;




@Service
public class DocumentiConvertitoPdfaService {

	// Inietto il repository per accedere ai dati nel database
	@Autowired
    private final DocumentiConvertitoPdfaRepository documentiConvertitoPdfaRepository;


    public DocumentiConvertitoPdfaService(DocumentiConvertitoPdfaRepository documentiConvertitoPdfaRepository) {
        this.documentiConvertitoPdfaRepository = documentiConvertitoPdfaRepository;
    }



	public Iterable<DocumentiConvertitoPdfa> getAllDocumentiConvertitiPdfa() {
        return documentiConvertitoPdfaRepository.findAll();
    }

    public DocumentiConvertitoPdfa saveDocumentoConvertitoPdfa(DocumentiConvertitoPdfa documentiConvertitoPdfa) {
        return documentiConvertitoPdfaRepository.save(documentiConvertitoPdfa);
    }

    // Si possono aggiungere altri metodi specifici per la logica di business
}
