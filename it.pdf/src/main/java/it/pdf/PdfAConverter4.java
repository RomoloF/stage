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

/**
 * Classe per la conversione di PDF in PDF/A-1b utilizzando Apache PDFBox.
 */
public class PdfAConverter4 {

    /**
     * Metodo per convertire un PDF in PDF/A-1b.
     *
     * @throws IOException Se si verifica un errore durante la lettura o la scrittura del file.
     */
    public static void convertToPdfA() throws IOException {
        String inputPath = "/home/romolofiorenza/stage/it.pdf/src/main/java/it/pdf/pdf/modello di istanza.pdf";
        String outputPath = "/home/romolofiorenza/stage/it.pdf/src/main/java/it/pdf/pdf/modello di istanzaPDFA.pdf";
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(inputPath));

            // Crea i metadati XMP
            XMPMetadata xmp = XMPMetadata.createXMPMetadata();

            // Aggiungi lo schema PDF/A
            PDFAIdentificationSchema pdfaid = xmp.createAndAddPFAIdentificationSchema();
            pdfaid.setPart(3);
            pdfaid.setConformance("A");

            // Aggiungi metadati Dublin Core
            DublinCoreSchema dc = xmp.createAndAddDublinCoreSchema();
            dc.setTitle("Garage.pdf");
            dc.addCreator("PDFBox");
            dc.addDate(Calendar.getInstance());

            // Crea i metadati del documento
            PDDocumentCatalog catalog = document.getDocumentCatalog();
            PDMetadata metadata = new PDMetadata(document);
            
            //metadata.importXMPMetadata(baos.toByteArray());
            
            catalog.setMetadata(metadata);

            // Serializza i metadati XMP
            XmpSerializer serializer = new XmpSerializer();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializer.serialize(xmp, baos, true);
            metadata.importXMPMetadata(baos.toByteArray());

            // Aggiungi il profilo colore
            try (InputStream colorProfile = PdfAConverter4.class.getResourceAsStream("/sRGB2014.icc")) {
                if (colorProfile != null) {
                    PDOutputIntent intent = new PDOutputIntent(document, colorProfile);
                    intent.setInfo("sRGB IEC61966-2.1");
                    intent.setOutputCondition("sRGB IEC61966-2.1");
                    intent.setOutputConditionIdentifier("sRGB IEC61966-2.1");
                    intent.setRegistryName("http://www.color.org");
                    catalog.addOutputIntent(intent);
                } else {
                    throw new IOException("Profilo colore non trovato.");
                }
            }

          // Salva il documento convertito
            
         // Rimuovi azioni vietate e annotazioni non conformi for (PDPage page : document.getPages())
            
         //   { List<PDAnnotation> annotations = page.getAnnotations();
         //  for (PDAnnotation annotation : annotations) { annotation.setActions(null); 
         //   Rimuovi azioni PDAppearanceDictionary appearance = annotation.getAppearance(); 
          //  if (appearance != null) { appearance.setNormalAppearance(appearance.getNormalAppearance()); } } }
          //  }
          //  }

            document.save(outputPath);

        } catch (BadFieldValueException | TransformerException e) {
            throw new IOException("Errore durante la conversione in PDF/A", e);
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    /**
     * Metodo principale per eseguire la conversione di un file PDF in PDF/A.
     *
     * @param args i parametri della riga di comando.
     */
    public static void main(String[] args) {
        System.out.println("Conversione PDF in PDF/A ...");
        try {
            convertToPdfA();
            System.out.println("Conversione completata con successo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

