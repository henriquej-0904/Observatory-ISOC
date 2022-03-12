package observatory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import observatory.argsParser.ParserException;
import observatory.argsParser.ReportArgs;
import observatory.argsParser.TestDomainsArgs;
import observatory.internetnlAPI.InternetnlAPI;
import observatory.internetnlAPI.InternetnlAPIOverNetwork;
import observatory.internetnlAPI.config.InternetnlRequest;
import observatory.internetnlAPI.config.testResult.TestResult;
import observatory.report.Report;
import observatory.tests.ListTest;
import observatory.tests.TestDomains;
import observatory.tests.index.Index;
import observatory.util.InvalidFormatException;
import observatory.util.Util;

public class Main
{
    private static final int EXIT_ERROR_STATUS = 1;

    private static final SimpleDateFormat DATE_FORMAT_PRINT_TEST_PROGRESS = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args)
    {        
        if (args.length == 0)
        {
            printHelp();
            return;
        }

        List<String> nextArgs = new LinkedList<>();
        for (int i = 1; i < args.length; i++)
            nextArgs.add(args[i]);
        
        try
        {
            switch (args[0].toLowerCase()) {
                case "test":
                    testDomains(new TestDomainsArgs(nextArgs));
                    break;

                case "report":
                    report(new ReportArgs(nextArgs));
                    break;

                default:
                    invalidArgsExit();
                    break;
            }
        } catch (ParserException e) {
            invalidArgsExit(e.getMessage());
        }
    }

    private static void printHelp()
    {
        System.out.println("Observatory commands:\n");
        TestDomainsArgs.printHelp();
        System.out.println("-------------------------------------------------------------------------------------------------------\n");
        ReportArgs.printHelp();
    }

    private static void invalidArgsExit()
    {
        System.err.println("Invalid arguments.\n");
        printHelp();
        System.exit(EXIT_ERROR_STATUS);
    }

    private static void invalidArgsExit(String msg)
    {
        System.err.println("Invalid arguments: " + msg + "\n");
        printHelp();
        System.exit(EXIT_ERROR_STATUS);
    }

    //#region Test Domains

    private static void testDomains(TestDomainsArgs args)
    {
        try
        (
            InternetnlAPI api = getInternetnlAPI(args.getConfigFile());
            TestDomains tests = initTestDomains(args, api);
        )
        {
            tests.start();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(EXIT_ERROR_STATUS);
        }
    }

    private static TestDomains initTestDomains(TestDomainsArgs args, InternetnlAPI api)
        throws InvalidFormatException, IOException
    {
        File workingDir = args.getWorkingDir();
        File domainsFile = args.getDomainsFile();
        Index index = Util.getIndexIfExists(new File(args.getWorkingDir(), Index.DEFAULT_FILE_NAME));

        TestDomains tests;
        if (args.getListsToTest().isEmpty()) // test all lists
            tests = new TestDomains(args.getType(), api, index, workingDir, domainsFile);
        else
            // test the specified lists
            tests = new TestDomains(args.getType(), api, index, workingDir, domainsFile, args.getListsToTest());

        tests.setListSubmittedListener((submitted) ->
            printTestProgress(submitted.getRequest(), "submitted"));

        tests.setListFetchedResultsListener((fetchedResults) ->
            printTestProgress(fetchedResults.getResults().getRequest(), "fetched"));

        return tests;
    }

    private static void printTestProgress(InternetnlRequest info, String status)
    {
        final String format = "%s\t%s\t%s\t%s\t%s\n";
        System.out.printf(format,
            DATE_FORMAT_PRINT_TEST_PROGRESS.format(Calendar.getInstance().getTime()),
            info.getName(),
            info.getRequest_type().getType(),
            info.getRequest_id(),
            status);
    }

    //#endregion

    //#region Report

    private static void report(ReportArgs args)
    {        
        try
        {
            Report report = new Report(args.getType(), args.getTemplateFile(),
                parseListResults(args));

            report.setReportDate(args.getReportDate());
            report.setListsFullReport(Set.copyOf(args.getListsFullReport()));

            report.generateAndSaveReport(args.getReportFile());
            System.out.println("Report generated successfully.");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(EXIT_ERROR_STATUS);
        }
    }

    private static Set<ListTest> parseListResults(ReportArgs args) throws IOException, InvalidFormatException
    {
        List<File> listsResultFiles = args.getListsResultFiles();
        Set<ListTest> result = new LinkedHashSet<>(listsResultFiles.size());
        
        for (File listResultFile : listsResultFiles)
        {
            TestResult testResult = TestResult.fromFile(listResultFile);
            result.add(ListTest.from(testResult));
        }

        return result;
    }

    //#endregion

    /**
     * Get the Internet.nl API based on the specified config file.
     * @param configFile - The config file.
     * @return The Internet.nl API
     */
    private static InternetnlAPI getInternetnlAPI(File configFile)
    {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(configFile))
        {
            props.load(input);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(EXIT_ERROR_STATUS);
        }

        Function<String, String> getProperty =
            (prop) -> {
                String value = props.getProperty(prop);
                if (value == null)
                    invalidArgsExit(String.format("Invalid Internet.nl API config file. %s is not defined.", prop));

                return value;
            };

        URI endpoint = null;
        String username = getProperty.apply("username");
        String password = getProperty.apply("password");

        try {
            endpoint = new URI(getProperty.apply("endpoint"));
        } catch (URISyntaxException e)
        {
            invalidArgsExit("Invalid endpoint URI in Internet.nl API config file.");
        }

        return new InternetnlAPIOverNetwork(endpoint, username, password);
    }
}
