package pdf.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pdf.entity.DocumentiOriginalePdf;
import pdf.repository.DocumentiOriginalePdfRepository;


@Service
public class DocumentiOriginalePdfService {

    private final DocumentiOriginalePdfRepository documentiOriginalePdfRepository;


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

    // Implementazione del metodo findAll
    public Iterable<DocumentiOriginalePdf> findAll() {
      return documentiOriginalePdfRepository.findAll();
    }

    /**
     * Salva un documento PDF nel database.
     *
     * @param nomeFile il nome del file
     * @param file il file PDF da salvare
     * @throws IOException se si verifica un errore durante la lettura del file
     */
    public void salvaDocumento(String nomeFile, MultipartFile file) throws IOException {
        DocumentiOriginalePdf documento = new DocumentiOriginalePdf();
        documento.setNomeFile(nomeFile);
        documento.setContenutoPdf(file.getBytes());
        documento.setDataCreazione(new Date());
        documento.setDimensioneFile((int) file.getSize());
        documentiOriginalePdfRepository.save(documento);
    }

	public void save(Object pdfA1B) {
		// TODO Auto-generated method stub
		
	}
}
