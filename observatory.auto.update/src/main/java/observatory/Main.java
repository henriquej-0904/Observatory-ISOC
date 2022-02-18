package observatory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import observatory.internetnlAPI.InternetnlAPI;
import observatory.internetnlAPI.InternetnlAPIWithPythonScripts;
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

        System.out.println("-> report <WEB | MAIL> <report.xlsx> <results folder> [lists to create a full report]");
        System.out.println("Create a report of the specified type based on the results provided.");
        System.out.println(
            "Options:\n"+
            "\tlists to create a full report -> The report of the specified lists will have the full results.\n"
        );
    }

    private static void invalidArgs()
    {
        System.err.println("Invalid arguments.");
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
        
        try
        (
            InternetnlAPI api =
                new InternetnlAPIWithPythonScripts(new File("internet.nl-python-scripts/batch.py"));
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

    private static void report(List<String> args)
    {
        if (args.size() < 3)
            invalidArgs();
        
        try
        {
            RequestType type = RequestType.parseType(args.remove(0));
            File report = new File(args.remove(0));
            File resultsFolder = new File(args.remove(0));

            if (args.isEmpty())
                Report.generateAndSaveReport(type, report, resultsFolder);
            else
                Report.generateAndSaveReport(type, report, resultsFolder, args);

            System.out.println("Report generated successfully.");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(EXIT_ERROR_STATUS);
        }
    }
}
