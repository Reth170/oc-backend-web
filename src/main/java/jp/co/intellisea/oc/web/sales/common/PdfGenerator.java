package jp.co.intellisea.oc.web.sales.common;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class PdfGenerator {

    private static final BaseColor HEADER_BG = new BaseColor(63, 81, 181);
    private static final BaseColor HEADER_FG = BaseColor.WHITE;
    private static final BaseColor ALT_ROW_BG = new BaseColor(245, 245, 245);

    /**
     * Generate a PDF table from a list of data objects.
     *
     * @param title       PDF document title
     * @param headers     column header names
     * @param getterNames getter method names corresponding to each column (e.g. "getName", "getMail")
     * @param dataList    list of data objects
     * @param <T>         data type
     * @return PDF content as byte array
     */
    public static <T> byte[] generateTablePdf(String title, String[] headers, String[] getterNames, List<T> dataList) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titleParagraph = new Paragraph(title, titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            titleParagraph.setSpacingAfter(20);
            document.add(titleParagraph);

            int columnCount = headers.length;
            PdfPTable table = new PdfPTable(columnCount);
            table.setWidthPercentage(100);

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, HEADER_FG);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(HEADER_BG);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
            for (int i = 0; i < dataList.size(); i++) {
                T item = dataList.get(i);
                BaseColor rowColor = (i % 2 == 1) ? ALT_ROW_BG : BaseColor.WHITE;
                for (String getterName : getterNames) {
                    String value = getPropertyValue(item, getterName);
                    PdfPCell cell = new PdfPCell(new Phrase(value, cellFont));
                    cell.setBackgroundColor(rowColor);
                    cell.setPadding(6);
                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private static <T> String getPropertyValue(T obj, String getterName) {
        try {
            Method method = obj.getClass().getMethod(getterName);
            Object value = method.invoke(obj);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
