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
import observatory.internetnlAPI.config.results.domain.Category;
import observatory.internetnlAPI.config.results.domain.CustomTest;
import observatory.internetnlAPI.config.results.domain.DomainResults;
import observatory.internetnlAPI.config.results.domain.ResultStatus;
import observatory.internetnlAPI.config.results.domain.Results;
import observatory.internetnlAPI.config.results.domain.Test;
import observatory.util.ListTest;

/**
 * A class to create a report from the results of a List.
 * 
 * TODO: extra fields...
 */
public class ListReport
{
    private static final CellAddress FIRST_DOMAIN_ROW = new CellAddress("A13");

    private static final CellAddress ADDRESS_TESTED_DOMAINS = new CellAddress("B1");

    private static final CellAddress ADDRESS_AVERAGE_SCORE = new CellAddress("C1");

    private static final Map<ResultStatus, CellAddress> ADDRESS_RESULT =
        Map.of
        (
            ResultStatus.STATUS_SUCCESS, new CellAddress("B2"),
            ResultStatus.STATUS_INFO, new CellAddress("B3"),
            ResultStatus.STATUS_NOTICE, new CellAddress("B4"),
            ResultStatus.STATUS_FAIL, new CellAddress("B5"),
            ResultStatus.STATUS_NOT_TESTED, new CellAddress("B6"),
            ResultStatus.STATUS_ERROR, new CellAddress("B7")
        );

    private static final Map<Category, CellAddress> ADDRESS_CATEGORY =
        Map.of
        (
            Category.WEB_IPV6, new CellAddress("F11"),
            Category.WEB_DNSSEC, new CellAddress("M11"),
            Category.WEB_HTTPS, new CellAddress("Q11"),
            Category.WEB_APPSECPRIV, new CellAddress("AM11"),

            Category.MAIL_IPV6, new CellAddress("F11"),
            Category.MAIL_DNSSEC, new CellAddress("L11"),
            Category.MAIL_AUTH, new CellAddress("R11"),
            Category.MAIL_STARTTLS, new CellAddress("Y11")
        );

    private static final Map<RequestType, Map<CustomTest, CellAddress>> ADDRESS_CUSTOM_FIELD =
        Map.of
        (
            RequestType.WEB,
            Map.of(CustomTest.TLS_1_3_SUPPORT, new CellAddress("BB12")),

            RequestType.MAIL,
            Map.of
            (
                CustomTest.TLS_1_3_SUPPORT, new CellAddress("BJ12"),
                CustomTest.MAIL_SENDING_DOMAIN, new CellAddress("BF12"),
                CustomTest.MAIL_SERVER_TESTABLE, new CellAddress("BG12")
            )
        );

    // Extra fields...
    /* private static final Map<ExtraField, CellAddress> ADDRESS_EXTRA_FIELD =
        Map.ofEntries
        (
            Map.entry(ExtraField.WEB_DNSSEC, new CellAddress("AT12")),
            Map.entry(ExtraField.WEB_TLS_AVAILABLE, new CellAddress("AU12")),
            Map.entry(ExtraField.WEB_HTTPS_REDIRECT, new CellAddress("AW12")),
            Map.entry(ExtraField.WEB_HSTS, new CellAddress("AX12")),
            Map.entry(ExtraField.WEB_IPV6, new CellAddress("AY12")),
            Map.entry(ExtraField.WEB_IPV6_NAME_SERVER, new CellAddress("AZ12")),
            Map.entry(ExtraField.WEB_IPV6_WEB_SERVER, new CellAddress("BA12")),

            Map.entry(ExtraField.MAIL_DMARC, new CellAddress("AS12")),
            Map.entry(ExtraField.MAIL_DKIM, new CellAddress("AT12")),
            Map.entry(ExtraField.MAIL_SPF, new CellAddress("AU12")),
            Map.entry(ExtraField.MAIL_DMARC_POLICY, new CellAddress("AV12")),
            Map.entry(ExtraField.MAIL_SPF_POLICY, new CellAddress("AW12")),
            Map.entry(ExtraField.MAIL_STARTTLS, new CellAddress("AX12")),
            Map.entry(ExtraField.MAIL_DNSSEC_MAILTO_EXIST, new CellAddress("AZ12")),
            Map.entry(ExtraField.MAIL_DNSSEC_MX_EXIST, new CellAddress("BA12")),
            Map.entry(ExtraField.MAIL_DANE, new CellAddress("BB12")),
            Map.entry(ExtraField.MAIL_IPV6, new CellAddress("BC12")),
            Map.entry(ExtraField.MAIL_IPV6_NAME_SERVER, new CellAddress("BD12")),
            Map.entry(ExtraField.MAIL_IPV6_MAIL_SERVER, new CellAddress("BE12"))
        );

    private static final Map<ExtraField, CellAddress> EXTRA_FIELD_POINTS_TO_ADDRESS =
        Stream.of(ExtraField.values())
            .map
            (
                (extraField) ->
                {
                    CellAddress pointsTo;
                    if (extraField.getCategory().isPresent())
                        pointsTo = ADDRESS_CATEGORY.get(extraField.getCategory().get());
                    else
                    {
                        Test test = extraField.getTest().get();
                        Category category = test.getCategory();

                        int index = Test.values(category).indexOf(test);

                        CellAddress categoryAddress = ADDRESS_CATEGORY.get(category);

                        pointsTo = new CellAddress(categoryAddress.getRow(),
                            categoryAddress.getColumn() + 1 + index);
                    }

                    return Map.entry(extraField, pointsTo);
                }
            )
            .collect(Collectors.toUnmodifiableMap(
                (entry) -> entry.getKey(), (entry) -> entry.getValue())); */


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
            RequestType.WEB, new CellAddress("BB1").getColumn(),
            RequestType.MAIL, new CellAddress("BJ1").getColumn()
        );

    
    private final Sheet report;

    private final ListTest listResults;

    //#region Report options

    private boolean fullReport;

    //#endregion

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


    public int getTotalDomains()
    {
        return this.listResults.getResults().getDomains().size();
    }

    public RequestType getType()
    {
        return getInternetnlRequest().getRequest_type();
    }

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

        int currentDomainRow = FIRST_DOMAIN_ROW.getRow();
        for (Entry<String, DomainResults> domainResults : resultsByDomain.entrySet())
        {
            Row row = report.createRow(currentDomainRow++);
            int currentColumn = FIRST_DOMAIN_ROW.getColumn();

            // set list name
            row.createCell(currentColumn++, CellType.STRING).setCellValue(listResults.getName());

            String domain = domainResults.getKey();
            DomainResults results = domainResults.getValue();

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

    private void setAverageScore()
    {
        if (getTotalDomains() == 0)
            return;
        
        Cell cell = report.getRow(ADDRESS_AVERAGE_SCORE.getRow())
            .createCell(ADDRESS_AVERAGE_SCORE.getColumn(), CellType.FORMULA);
        
        CellAddress range1, range2;
        range1 = new CellAddress(FIRST_DOMAIN_ROW.getRow(), ADDRESS_AVERAGE_SCORE.getColumn());
        range2 = new CellAddress(range1.getRow() + getTotalDomains() - 1, range1.getColumn());
        cell.setCellFormula(String.format("AVERAGE(%s:%s)", range1.formatAsString(), range2.formatAsString()));
    }

    //#region Domain Statistics

    private void setDomainStatistics(Results results)
    {
        for (Category category : Category.values(getType()))
            setDomainStatistics(results, category);

        setCustomFieldStatistics(results);
    }

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

    private void incCell(ResultStatus result, int column)
    {
        Cell cell = this.report.getRow(ADDRESS_RESULT.get(result).getRow()).getCell(column);
        int value = (int)cell.getNumericCellValue();
        cell.setCellValue(value + 1);
    }

    //#endregion

    //#region Set number tested domains

    private void setTestedDomains(int testedDomains)
    {
        RequestType type  = getType();
        Row row = this.report.getRow(ADDRESS_TESTED_DOMAINS.getRow());
        for (Category category : Category.values(type))
            setTestedDomains(row, category, testedDomains);

        // custom fields
        for (CellAddress customCellAddress : ADDRESS_CUSTOM_FIELD.get(type).values())
            setTestedDomains(row, customCellAddress.getColumn(), testedDomains);
    }

    private void setTestedDomains(Row row, Category category, int testedDomains)
    {
        CellAddress categoryAddress = ADDRESS_CATEGORY.get(category);
        int currentColumn = categoryAddress.getColumn();

        setTestedDomains(row, currentColumn++, testedDomains);

        for (int i = 0; i < Test.values(category).size(); i++)
            setTestedDomains(row, currentColumn++, testedDomains);
    }

    private void setTestedDomains(Row row, int column, int testedDomains)
    {
        row.getCell(column).setCellValue(testedDomains);
    }

    //#endregion

    //#region Set Domain Results - Full report

    private void setDomainResults(Row domainRow, Results results)
    {
        RequestType type = getType();
        for (Category category : Category.values(type))
            setDomainResults(domainRow, results, category);
        
        // TODO: set extra fields - points to...

        // custom fields

        Map<CustomTest, CellAddress> addresses = ADDRESS_CUSTOM_FIELD.get(type);

        for (Entry<CustomTest, Object> test : results.getCustom().entrySet())
        {
            CustomTest customTest = test.getKey();
            ResultStatus result = customTest.convertToResult(test.getValue());

            setDomainResult(domainRow, addresses.get(customTest).getColumn(), result);
        }
    }

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

    private void setDomainResult(Row domainRow, int column, ResultStatus result)
    {
        Cell cell = domainRow.createCell(column, CellType.STRING);
        cell.setCellValue(result.getStatus());
    }

    //#endregion

    private void setConditionalFormatting()
    {
        SheetConditionalFormatting formatting = this.report.getSheetConditionalFormatting();

        // calculate results range
        CellAddress firstAddress = new CellAddress(FIRST_DOMAIN_ROW.getRow(), FIRST_RESULT_COLUMN);
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
