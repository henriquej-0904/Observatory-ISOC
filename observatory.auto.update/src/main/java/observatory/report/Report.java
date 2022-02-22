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


    private final RequestType type;

    private final List<ListTest> results;

    private List<String> listsFullReport;

    private Calendar reportDate;

    /**
     * Creates a new Report of the specified type and results folder.
     * @param type - The type of report.
     * @param resultsFolder - The location of the results.
     * @throws IOException
     */
    public Report(RequestType type, File resultsFolder) throws IOException
    {
        this.type = type;
        this.results = Util.readResultsFromFolder(Objects.requireNonNull(resultsFolder), type);
        this.listsFullReport = List.of();
        this.reportDate = Calendar.getInstance();
    }

    /**
     * Generates a report based on the results provided for the specified type and saves it.
     * 
     * @param report - The location of the report.
     * @throws IOException
     * @throws InvalidTemplateException
     */
    public void generateAndSaveReport(File report) throws IOException, InvalidTemplateException
    {
        Objects.requireNonNull(report);

        File template = type == RequestType.WEB ? WEB_TEMPLATE : MAIL_TEMPLATE;

        if (!template.isFile())
            throw new InvalidTemplateException("There is no report template in the config folder.");

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

            setReportDate(workbook, listResultsTemplate);

            // Generate List Reports.
            for (ListTest listResults : results)
            {
                ListReport listReport = new ListReport(listResultsTemplate, listResults);
                listReport.setFullReport(listsFullReport.contains(listResults.getName()));
                listReport.generateReport();
            }

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

    private void setReportDate(Workbook workbook, Sheet listResultsTemplate)
    {
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

                    String date = String.format("%d/%d/%d",
                        this.reportDate.get(Calendar.DAY_OF_MONTH),
                        this.reportDate.get(Calendar.MONTH) + 1,
                        this.reportDate.get(Calendar.YEAR));
                        
                    cell.setCellValue(valueFormat.replace(DATE_FORMAT, date));
                }
            }
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

    /**
     * @return The type of report.
     */
    public RequestType getType() {
        return type;
    }

    /**
     * @return The lists that will have a full report of the results.
     */
    public List<String> getListsFullReport() {
        return listsFullReport;
    }

    /**
     * @param listsFullReport - The lists that will have a full report of the results.
     * 
     * @throws IllegalArgumentException if the results of the specified lists are not available.
     */
    public void setListsFullReport(List<String> listsFullReport) throws IllegalArgumentException
    {
        if (listsFullReport == null || listsFullReport.isEmpty())
        {
            this.listsFullReport = List.of();
            return;
        }

        checkIfListsExist(listsFullReport, this.results);
        this.listsFullReport = List.copyOf(listsFullReport);
    }

    /**
     * @return The date of the report.
     */
    public Calendar getReportDate() {
        return reportDate;
    }

    /**
     * @param reportDate the date of the report.
     */
    public void setReportDate(Calendar reportDate) {
        this.reportDate = reportDate;
    }    
}
