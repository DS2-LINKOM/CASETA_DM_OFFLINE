package mx.linkom.caseta_dm_offline.Utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

public class PDFUtils {
    public static void addNewItem(Document document, String text, int align, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text,font);
        Paragraph paragraph= new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);

    }

    public static void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }
    public static void addNewItemWhitLeftAndRight(Document document, String textLeft, String textRight, Font textLeftFont, Font textRightFont) throws DocumentException {
        Chunk chuckTextLeft = new Chunk(textLeft,textLeftFont);
        Chunk chuckTextRight = new Chunk(textRight,textRightFont);
        Paragraph p= new Paragraph(chuckTextLeft);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(chuckTextRight);
        document.add(p);

    }

    public static void addLineSeparator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0,0,0,68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSeparator(document);
    }



}
