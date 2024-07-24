package pdf.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pdf.entity.DocumentiOriginalePdf;


@Repository
public interface DocumentiOriginalePdfRepository extends CrudRepository<DocumentiOriginalePdf, Integer> {
    // Puoi aggiungere metodi di query personalizzati se necessario
}
