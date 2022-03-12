package observatory.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;

import observatory.internetnlAPI.config.RequestType;
import observatory.tests.ListTest;
import observatory.util.Util;

/**
 * Represents a Report based on the results provided.
 * 
 * @author Henrique Campos Ferreira
 */
public class Report
{
    private static final String LIST_RESULTS_TEMPLATE_SHEET_NAME = "ListResults";

    private static final CellAddress ADDRESS_DATE_CELL = new CellAddress("A1");
    private static final String DATE_FORMAT = "%DATE%";


    private final RequestType type;

    private final File templateWorkbookFile;

    private final Set<ListTest> results;

    private Set<String> listsFullReport;

    private Calendar reportDate;

    /**
     * 
     * @param type - The type of report.
     * @param templateWorkbookFile - The location of the template workbook file.
     * @param results - A set of list results.
     * @throws IOException
     */
    public Report(RequestType type, File templateWorkbookFile, Set<ListTest> results) throws IOException
    {
        this.type = type;
        this.templateWorkbookFile = Objects.requireNonNull(templateWorkbookFile);
        if (!this.templateWorkbookFile.isFile())
            throw new FileNotFoundException("There is no report template file.");
        
        this.results = results;
        
        this.reportDate = Calendar.getInstance();
        this.listsFullReport = Set.of();
    }

    /**
     * Generate a report based on the results provided for the specified type and saves it.
     * 
     * @param report - The location to save the report.
     * @throws IOException
     * @throws InvalidTemplateException
     */
    public void generateAndSaveReport(File report) throws IOException, InvalidTemplateException
    {
        Objects.requireNonNull(report);

        try
        (
            InputStream input = new FileInputStream(this.templateWorkbookFile);
            Workbook workbook = Util.openWorkbook(input);
        )
        {
            Sheet listResultsTemplate = workbook.getSheet(LIST_RESULTS_TEMPLATE_SHEET_NAME);
            if (listResultsTemplate == null)
                throw new InvalidTemplateException("There is no " + LIST_RESULTS_TEMPLATE_SHEET_NAME
                    + " sheet in the report template.");

            setReportDate(workbook, listResultsTemplate);

            // Generate List Reports.
            for (ListTest listResults : this.results)
            {
                ListReport listReport = new ListReport(listResultsTemplate, listResults);
                listReport.setFullReport(listsFullReport.contains(listResults.getName()));
                listReport.generateReport();
            }

            // Remove list template from the final report.
            workbook.removeSheetAt(workbook.getSheetIndex(listResultsTemplate));

            workbook.setForceFormulaRecalculation(true);

            try (OutputStream output = new FileOutputStream(report))
                {workbook.write(output);}
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
        listsToCheck.removeAll(this.results.stream().map(ListTest::getName).collect(Collectors.toList()));

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
