import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;

import java.io.IOException;

/**
 * Created by Kevin on 4/26/2016.
 */
public class MyCellField implements PdfPCellEvent {
    protected PdfFormField radiogroup;
    protected String value;
    public MyCellField(PdfFormField radiogroup, String value) {
        this.radiogroup = radiogroup;
        this.value = value;
    }
    public void cellLayout(PdfPCell cell, Rectangle rectangle, PdfContentByte[] canvases) {
        final PdfWriter writer = canvases[0].getPdfWriter();
        RadioCheckField radio = new RadioCheckField(writer, rectangle, null, value);
        radio.setCheckType(RadioCheckField.TYPE_CIRCLE);
        radio.setBorderColor(GrayColor.GRAYBLACK);
        radio.setBackgroundColor(GrayColor.GRAYWHITE);
        try {
            radiogroup.addKid(radio.getRadioField());
        } catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        } catch (final DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
}