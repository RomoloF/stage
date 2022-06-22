import com.spire.pdf.*;
import com.spire.pdf.graphics.*;
import com.spire.pdf.tables.*;

import java.awt.*;
import java.awt.geom.Point2D;

public class addRepeatingHeader {
    public static void main(String[] args) {
        //Create a pdf document
        PdfDocument doc = new PdfDocument();
        //Set the margin
        PdfUnitConvertor unitCvtr = new PdfUnitConvertor();
        PdfMargins margin = new PdfMargins();
        margin.setTop(unitCvtr.convertUnits(2.54f, PdfGraphicsUnit.Centimeter, PdfGraphicsUnit.Point));
        margin.setBottom(margin.getTop());
        margin.setLeft(unitCvtr.convertUnits(3.17f, PdfGraphicsUnit.Centimeter, PdfGraphicsUnit.Point));
        margin.setRight(margin.getLeft());

        //Add a page
        PdfPageBase page = doc.getPages().add(PdfPageSize.A4, margin);

        float y = 10;

        //Title
        PdfBrush brush = PdfBrushes.getBlack();
        PdfTrueTypeFont font = new PdfTrueTypeFont(new Font("Arial",  Font.BOLD,16));
        PdfStringFormat format = new PdfStringFormat(PdfTextAlignment.Center);
        page.getCanvas().drawString("Country List", font, brush, page.getCanvas().getClientSize().getWidth() / 2, y, format);
        y = y + (float) font.measureString("Country List", format).getHeight();
        y = y + 5;

        //Create data table
        PdfTable table = new PdfTable();
        table.getStyle().setBorderPen(new PdfPen(brush, 0.5f));

        //Header style
        table.getStyle().setHeaderSource(PdfHeaderSource.Rows);
        table.getStyle().setHeaderRowCount(1);
        table.getStyle().setShowHeader(true);
        table.getStyle().getHeaderStyle().setBackgroundBrush(PdfBrushes.getCadetBlue());
        table.getStyle().getHeaderStyle().setFont(new PdfTrueTypeFont(new Font("Arial", Font.BOLD,14)));
        table.getStyle().getHeaderStyle().setStringFormat(new PdfStringFormat(PdfTextAlignment.Center, PdfVerticalAlignment.Middle));
        //Repeat header
        table.getStyle().setRepeatHeader(true);

        //Body style
        table.getStyle().getDefaultStyle().setBackgroundBrush(PdfBrushes.getSkyBlue());
        table.getStyle().getDefaultStyle().setFont(new PdfTrueTypeFont(new Font("Arial", Font.PLAIN, 10)));
        table.getStyle().setAlternateStyle(new PdfCellStyle());
        table.getStyle().getAlternateStyle().setBackgroundBrush(PdfBrushes.getLightYellow());
        table.getStyle().getAlternateStyle().setFont(new PdfTrueTypeFont(new Font("Arial",Font.PLAIN, 10)));

        table.setDataSource(GetData());

        for(int i=0; i<table.getColumns().getCount();i++)
        {
            PdfColumn column= table.getColumns().get(i);
            column.setStringFormat(new PdfStringFormat(PdfTextAlignment.Center, PdfVerticalAlignment.Middle));
        }

        //Set the row height
        table.beginRowLayout.add(new BeginRowLayoutEventHandler() {
            @Override
            public void invoke(Object sender, BeginRowLayoutEventArgs args) { table_BeginRowLayout(sender,args);
            }});


        //Draw text below the table
        PdfLayoutResult result = table.draw(page, new Point2D.Float(0, y));
        y = y + (float) result.getBounds().getHeight() + 5;
        PdfBrush brush2 = PdfBrushes.getGray();
        PdfTrueTypeFont font2 = new PdfTrueTypeFont(new Font("Arial",Font.PLAIN, 9));
        page.getCanvas().drawString("* "+ table.getRows().getCount()+" countries in the list.", font2, brush2, 5, y);

        //Save the document
        doc.saveToFile("output/addRepeatingColumn_out.pdf");
    }
   static void table_BeginRowLayout(Object sender, BeginRowLayoutEventArgs args)
    {
        args.setMinimalHeight(50f);
    }
    private static String[][] GetData()
    {
        String[] data= {
                "Name;Capital;Continent;Area;Population",
                "Argentina;Buenos Aires;South America;2777815;32300003",
                "Bolivia;La Paz;South America;1098575;7300000",
                "Brazil;Brasilia;South America;8511196;150400000",
                "Canada;Ottawa;North America;9976147;26500000",
                "Chile;Santiago;South America;756943;13200000",
                "Colombia;Bagota;South America;1138907;33000000",
                "Cuba;Havana;North America;114524;10600000",
                "Ecuador;Quito;South America;455502;10600000",
                "El Salvador;San Salvador;North America;20865;5300000",
                "Guyana;Georgetown;South America;214969;800000",
                "Jamaica;Kingston;North America;11424;2500000",
                "Mexico;Mexico City;North America;1967180;88600000",
                "Nicaragua;Managua;North America;139000;3900000",
                "Paraguay;Asuncion;South America;406576;4660000",
                "Peru;Lima;South America;1285215;21600000",
                "United States of America;Washington;North America;9363130;249200000",
                "Uruguay;Montevideo;South America;176140;3002000",
                "Venezuela;Caracas;South America;912047;19700000"
        };

        String[][] dataSource
                = new String[data.length][];
        for (int i = 0; i < data.length; i++)
        {
            dataSource[i] = data[i].split(";");
        }
        return dataSource;
    }
}
