package swati4star.createpdf.util;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ss.util.SheetUtil;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import swati4star.createpdf.model.ExcelToPDFOptions;

public class ExcelUtils {

    public void createPDF(ExcelToPDFOptions excelToPDFOptions) {



        /*
            TODO : Get the column width in the excel and create the same for pdf

            Get the column length in pixel, convert to inch by dividing with 81 (1 inch = 81px).
            In iText, 1 inch = 72 user units, multiply result with 72 to get the column length
         */
        new ExcelUtilAsyncTask().execute(excelToPDFOptions);

    }
}

class ExcelUtilAsyncTask extends AsyncTask<ExcelToPDFOptions, Integer, String> {

    @Override
    protected String doInBackground(ExcelToPDFOptions... options) {
        InputStream inputStream;

        try {

            inputStream = options[0].getmContext().getContentResolver().openInputStream(options[0].getmInFileUri());
            HSSFWorkbook mWorkBook = new HSSFWorkbook(inputStream);

            HSSFSheet mHSSFSheet = mWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mHSSFSheet.iterator();

            Document document = new Document();
            //document.setPageSize(PageSize.A5);

            PdfWriter.getInstance(document, new FileOutputStream(options[0].getmFinalPath()));
            document.open();
            document.setPageSize(PageSize.A4);

            // Gets the last column of longest row.
            int rows = mHSSFSheet.getLastRowNum();
            Log.d("ExcelUtil: ", "Number of rows " + rows);

            int sheetLastCellNum = -1;

            for (int i = 0; i < rows; i++) {
                int lastCellNum = mHSSFSheet.getRow(i).getLastCellNum();
                Log.e("ExcelUtils", "LastCol = " + lastCellNum);
                if (sheetLastCellNum < lastCellNum) {
                    sheetLastCellNum = lastCellNum;
                }
            }

            Log.e("ExcelUtil:", "Number of cols = " + sheetLastCellNum);

            // Gets the column sizes
            float []columnSizes = new float[sheetLastCellNum];

            for (int i = 0; i < sheetLastCellNum; i++) {
                columnSizes[i] = mHSSFSheet.getColumnWidthInPixels(i);
            }

            // create the table with relative column width
            PdfPTable mTable = new PdfPTable(columnSizes);
            mTable.setWidthPercentage(100);

            PdfPCell pdfPCell;

            //Parses each row and adds entry to the table.
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
                        case FORMULA:
                            pdfPCell = new PdfPCell(new Phrase(String.valueOf(cell.getCellFormula())));
                            mTable.addCell(pdfPCell);
                            break;
                    }
                }

                mTable.completeRow();
            }

            document.add(mTable);
            document.close();

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPostExecute(String res) {

    }

}
