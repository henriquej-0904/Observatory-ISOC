package observatory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import observatory.internetnlAPI.InternetnlAPI;
import observatory.internetnlAPI.InternetnlAPIWithPythonScripts;
import observatory.internetnlAPI.config.RequestType;
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
        
            default:
                invalidArgs();
                break;
        }
    }

    private static void printHelp()
    {
        System.out.println("Observatory commands:\n");

        System.out.println("-> test <[ALL | WEB | MAIL]> <domains.xlsx> <results folder> [-o] [lists to test]");
        System.out.println("Test the lists of domains specified in the domains excel file and place "
        + "the results in the results folder.\n" +
        "If the results folder is not empty then this command " +
        "will test all the remaining lists.\n" +
        "Options:\n"+
        "\t-o -> Overwrite results if they were already tested.\n" +
        "\tlists to test -> A set of lists to test.\n");
    }

    private static void invalidArgs()
    {
        System.err.println("Invalid arguments.");
        printHelp();
        System.exit(EXIT_ERROR_STATUS);
    }

    private static void invalidArgs(String msg)
    {
        System.err.println(msg);
        printHelp();
        System.exit(EXIT_ERROR_STATUS);
    }

    private static void testDomains(List<String> args)
    {
        if (args.size() < 3)
            invalidArgs();
        
        String type = args.remove(0);
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

            switch (type.toUpperCase()) {
                case "ALL":
                    tests.Start(overwrite);
                    break;
                case "WEB":
                    tests.Start(RequestType.WEB, overwrite);
                    break;
                case "MAIL":
                    tests.Start(RequestType.MAIL, overwrite);
                    break;
                default:
                    invalidArgs("Invalid type.");
                    break;
            }

            System.out.println("All tests completed successfully!");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
