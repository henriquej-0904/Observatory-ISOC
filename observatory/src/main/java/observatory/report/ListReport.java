package observatory.report;

import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;

import observatory.internetnlAPI.config.InternetnlRequest;
import observatory.internetnlAPI.config.RequestType;
import observatory.internetnlAPI.config.testResult.domain.Category;
import observatory.internetnlAPI.config.testResult.domain.CustomTest;
import observatory.internetnlAPI.config.testResult.domain.DomainResults;
import observatory.internetnlAPI.config.testResult.domain.ResultStatus;
import observatory.internetnlAPI.config.testResult.domain.Results;
import observatory.internetnlAPI.config.testResult.domain.Test;
import observatory.tests.ListTest;

/**
 * A class to create a report from the results of a List.
 * 
 * @author Henrique Campos Ferreira
 */
public class ListReport
{
    private static final CellAddress ADDRESS_FIRST_DOMAIN = new CellAddress("A15");

    private static final CellAddress ADDRESS_NUMBER_TESTED_DOMAINS = new CellAddress("B5");

    private static final CellAddress ADDRESS_AVERAGE_SCORE = new CellAddress("C13");

    private static final Map<ResultStatus, CellAddress> ADDRESS_RESULT =
        Map.of
        (
            ResultStatus.STATUS_SUCCESS, new CellAddress("B6"),
            ResultStatus.STATUS_INFO, new CellAddress("B7"),
            ResultStatus.STATUS_NOTICE, new CellAddress("B8"),
            ResultStatus.STATUS_FAIL, new CellAddress("B9"),
            ResultStatus.STATUS_NOT_TESTED, new CellAddress("B10"),
            ResultStatus.STATUS_ERROR, new CellAddress("B11")
        );

    private static final Map<Category, CellAddress> ADDRESS_CATEGORY =
        Map.of
        (
            Category.WEB_IPV6, new CellAddress("F2"),
            Category.WEB_DNSSEC, new CellAddress("M2"),
            Category.WEB_HTTPS, new CellAddress("Q2"),
            Category.WEB_APPSECPRIV, new CellAddress("AM2"),

            Category.MAIL_IPV6, new CellAddress("F2"),
            Category.MAIL_DNSSEC, new CellAddress("L2"),
            Category.MAIL_AUTH, new CellAddress("R2"),
            Category.MAIL_STARTTLS, new CellAddress("Y2")
        );

    private static final Map<RequestType, Map<CustomTest, CellAddress>> ADDRESS_CUSTOM_FIELD =
        Map.of
        (
            RequestType.WEB,
            Map.of(CustomTest.TLS_1_3_SUPPORT, new CellAddress("AS3")),

            RequestType.MAIL,
            Map.of
            (
                CustomTest.TLS_1_3_SUPPORT, new CellAddress("AR3"),
                CustomTest.MAIL_SENDING_DOMAIN, new CellAddress("AS3"),
                CustomTest.MAIL_SERVER_TESTABLE, new CellAddress("AT3")
            )
        );

    private static final Map<ResultStatus, IndexedColors> COLOR_BY_RESULT =
        Map.of
        (
            ResultStatus.STATUS_ERROR, IndexedColors.LIGHT_ORANGE,
            ResultStatus.STATUS_FAIL, IndexedColors.RED1,
            ResultStatus.STATUS_NOTICE, IndexedColors.LIGHT_YELLOW,
            ResultStatus.STATUS_INFO, IndexedColors.LIGHT_BLUE,
            ResultStatus.STATUS_NOT_TESTED, IndexedColors.GREY_50_PERCENT,
            ResultStatus.STATUS_SUCCESS, IndexedColors.SEA_GREEN
        );


    private static final int FIRST_RESULT_COLUMN = new CellAddress("F1").getColumn();

    private static final Map<RequestType, Integer> LAST_RESULT_COLUMN =
        Map.of(
            RequestType.WEB, new CellAddress("AS1").getColumn(),
            RequestType.MAIL, new CellAddress("AT1").getColumn()
        );

    
    private final Sheet report;

    private final ListTest listResults;

    private boolean fullReport, orderByIntnl;

    /**
     * Creates a new instance based on a report template and the results of a list.
     * 
     * @param reportTemplate - The template of the List Report.
     * @param listResults - The results of the List.
     */
    public ListReport(Sheet reportTemplate, ListTest listResults)
    {
        Objects.requireNonNull(reportTemplate);
        this.listResults = Objects.requireNonNull(listResults);
        this.fullReport = false;
        this.orderByIntnl = true;

        Workbook workbook = reportTemplate.getWorkbook();
        this.report = workbook.cloneSheet(workbook.getSheetIndex(reportTemplate));
        workbook.setSheetName(workbook.getSheetIndex(report), listResults.getName());
    }

    /**
     * @return the fullReport
     */
    public boolean isFullReport() {
        return fullReport;
    }

    /**
     * @param fullReport the fullReport to set
     */
    public void setFullReport(boolean fullReport) {
        this.fullReport = fullReport;
    }

    /**
     * Check if the list must be ordered by the Internet.nl classification (true by default).
     * 
     * @return true if the list must be ordered, false otherwise.
     */
    public boolean orderByIntnl() {
        return orderByIntnl;
    }

    /**
     * Set if the list must be ordered by the Internet.nl classification.
     * @param orderByIntnl true if the list must be ordered, false otherwise.
     */
    public void setOrderByIntnl(boolean orderByIntnl) {
        this.orderByIntnl = orderByIntnl;
    }

    /**
     * Get the total domains.
     * @return The total domains.
     */
    public int getTotalDomains()
    {
        return this.listResults.getResults().getDomains().size();
    }

    /**
     * Get the type of report.
     * @return The type of report.
     */
    public RequestType getType()
    {
        return getInternetnlRequest().getRequest_type();
    }

    /**
     * Get the internet nl request.
     * @return The internet nl request.
     */
    private InternetnlRequest getInternetnlRequest()
    {
        return this.listResults.getResults().getRequest();
    }

    /**
     * Create a report for the specified List.
     * 
     * @return The generated List report as a Sheet.
     */
    public Sheet generateReport()
    {
        Map<String, DomainResults> resultsByDomain = listResults.getResults().getDomains();
        int testedDomains = 0;

        int currentDomainRow = ADDRESS_FIRST_DOMAIN.getRow();
        for (Entry<String, DomainResults> domainResultEntry : resultsByDomain.entrySet())
        {
            String domain = domainResultEntry.getKey();
            DomainResults results = domainResultEntry.getValue();

            Row row = report.createRow(currentDomainRow++);
            int currentColumn = ADDRESS_FIRST_DOMAIN.getColumn();

            // set list name
            row.createCell(currentColumn++, CellType.STRING).setCellValue(listResults.getName());

            // set domain url
            row.createCell(currentColumn++, CellType.STRING).setCellValue(domain);

            // check if domain was not tested
            if (!results.getStatus().equals("ok"))
                continue;

            testedDomains++;

            // set score
            row.createCell(currentColumn++, CellType.NUMERIC).setCellValue(results.getScoring().getPercentage());

            setDomainStatistics(results.getResults());

            if (this.fullReport)
            {
                // set report url
                row.createCell(currentColumn++, CellType.STRING).setCellValue(results.getReport().getUrl());
                setDomainResults(row, results.getResults());
            }
        }
    
        setTestedDomains(testedDomains);
        setAverageScore();

        if (this.fullReport)
            setConditionalFormatting();

        return report;
    }

    /**
     * Set the average score.
     */
    private void setAverageScore()
    {
        if (getTotalDomains() == 0)
            return;
        
        Cell cell = report.getRow(ADDRESS_AVERAGE_SCORE.getRow())
            .createCell(ADDRESS_AVERAGE_SCORE.getColumn(), CellType.FORMULA);
        
        CellAddress rangeLeft, rangeRight;
        rangeLeft = new CellAddress(ADDRESS_FIRST_DOMAIN.getRow(), ADDRESS_AVERAGE_SCORE.getColumn());
        rangeRight = new CellAddress(rangeLeft.getRow() + getTotalDomains() - 1, rangeLeft.getColumn());
        CellRangeAddress range = new CellRangeAddress(rangeLeft.getRow(), rangeRight.getRow(),
            rangeLeft.getColumn(), rangeRight.getColumn());

        cell.setCellFormula(String.format("AVERAGE(%s)", range.formatAsString()));
    }

    //#region Domain Statistics

    /**
     * Set the domain statistics given the results.
     * @param results
     */
    private void setDomainStatistics(Results results)
    {
        for (Category category : Category.values(getType()))
            setDomainStatistics(results, category);

        setCustomFieldStatistics(results);
    }

    /**
     * Set the domain statistics of the specified category given the results.
     * @param results
     * @param category
     */
    private void setDomainStatistics(Results results, Category category)
    {
        CellAddress categoryAddress = ADDRESS_CATEGORY.get(category);
        int currentColumn = categoryAddress.getColumn();

        ResultStatus result = results.getCategories().get(category).getStatus();
        incCell(result, currentColumn++);

        for (Test test : Test.values(category))
        {
            result = results.getTests().get(test).getStatus();
            incCell(result, currentColumn++);
        }
    }

    /**
     * Set the custom field statistics given the results.
     * @param results
     */
    private void setCustomFieldStatistics(Results results)
    {
        RequestType type = getType();

        for (Entry<CustomTest, Object> test : results.getCustom().entrySet())
        {
            CustomTest customTest = test.getKey();
            ResultStatus result = customTest.convertToResult(test.getValue());

            incCell(result, ADDRESS_CUSTOM_FIELD.get(type).get(customTest).getColumn());
        }
    }

    /**
     * Increment the value of a numeric cell.
     * @param result
     * @param column
     */
    private void incCell(ResultStatus result, int column)
    {
        Cell cell = this.report.getRow(ADDRESS_RESULT.get(result).getRow()).getCell(column);
        int value = (int)cell.getNumericCellValue();
        cell.setCellValue(value + 1);
    }

    //#endregion

    //#region Set number tested domains

    /**
     * Set the number of tested domains.
     * @param testedDomains - The number of tested domains to set.
     */
    private void setTestedDomains(int testedDomains)
    {
        RequestType type  = getType();
        Row row = this.report.getRow(ADDRESS_NUMBER_TESTED_DOMAINS.getRow());
        for (Category category : Category.values(type))
            setTestedDomains(row, category, testedDomains);

        // custom fields
        for (CellAddress customCellAddress : ADDRESS_CUSTOM_FIELD.get(type).values())
            setTestedDomains(row, customCellAddress.getColumn(), testedDomains);
    }

    /**
     * Set the number of tested domains.
     * @param row - The current row.
     * @param category - The category.
     * @param testedDomains - The number of tested domains to set.
     */
    private void setTestedDomains(Row row, Category category, int testedDomains)
    {
        CellAddress categoryAddress = ADDRESS_CATEGORY.get(category);
        int currentColumn = categoryAddress.getColumn();

        setTestedDomains(row, currentColumn++, testedDomains);

        for (int i = 0; i < Test.values(category).size(); i++)
            setTestedDomains(row, currentColumn++, testedDomains);
    }

    /**
     * Set the number of tested domains.
     * @param row - The current row.
     * @param column - The current column.
     * @param testedDomains - The number of tested domains to set.
     */
    private void setTestedDomains(Row row, int column, int testedDomains)
    {
        row.getCell(column).setCellValue(testedDomains);
    }

    //#endregion

    //#region Set Domain Results - Full report

    /**
     * Set the specific results of a domain.
     * 
     * @param domainRow
     * @param results
     */
    private void setDomainResults(Row domainRow, Results results)
    {
        RequestType type = getType();
        for (Category category : Category.values(type))
            setDomainResults(domainRow, results, category);

        // custom fields

        Map<CustomTest, CellAddress> addresses = ADDRESS_CUSTOM_FIELD.get(type);

        for (Entry<CustomTest, Object> test : results.getCustom().entrySet())
        {
            CustomTest customTest = test.getKey();
            ResultStatus result = customTest.convertToResult(test.getValue());

            setDomainResult(domainRow, addresses.get(customTest).getColumn(), result);
        }
    }

    /**
     * Set the specific results of a domain.
     * 
     * @param domainRow
     * @param results
     * @param category
     */
    private void setDomainResults(Row domainRow, Results results, Category category)
    {
        CellAddress categoryAddress = ADDRESS_CATEGORY.get(category);
        int currentColumn = categoryAddress.getColumn();

        ResultStatus result = results.getCategories().get(category).getStatus();
        setDomainResult(domainRow, currentColumn++, result);

        for (Test test : Test.values(category))
        {
            result = results.getTests().get(test).getStatus();
            setDomainResult(domainRow, currentColumn++, result);
        }
    }

    /**
     * Set the specific results of a domain.
     * 
     * @param domainRow
     * @param column
     * @param result
     */
    private void setDomainResult(Row domainRow, int column, ResultStatus result)
    {
        Cell cell = domainRow.createCell(column, CellType.STRING);
        cell.setCellValue(result.getStatus());
    }

    //#endregion

    /**
     * Set the conditional formatting (colors in results).
     */
    private void setConditionalFormatting()
    {
        SheetConditionalFormatting formatting = this.report.getSheetConditionalFormatting();

        // calculate results range
        CellAddress firstAddress = new CellAddress(ADDRESS_FIRST_DOMAIN.getRow(), FIRST_RESULT_COLUMN);
        CellAddress lastAddress = new CellAddress(firstAddress.getRow() + getTotalDomains() - 1,
            LAST_RESULT_COLUMN.get(getType()));
        CellRangeAddress range = new CellRangeAddress(
            firstAddress.getRow(), lastAddress.getRow(), firstAddress.getColumn(), lastAddress.getColumn());
        
        CellRangeAddress[] regions = new CellRangeAddress[] {range};

        // create conditions
        COLOR_BY_RESULT.entrySet().stream()
            .map(
                (entry) ->
                {
                    ConditionalFormattingRule rule =
                        formatting.createConditionalFormattingRule(ComparisonOperator.EQUAL,
                            "= \"" + entry.getKey().getStatus() + "\"");

                    PatternFormatting patternFormatting = rule.createPatternFormatting();
                    patternFormatting.setFillBackgroundColor(entry.getValue().index);
                    patternFormatting.setFillForegroundColor(IndexedColors.AUTOMATIC.index);
                    patternFormatting.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
                    
                    return rule;
                })
            .forEach((rule) -> formatting.addConditionalFormatting(regions, rule));
    }
}
