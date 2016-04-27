import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RadioCheckField;

public class RadioButtons {

    /** The resulting PDF. */
    public static final String RESULT = "results/part2/chapter08/radiobuttons.pdf";
    /** Possible values of a Choice field. */
    public static final String[] LANGUAGES = { "English", "German", "French", "Spanish", "Dutch" };

    /**
     * Creates a PDF document.
     * @param filename the path to the new PDF document
     * @throws    DocumentException
     * @throws    IOException
     */
    public void createPdf(String filename) throws IOException, DocumentException {
        // step 1
        Document document = new Document();
        // step 2
        PdfWriter writer =
                PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
        document.open();
        // step 4
        PdfContentByte cb = writer.getDirectContent();
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);

        // create a radio field spanning different pages
        PdfFormField radiogroup = PdfFormField.createRadioButton(writer, true);
        radiogroup.setFieldName("language");
        float top = 806;
        Rectangle rect = new Rectangle(40, top, 60, 788);
        RadioCheckField radio;
        PdfFormField radiofield;
        for (int page = 0; page < LANGUAGES.length; ) {
            radio = new RadioCheckField(writer, rect, null, LANGUAGES[page]);
            radio.setBackgroundColor(new GrayColor(0.8f));
            radiofield = radio.getRadioField();
            radiofield.setPlaceInPage(++page);
            radiogroup.addKid(radiofield);
            top -= 40;
            rect = new Rectangle(40, top, 60, 788);
        }
        writer.addAnnotation(radiogroup);
        // add the content

        for (int i = 0; i < LANGUAGES.length; i++) {
            cb.beginText();
            cb.setFontAndSize(bf, 18);
            cb.showTextAligned(Element.ALIGN_LEFT, LANGUAGES[i], 70, 790, 0);
            cb.endText();
            document.newPage();
        }
        // step 5
        document.close();
    }

    /**
     * Main method
     * @param args no arguments needed
     * @throws IOException
     * @throws DocumentException
     */
    public static void main(String[] args) throws IOException, DocumentException {
        new RadioButtons().createPdf(RESULT);
    }
}