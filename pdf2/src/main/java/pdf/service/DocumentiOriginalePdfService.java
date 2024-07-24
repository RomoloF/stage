package pdf.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pdf.entity.DocumentiOriginalePdf;
import pdf.repository.DocumentiOriginalePdfRepository;


//@Service
public class DocumentiOriginalePdfService {

    private final DocumentiOriginalePdfRepository documentiOriginalePdfRepository;

    @Autowired
    public DocumentiOriginalePdfService(DocumentiOriginalePdfRepository documentiOriginalePdfRepository) {
        this.documentiOriginalePdfRepository = documentiOriginalePdfRepository;
    }

    public Optional<DocumentiOriginalePdf> findById(int id) {
        return documentiOriginalePdfRepository.findById(id);
    }

    public DocumentiOriginalePdf save(DocumentiOriginalePdf documentiOriginalePdf) {
        return documentiOriginalePdfRepository.save(documentiOriginalePdf);
    }

    public void deleteById(int id) {
        documentiOriginalePdfRepository.deleteById(id);
    }

    // Altri metodi di servizio se necessario
}
