package pdf.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pdf.entity.DocumentiConvertitoPdfa;



@Repository
public interface DocumentiConvertitoPdfaRepository extends CrudRepository<DocumentiConvertitoPdfa, Integer> {

    // Si possono aggiungere query personalizzate se necessario
	
	// DocumentiConvertitoPdfa saveDocumentoConvertitoPdfa(DocumentiConvertitoPdfa documentoConvertitoPdfa);
}
