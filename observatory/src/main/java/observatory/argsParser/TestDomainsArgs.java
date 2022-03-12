package observatory.argsParser;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import observatory.argsParser.options.Option;
import observatory.argsParser.options.OptionType;
import observatory.argsParser.options.OptionValue;
import observatory.argsParser.options.ParseOptions;
import observatory.internetnlAPI.config.RequestType;
import observatory.util.Util;

import static observatory.argsParser.ArgsParser.*;

public class TestDomainsArgs {
    private static final String DEFAULT_INTERNTNL_API_CONFIG_FILE_NAME = "intnl.properties";
    public static final String DEFAULT_DOMAINS_WORKBOOK_FILE_NAME = "domains.xlsx";

    public static final Option OPTION_WORKING_DIR = new Option("--dir", OptionType.SINGLE);
    public static final Option OPTION_CONFIG_FILE = new Option("--conf", OptionType.SINGLE);
    public static final Option OPTION_DOMAINS_FILE = new Option("--dom", OptionType.SINGLE);

    private static final ParseOptions PARSE_OPTIONS = new ParseOptions(
            Set.of(OPTION_WORKING_DIR, OPTION_CONFIG_FILE, OPTION_DOMAINS_FILE));

    private final RequestType type;

    private final Map<Option, OptionValue> options;

    private final Set<String> listsToTest;

    private File workingDir, configFile, domainsFile;

    public TestDomainsArgs(List<String> args) throws ParserException {
        if (args.isEmpty())
            throw new ParserException("Not enough arguments.");

        this.type = parseType(args.remove(0));
        this.options = PARSE_OPTIONS.parse(args);
        this.listsToTest = Set.copyOf(args);

        args.clear();
    }

    /**
     * @return the type
     */
    public RequestType getType() {
        return type;
    }

    /**
     * @return the listsToTest
     */
    public Set<String> getListsToTest() {
        return listsToTest;
    }

    public File getWorkingDir()
    {
        if (this.workingDir == null)
            this.workingDir = getOption(this.options, OPTION_WORKING_DIR,
                (Function<OptionValue, File>) (optionValue) ->
                {
                    return new File(optionValue.getSingle());
                },
                Util::getCurrentWorkingDir);

        return this.workingDir;
    }

    public File getConfigFile()
    {
        if (this.configFile == null)
            this.configFile = getOption(this.options, OPTION_CONFIG_FILE,
                (Function<OptionValue, File>) (optionValue) ->
                {
                    return new File(optionValue.getSingle());
                },
                () -> new File(getWorkingDir(), DEFAULT_INTERNTNL_API_CONFIG_FILE_NAME));

        return this.configFile;
    }

    public File getDomainsFile()
    {
        if (this.domainsFile == null)
            this.domainsFile = getOption(this.options, OPTION_DOMAINS_FILE,
                (Function<OptionValue, File>) (optionValue) ->
                {
                    return new File(optionValue.getSingle());
                },
                () -> new File(getWorkingDir(), DEFAULT_DOMAINS_WORKBOOK_FILE_NAME));

        return this.domainsFile;
    }

    public static void printHelp() {
        System.out.println("-> test <web | mail> [options] [name of lists to test]");
        System.out.println("Test the lists of domains specified in the domains workbook file and place " +
                "the results in the working dir. " +
                "If the names of lists to test are not specified then all lists in the domains file are tested.\n\n" +

                "[options]:\n" +

                "\t" + OPTION_WORKING_DIR.getName() + " working-dir-path -> The directory to save all the results, configurations and log files. " +
                "If not defined, defaults to the current directory.\n" +

                "\t" + OPTION_CONFIG_FILE.getName() + " config-file-path -> The path to the Internet.nl API config file. " +
                "If not defined, defaults to \"" + DEFAULT_INTERNTNL_API_CONFIG_FILE_NAME + "\" in the working directory (working-dir-path).\n" +

                "\t" + OPTION_DOMAINS_FILE.getName() + " domains-file-path -> The path to the workbook file that contains the lists of domains to test. " +
                "If not defined, defaults to \"" + DEFAULT_DOMAINS_WORKBOOK_FILE_NAME + "\" in the working directory (working-dir-path).\n");
    }
}
