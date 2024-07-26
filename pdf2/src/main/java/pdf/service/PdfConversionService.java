package pdf.service;

import com.spire.pdf.PdfDocument;

import org.springframework.stereotype.Service;

import com.spire.pdf.conversion.PdfStandardsConverter;

@Service
public class PdfConversionService {
	

	// Esegui la conversione se il file di input è un file PDF
	public void convertToPdfA(String inputFile, String outputFile) {
		
		 // Verifica se il file di input è un file PDF
	    try {
	        PdfDocument pdfDocument = new PdfDocument(inputFile);//Se il file non è pdf da errore e va sul catch .
	        pdfDocument.close();// Se è okei chiude il documento e va oltre .
	    } catch (Exception e) {
	        System.out.println("Errore: Il file di input non è un file PDF. Ma comunque lo salvo .");
	        return;
	    }
		
	 // Esegui la conversione se il file di input è un file PDF
		PdfStandardsConverter pdfStandardsConverter = new PdfStandardsConverter(inputFile);
        //pdfStandardsConverter.load(inputFile);
        pdfStandardsConverter.toPdfA1B(outputFile); 
//      pdfStandardsConverter.toPdfA1B(documentiOriginalePdfService.salvaDocumento(outputFile, null)); 
//      documentiOriginalePdfService.save(pdfStandardsConverter.toPdfA1B(outputFile));
        
        System.out.println("Conversione completata!");
        System.out.println("Il file è >>>>  " + outputFile);
        
        
    }
}
