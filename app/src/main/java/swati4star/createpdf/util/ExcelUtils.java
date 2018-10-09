package swati4star.createpdf.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.SheetUtil;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;

public class ExcelUtils {

    public void createPDF(Uri mFileUri, Context context, String mFinalPath) {

        InputStream inputStream;

        try {

            inputStream = context.getContentResolver().openInputStream(mFileUri);
            HSSFWorkbook mWorkBook = new HSSFWorkbook(inputStream);

            HSSFSheet mHSSFSheet = mWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mHSSFSheet.iterator();

            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(mFinalPath));
            document.open();
            // TODO : Get the last column of longest row.
            int cols = mHSSFSheet.getRow(0).getLastCellNum();
            PdfPTable mTable = new PdfPTable(cols);

            /*
                TODO : Get the column width in the excel and create the same for pdf
             */
            for (int i = 0; i < cols; i++) {
                Log.e("ExcelUtils col" + i, String.valueOf(mHSSFSheet.getColumnWidth(i)));
            }

            //PdfPCell pdfPCell;

            // Parses each row and adds entry to the table.
            /*
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    cell.getCellStyle();

                    switch (cell.getCellType()) {

                        case BLANK:
                        case _NONE:
                            pdfPCell = new PdfPCell(new Phrase(""));
                            mTable.addCell(pdfPCell);
                            break;
                        case STRING:
                            pdfPCell = new PdfPCell(new Phrase(cell.getStringCellValue()));
                            mTable.addCell(pdfPCell);
                            break;
                        case NUMERIC:
                            pdfPCell = new PdfPCell(new Phrase(String.valueOf(cell.getNumericCellValue())));
                            mTable.addCell(pdfPCell);
                            break;
                        case BOOLEAN:
                            pdfPCell = new PdfPCell(new Phrase(String.valueOf(cell.getBooleanCellValue())));
                            mTable.addCell(pdfPCell);
                            break;
                    }
                }
            }

            document.add(mTable);
            document.close();
            */

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
