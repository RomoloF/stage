import com.spire.pdf.*;
import com.spire.pdf.graphics.*;

import java.awt.geom.*;

public class createTwoColumnPDF {
    public static void main(String[] args) {
        // Creates a pdf document
        PdfDocument doc = new PdfDocument();

        // Creates a new page
        PdfPageBase page = doc.getPages().add();

        String s1 = "Spire.PDF for Java is a PDF API that enables Java applications to read, write and "
                + "save PDF documents without using Adobe Acrobat. Using this Java PDF component, developers and "
                + "programmers can implement rich capabilities to create PDF files from scratch or process existing"
                + "PDF documents entirely on Java applications (J2SE and J2EE).";
        String s2 = "Many rich features can be supported by Spire.PDF for Java, such as security settings,"
                + "extract text/image from the PDF, merge/split PDF, draw text/image/shape/barcode to the PDF, create"
                + "and fill in form fields, add and delete PDF layers, overlay PDF, insert text/image watermark to the "
                + "PDF, add/update/delete PDF bookmarks, add tables to the PDF, compress PDF document etc. Besides, "
                + "Spire.PDF for Java can be applied easily to convert PDF to XPS, XPS to PDF, PDF to SVG, PDF to Word,"
                + "PDF to HTML and PDF to PDF/A in high quality.";

        // Get width and height of page
        double pageWidth = page.getClientSize().getWidth();
        double pageHeight = page.getClientSize().getHeight();

        PdfBrush brush = PdfBrushes.getBlack();
        PdfFont font = new PdfFont(PdfFontFamily.Times_Roman, 12f);
        PdfStringFormat format = new PdfStringFormat(PdfTextAlignment.Justify);

        // Draw text
        page.getCanvas().drawString(s1, font, brush, new Rectangle2D.Double(0, 20, pageWidth / 2 - 8f, pageHeight), format);
        page.getCanvas().drawString(s2, font, brush, new Rectangle2D.Double(pageWidth / 2 + 8f, 20, pageWidth / 2 - 8f, pageHeight), format);

        //Save the document
        String output = "output/createTwoColumnPDF.pdf";
        doc.saveToFile(output, FileFormat.PDF);
    }
}
