package pdf.service;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pdf.entity.DocumentiConvertitoPdfa;
import pdf.entity.DocumentiOriginalePdf;
import pdf.repository.DocumentiConvertitoPdfaRepository;

@Service
public class DocumentiConvertitoPdfaService {

	// Inietto il repository per accedere ai dati nel database
	
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
    
//    public DocumentiConvertitoPdfa salvaDocumentoConvertitoPdfa(DocumentiConvertitoPdfa documentoConvertitoPdfa) {
//        return documentoConvertitoPdfaRepository.save(documentoConvertitoPdfa);
//    }
       
    /**
     * Salva il documento PDF convertito nel database.
     *
     * @param nomeFile il nome del file
     * @param file il file PDF da salvare
     * @throws IOException se si verifica un errore durante la lettura del file
     */
    public void salvaDocumento(String nomeFile, MultipartFile file) throws IOException {
    	DocumentiConvertitoPdfa documento = new DocumentiConvertitoPdfa();
        documento.setNomeFile(nomeFile);
        documento.setContenutoPdf(file.getBytes());
        documento.setDataConvertito(new Date());
        documento.setDimensioneFile((int) file.getSize());
        documentiConvertitoPdfaRepository.save(documento);
    }
    
}
