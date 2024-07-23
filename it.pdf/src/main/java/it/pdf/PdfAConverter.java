package it.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class PdfAConverter {

    public static void convertToPdfA() throws IOException {
    	
    	String inputPath="/home/romolofiorenza/stage/it.pdf/src/main/java/it/pdf/sample.pdf";
    	String outputPath="/home/romolofiorenza/stage/it.pdf/src/main/java/it/pdf/samplePdfa.pdf";
    	
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(inputPath));
            
            // Crea i metadati XMP
            XMPMetadata xmp = XMPMetadata.createXMPMetadata();
            
            // Aggiungi lo schema PDF/A
            PDFAIdentificationSchema pdfaid = xmp.createAndAddPFAIdentificationSchema();
            pdfaid.setPart(1);
            pdfaid.setConformance("A");
            
            // Aggiungi metadati Dublin Core
            DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
            dc.setTitle(inputPath);
            dc.addCreator("PDFBox");
            dc.addDate(Calendar.getInstance());
            
            // Crea i metadati del documento
            PDDocumentCatalog catalog = document.getDocumentCatalog();
            PDMetadata metadata = new PDMetadata(document);
            catalog.setMetadata(metadata);
            
            // Serializza i metadati XMP
            XmpSerializer serializer = new XmpSerializer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializer.serialize(xmp, baos, true);
            metadata.importXMPMetadata(baos.toByteArray());
            
            // Aggiungi il profilo colore
            InputStream colorProfile = PdfAConverter.class.getResourceAsStream("/sRGB Color Space Profile.icm");
            PDOutputIntent intent = new PDOutputIntent(document, colorProfile);
            intent.setInfo("sRGB IEC61966-2.1");
            intent.setOutputCondition("sRGB IEC61966-2.1");
            intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
            intent.setRegistryName("http://www.color.org");
            catalog.addOutputIntent(intent);
            
            // Salva il documento convertito
            document.save(outputPath);
            
        } catch (BadFieldValueException | TransformerException e) {
            throw new IOException("Errore durante la conversione in PDF/A", e);
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    public static void main(String[] args) {
    	   System.out.println("Converte PDF in PDF/A-1b...");
    	   
    	       try {
            convertToPdfA();
            System.out.println("Conversione completata con successo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}




/**
Questo metodo fa quanto segue:

Carica il PDF di input.
Crea i metadati XMP necessari per PDF/A.
Aggiunge uno schema PDF/A-1b ai metadati.
Aggiunge metadati Dublin Core di base.
Serializza e aggiunge i metadati XMP al documento.
Aggiunge un profilo colore sRGB (necessario per PDF/A).
Salva il documento convertito.

Punti importanti da notare:

Questo metodo converte in PDF/A-1b. Per altri livelli di PDF/A, potrebbero essere necessarie modifiche aggiuntive.
La conversione potrebbe non essere perfetta per tutti i PDF, specialmente quelli con contenuti complessi o non conformi.
È necessario il file del profilo colore sRGB. Assicurati di avere il file "sRGB Color Space Profile.icm" nel tuo classpath.
Potresti dover gestire font incorporati, trasparenze e altri elementi avanzati per una conversione completa.
Dopo la conversione, è consigliabile validare il PDF risultante con il metodo di validazione che abbiamo discusso in precedenza.
    
    */