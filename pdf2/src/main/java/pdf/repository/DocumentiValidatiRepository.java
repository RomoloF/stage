package pdf.repository;




import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pdf.entity.DocumentiValidati;



@Repository
public interface DocumentiValidatiRepository extends CrudRepository<DocumentiValidati, Integer> {




}
