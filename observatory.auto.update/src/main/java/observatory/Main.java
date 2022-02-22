package observatory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import observatory.internetnlAPI.InternetnlAPI;
import observatory.internetnlAPI.InternetnlAPIOverNetwork;
import observatory.internetnlAPI.config.RequestType;
import observatory.report.Report;
import observatory.update.TestDomains;

public class Main
{
    private static final int EXIT_ERROR_STATUS = 1;

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
        
        switch (args[0].toLowerCase())
        {
            case "test":
                testDomains(nextArgs);
                break;
        
            case "report":
                report(nextArgs);
                break;

            default:
                invalidArgs();
                break;
        }
    }

    private static void printHelp()
    {
        System.out.println("Observatory commands:\n");

        System.out.println("-> test <ALL | WEB | MAIL> <domains.xlsx> <results folder> [-o] [lists to test]");
        System.out.println("Test the lists of domains specified in the domains excel file and place "
        + "the results in the results folder.\n" +
        "If the results folder is not empty then this command " +
        "will test all the remaining lists.\n" +
        "Options:\n"+
        "\t-o -> Overwrite results if they were already tested.\n" +
        "\tlists to test -> A set of lists to test.\n");

        System.out.println("-> report <WEB | MAIL> <report.xlsx> <results folder> [--date <day/month/year>] [lists to create a full report]");
        System.out.println("Create a report of the specified type based on the results provided.");
        System.out.println(
            "Options:\n"+
            "\t--date -> The date of the report in <day/month/year> format.\n" +
            "\tlists to create a full report -> The report of the specified lists will have the full results.\n"
        );
    }

    private static void invalidArgs()
    {
        System.err.println("Invalid arguments.\n");
        printHelp();
        System.exit(EXIT_ERROR_STATUS);
    }

    private static void invalidArgs(String msg)
    {
        System.err.println("Invalid arguments: " + msg + "\n");
        printHelp();
        System.exit(EXIT_ERROR_STATUS);
    }

    private static void testDomains(List<String> args)
    {
        if (args.size() < 3)
            invalidArgs();
        
        String type = args.remove(0).toUpperCase();
        File domains = new File(args.remove(0));
        File resultsFolder = new File(args.remove(0));

        boolean overwrite;

        if (args.isEmpty())
            overwrite = false;
        else
        {
            String arg = args.get(0);
            if (overwrite = arg.toLowerCase().equals("-o"))
                args.remove(0);
        }

        Properties batchConf = getInternetnlAPI_BatchConfig();
        String endpoint = batchConf.getProperty("endpoint");
        String username = batchConf.getProperty("username");
        String password = batchConf.getProperty("password");

        try
        (
            InternetnlAPI api =
                //new InternetnlAPIWithPythonScripts(new File("internet.nl-python-scripts/batch.py"));
                new InternetnlAPIOverNetwork(new URI(endpoint), username, password);
        )
        {
            TestDomains tests;
            if (!args.isEmpty())
                tests = new TestDomains(domains, args, resultsFolder, api);
            else
                tests = new TestDomains(domains, resultsFolder, api);

            if (type.equals("ALL"))
                tests.Start(overwrite);
            else
                tests.Start(RequestType.parseType(type), overwrite);

            System.out.println("All tests completed successfully!");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(EXIT_ERROR_STATUS);
        }
    }

    private static Properties getInternetnlAPI_BatchConfig()
    {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("config/internetnlBatchAPI.properties");)
        {
            props.load(input);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(EXIT_ERROR_STATUS);
        }
        return props;
    }

    private static void report(List<String> args)
    {
        if (args.size() < 3)
            invalidArgs();
        
        try
        {
            RequestType type = RequestType.parseType(args.remove(0));
            File reportLocation = new File(args.remove(0));
            File resultsFolder = new File(args.remove(0));

            Report report = new Report(type, resultsFolder);

            if (!args.isEmpty() && args.get(0).equals("--date"))
            {
                args.remove(0);
                report.setReportDate(parseDate(args));
            }

            if (!args.isEmpty())
                report.setListsFullReport(args);

            report.generateAndSaveReport(reportLocation);
            System.out.println("Report generated successfully.");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(EXIT_ERROR_STATUS);
        }
    }

    private static Calendar parseDate(List<String> args)
    {
        if (args.isEmpty())
            invalidArgs("Expected a date.");

        final String dateFormatStr = "dd/MM/yyyy";
        final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        final Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(dateFormat.parse(args.remove(0)));
        } catch (ParseException e) {
            invalidArgs("Invalid date format specified.");
        }

        return cal;
    }
}
