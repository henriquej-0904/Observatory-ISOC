package observatory.report;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;

import observatory.internetnlAPI.config.RequestType;
import observatory.internetnlAPI.config.results.domain.Category;
import observatory.internetnlAPI.config.results.domain.CustomTest;
import observatory.internetnlAPI.config.results.domain.DomainResults;
import observatory.internetnlAPI.config.results.domain.Result;
import observatory.internetnlAPI.config.results.domain.ResultStatus;
import observatory.internetnlAPI.config.results.domain.Results;
import observatory.internetnlAPI.config.results.domain.Test;
import observatory.util.ListTest;

/**
 * A class to create a report from the results of a List.
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

    private static final CellAddress ADDRESS_EXTRA_FIELD_WEB_TLS_1_3_SUPPORT =
        new CellAddress("BB12");

    private static final CellAddress ADDRESS_EXTRA_FIELD_MAIL_TLS_1_3_SUPPORT =
        new CellAddress("BJ12");

    private static final CellAddress ADDRESS_EXTRA_FIELD_MAIL_SENDING_DOMAIN =
        new CellAddress("BF12");

    private static final CellAddress ADDRESS_EXTRA_FIELD_MAIL_SERVER_TESTABLE =
        new CellAddress("BG12");


    /**
     * Create a report for the specified List results and template.
     * 
     * @param reportTemplate - The template of the List Report.
     * @param listResults - The results of the List.
     * 
     * @return The generated List report as a Sheet.
     */
    public static Sheet createListReport(Sheet reportTemplate, ListTest listResults)
    {
        Workbook workbook = reportTemplate.getWorkbook();
        Sheet report = workbook.cloneSheet(workbook.getSheetIndex(reportTemplate));

        RequestType type = listResults.getResults().getRequest().getRequest_type();

        // Set report name
        workbook.setSheetName(workbook.getSheetIndex(report), listResults.getName());

        int totalDomains, testedDomains;

        Map<String, DomainResults> resultsByDomain = listResults.getResults().getDomains();
        totalDomains = resultsByDomain.size();
        testedDomains = 0;

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

            setDomainStatistics(report, results.getResults(), type);
        }
    
        setTestedDomains(report, type, testedDomains);
        setAverageScore(report, totalDomains);

        return report;
    }

    private static void setAverageScore(Sheet report, int totalDomains)
    {
        if (totalDomains == 0)
            return;
        
        Cell cell = report.getRow(ADDRESS_AVERAGE_SCORE.getRow())
            .createCell(ADDRESS_AVERAGE_SCORE.getColumn(), CellType.FORMULA);
        
        CellAddress range1, range2;
        range1 = new CellAddress(FIRST_DOMAIN_ROW.getRow(), ADDRESS_AVERAGE_SCORE.getColumn());
        range2 = new CellAddress(range1.getRow() + totalDomains - 1, range1.getColumn());
        cell.setCellFormula(String.format("AVERAGE(%s:%s)", range1.formatAsString(), range2.formatAsString()));
    }

    private static void setDomainStatistics(Sheet report, Results results, RequestType type)
    {
        for (Category category : Category.values(type))
            setDomainStatistics(report, results, category);

        setExtraFieldStatistics(report, results, type);
    }

    private static void setDomainStatistics(Sheet report, Results results, Category category)
    {
        CellAddress categoryAddress = ADDRESS_CATEGORY.get(category);
        int currentColumn = categoryAddress.getColumn();

        Result result = results.getCategories().get(category);
        setResult(report, result, currentColumn++);

        for (Test test : Test.values(category))
        {
            result = results.getTests().get(test);
            setResult(report, result, currentColumn++);
        }
    }

    private static void setResult(Sheet report, Result result, int column)
    {
        Cell cell = report.getRow(ADDRESS_RESULT.get(result.getStatus()).getRow()).getCell(column);
        int value = (int)cell.getNumericCellValue();
        cell.setCellValue(value + 1);
    }

    private static void setExtraFieldStatistics(Sheet report, Results results, RequestType type)
    {
        Map<CustomTest, Object> custom = results.getCustom();

        setExtraFieldTls_1_3_Support(report, custom.get(CustomTest.TLS_1_3_SUPPORT), type);

        if (type == RequestType.WEB)
            return;

        setExtraFieldMailSendingDomain(report, custom.get(CustomTest.MAIL_NON_SENDING_DOMAIN));
        setExtraFieldMailServersTestable(report, custom.get(CustomTest.MAIL_SERVER_TESTABLE));
    }

    private static void setExtraFieldTls_1_3_Support(Sheet report, Object result, RequestType type)
    {
        CellAddress tls_1_3Address;
        if (type == RequestType.WEB)
            tls_1_3Address = ADDRESS_EXTRA_FIELD_WEB_TLS_1_3_SUPPORT;
        else
            tls_1_3Address = ADDRESS_EXTRA_FIELD_MAIL_TLS_1_3_SUPPORT;

        Cell cell;
        if (result.equals("yes"))
            cell = report.getRow(ADDRESS_RESULT.get(ResultStatus.STATUS_SUCCESS).getRow())
                    .getCell(tls_1_3Address.getColumn());
        else
            cell = report.getRow(ADDRESS_RESULT.get(ResultStatus.STATUS_FAIL).getRow())
                    .getCell(tls_1_3Address.getColumn());

        int value = (int)cell.getNumericCellValue();
        cell.setCellValue(value + 1);
    }

    /**
     * Sets the extra field mail sending domain.
     * As specified in CustomTest.MAIL_NON_SENDING_DOMAIN documentation,
     * the result of this test should be negated to avoid confusion.
     * 
     * @param report
     * @param result
     * 
     * @see CustomTest
     */
    private static void setExtraFieldMailSendingDomain(Sheet report, Object result)
    {
        Cell cell;

        /**
         * Negate the result
         */
        if (!(Boolean)result)
            cell = report.getRow(ADDRESS_RESULT.get(ResultStatus.STATUS_SUCCESS).getRow())
                    .getCell(ADDRESS_EXTRA_FIELD_MAIL_SENDING_DOMAIN.getColumn());
        else
            cell = report.getRow(ADDRESS_RESULT.get(ResultStatus.STATUS_FAIL).getRow())
                    .getCell(ADDRESS_EXTRA_FIELD_MAIL_SENDING_DOMAIN.getColumn());
        
        int value = (int)cell.getNumericCellValue();
        cell.setCellValue(value + 1);
    }

    private static void setExtraFieldMailServersTestable(Sheet report, Object result)
    {
        Cell cell;
        if (result.equals("ok"))
            cell = report.getRow(ADDRESS_RESULT.get(ResultStatus.STATUS_SUCCESS).getRow())
                    .getCell(ADDRESS_EXTRA_FIELD_MAIL_SERVER_TESTABLE.getColumn());
        else
            cell = report.getRow(ADDRESS_RESULT.get(ResultStatus.STATUS_FAIL).getRow())
                    .getCell(ADDRESS_EXTRA_FIELD_MAIL_SERVER_TESTABLE.getColumn());
        
        int value = (int)cell.getNumericCellValue();
        cell.setCellValue(value + 1);
    }


    private static void setTestedDomains(Sheet report, RequestType type, int testedDomains)
    {
        Row row = report.getRow(ADDRESS_TESTED_DOMAINS.getRow());
        for (Category category : Category.values(type))
            setTestedDomains(row, category, testedDomains);

        // extra fields

        // tls 1.3
        int column;
        if (type == RequestType.WEB)
            column = ADDRESS_EXTRA_FIELD_WEB_TLS_1_3_SUPPORT.getColumn();
        else
            column = ADDRESS_EXTRA_FIELD_MAIL_TLS_1_3_SUPPORT.getColumn();

        setTestedDomains(row, column, testedDomains);

        if (type == RequestType.WEB)
            return;

        // mail sending domain
        column = ADDRESS_EXTRA_FIELD_MAIL_SENDING_DOMAIN.getColumn();
        setTestedDomains(row, column, testedDomains);

        // mail server testable
        column = ADDRESS_EXTRA_FIELD_MAIL_SERVER_TESTABLE.getColumn();
        setTestedDomains(row, column, testedDomains);
    }

    private static void setTestedDomains(Row row, Category category, int testedDomains)
    {
        CellAddress categoryAddress = ADDRESS_CATEGORY.get(category);
        int currentColumn = categoryAddress.getColumn();

        setTestedDomains(row, currentColumn++, testedDomains);

        for (int i = 0; i < Test.values(category).size(); i++)
            setTestedDomains(row, currentColumn++, testedDomains);
    }

    private static void setTestedDomains(Row row, int column, int testedDomains)
    {
        row.getCell(column).setCellValue(testedDomains);
    }
}
