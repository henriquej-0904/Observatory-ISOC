package observatory.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import observatory.internetnlAPI.config.RequestType;
import observatory.tests.ListTest;
import observatory.tests.collection.ListTestCollection;

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

    private final SortedMap<String, ListTest> results;

    private Set<String> listsFullReport;

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
        this.reportDate = Calendar.getInstance();
        this.listsFullReport = Set.of();
        
        ListTestCollection listTestCollection = new ListTestCollection(resultsFolder, type);
        this.results = listTestCollection.getSortedResults();
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
            for (ListTest listResults : this.results.values())
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

    /**
     * Set the date of the report.
     * @param workbook
     * @param listResultsTemplate
     */
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

    /**
     * Check if the results of the specified lists are available.
     * @param listsToConfirm
     */
    private Set<String> checkLists(Set<String> listsToCheck)
    {
        listsToCheck = listsToCheck.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toSet());
        
        Set<String> toReturn = Set.copyOf(listsToCheck);
        listsToCheck.removeAll(this.results.keySet());

        if (!listsToCheck.isEmpty())
            throw new IllegalArgumentException(
                "The results of the specified lists are not available: " + listsToCheck.toString());

        return toReturn;
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
    public Set<String> getListsFullReport() {
        return listsFullReport;
    }

    /**
     * @param listsFullReport - The lists that will have a full report of the results.
     * 
     * @throws IllegalArgumentException if the results of the specified lists are not available.
     */
    public void setListsFullReport(Set<String> listsFullReport) throws IllegalArgumentException
    {
        if (listsFullReport == null || listsFullReport.isEmpty())
        {
            this.listsFullReport = Set.of();
            return;
        }

        this.listsFullReport = checkLists(listsFullReport);
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
