package observatory.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import observatory.internetnlAPI.config.RequestType;
import observatory.util.ListTest;
import observatory.util.Util;

/**
 * Represents a Report based on the results provided.
 */
public class Report
{
    private static final File WEB_TEMPLATE = new File("config/template-report-web.xlsx");
    private static final File MAIL_TEMPLATE = new File("config/template-report-mail.xlsx");

    private static final String LIST_RESULTS_TEMPLATE_SHEET_NAME = "ListResults";

    private static final CellAddress ADDRESS_DATE_CELL = new CellAddress("A1");
    private static final String DATE_FORMAT = "%DATE%";


    /**
     * Generates a report based on the results provided for the specified type and saves it.
     * 
     * @param type - The type of report.
     * @param report - The location of the report.
     * @param resultsFolder - The location of the results.
     * 
     * @throws IOException
     * @throws InvalidTemplateException
     */
    public static void generateAndSaveReport(RequestType type, File report, File resultsFolder)
        throws IOException, InvalidTemplateException
    {
        generateAndSaveReport(type, report, resultsFolder, List.of());
    }

    /**
     * Generates a report based on the results provided for the specified type and saves it.
     * 
     * @param type - The type of report.
     * @param report - The location of the report.
     * @param resultsFolder - The location of the results.
     * @param listsFullReport - The lists that will have a full report of the results.
     * @throws IOException
     * @throws InvalidTemplateException
     */
    public static void generateAndSaveReport(RequestType type, File report, File resultsFolder,
        List<String> listsFullReport) throws IOException, InvalidTemplateException
    {
        Objects.requireNonNull(report);
        Objects.requireNonNull(resultsFolder);
        Objects.requireNonNull(listsFullReport);

        File template = type == RequestType.WEB ? WEB_TEMPLATE : MAIL_TEMPLATE;

        if (!template.isFile())
            throw new InvalidTemplateException("There is no report template in the config folder.");

        List<ListTest> results = Util.readResultsFromFolder(resultsFolder, type);

        checkIfListsExist(listsFullReport, results);

        try
        (
            InputStream input = new FileInputStream(template);
            Workbook workbook = new XSSFWorkbook(input);
        )
        {
            Sheet listResultsTemplate = workbook.getSheet(LIST_RESULTS_TEMPLATE_SHEET_NAME);
            if (listResultsTemplate == null)
                throw new InvalidTemplateException("There is no " + LIST_RESULTS_TEMPLATE_SHEET_NAME
                    + " sheet in the report template.");

            // set date on sheets
            for (int i = 0; i < workbook.getSheetIndex(listResultsTemplate); i++)
            {
                Sheet sheet = workbook.getSheetAt(i);
                Row row = sheet.getRow(ADDRESS_DATE_CELL.getRow());
                Cell cell;

                if (row != null && (cell = row.getCell(ADDRESS_DATE_CELL.getColumn())) != null)
                {
                    if (cell.getCellType() == CellType.STRING)
                    {
                        String valueFormat = cell.getStringCellValue();

                        Calendar calendar = Calendar.getInstance();
                        String date = String.format("%d/%d/%d",
                            calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.YEAR));
                        
                        cell.setCellValue(valueFormat.replace(DATE_FORMAT, date));
                    }
                }
            }

            for (ListTest listResults : results)
                ListReport.createListReport(listResultsTemplate, listResults,
                    listsFullReport.contains(listResults.getName()));

            // Remove list template from the final report.
            workbook.removeSheetAt(workbook.getSheetIndex(listResultsTemplate));

            workbook.setForceFormulaRecalculation(true);

            try
            (
                OutputStream output = new FileOutputStream(report);
            )
            {
                workbook.write(output);
            }
        }
        catch (IOException | InvalidTemplateException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new InvalidTemplateException(e);
        }
    }

    private static void checkIfListsExist(List<String> listsToConfirm, List<ListTest> lists)
    {
        if (listsToConfirm.isEmpty())
            return;

        listsToConfirm = new ArrayList<>(listsToConfirm);

        List<String> availableLists = lists.stream()
            .map((v) -> v.getName())
            .collect(Collectors.toList());

        listsToConfirm.removeAll(availableLists);

        if (!listsToConfirm.isEmpty())
            throw new IllegalArgumentException(
                "The results of the specified lists are not available: " + listsToConfirm.toString());
    }
}
