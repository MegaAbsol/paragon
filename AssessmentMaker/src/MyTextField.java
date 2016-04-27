import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.TextField;

import java.io.IOException;

/**
 * Created by Kevin on 4/26/2016.
 */
public class MyTextField implements PdfPCellEvent {
    protected String fieldname;
    public MyTextField(String fieldname) {
        this.fieldname = fieldname;
    }
    public void cellLayout(PdfPCell cell, Rectangle rectangle, PdfContentByte[] canvases) {
        final PdfWriter writer = canvases[0].getPdfWriter();
        final TextField textField = new TextField(writer, rectangle, fieldname);
        try {
            final PdfFormField field = textField.getTextField();
            writer.addAnnotation(field);
        } catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        } catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
}
