package observatory.argsParser;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import observatory.argsParser.options.Option;
import observatory.argsParser.options.OptionType;
import observatory.argsParser.options.OptionValue;
import observatory.argsParser.options.ParseOptions;
import observatory.internetnlAPI.config.RequestType;

import static observatory.argsParser.ArgsParser.*;

/**
 * A class to parse the arguments for the report cmd.
 * 
 * @author Henrique Campos Ferreira
 */
public class ReportArgs
{
    private static final String WEB_TEMPLATE_FILE = "template-web.xlsx";
    private static final String MAIL_TEMPLATE_FILE = "template-mail.xlsx";

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    public static final Option OPTION_TEMPLATE_FILE = new Option("--template", OptionType.SINGLE);
    public static final Option OPTION_DATE = new Option("--date", OptionType.SINGLE);
    public static final Option OPTION_FULL_REPORT = new Option("--full-report", OptionType.LIST);
    public static final Option OPTION_NO_ORDER = new Option("--no-order", OptionType.LIST);

    private static final ParseOptions PARSE_OPTIONS = new ParseOptions(
            Set.of(OPTION_TEMPLATE_FILE, OPTION_DATE, OPTION_FULL_REPORT, OPTION_NO_ORDER));

    private final RequestType type;

    private final Map<Option, OptionValue> options;

    private final File reportFile;

    private final List<File> listsResultFiles;


    private File templateFile;
    private Calendar date;
    private List<String> listsFullReport, listsNoOrder;


    public ReportArgs(List<String> args) throws ParserException {
        if (args.isEmpty())
            throw new ParserException("Not enough arguments.");

        this.type = parseType(args.remove(0));
        this.options = PARSE_OPTIONS.parse(args);
        this.reportFile = parseReportFile(args);
        this.listsResultFiles = parseResultFiles(args);

        args.clear();
    }

    private static File parseReportFile(List<String> args) throws ParserException
    {
        if (args.isEmpty())
            throw new ParserException("Not enough arguments.");

        return new File(args.remove(0));
    }

    private static List<File> parseResultFiles(List<String> args) throws ParserException
    {
        if (args.isEmpty())
            throw new ParserException("Not enough arguments.");

        return args.stream().map(File::new).collect(Collectors.toUnmodifiableList());
    }

    /**
     * @return the type
     */
    public RequestType getType() {
        return type;
    }

    /**
     * @return the reportFile
     */
    public File getReportFile() {
        return reportFile;
    }

    /**
     * @return the listsResultFiles
     */
    public List<File> getListsResultFiles() {
        return listsResultFiles;
    }

    //#region Options

    public File getTemplateFile()
    {
        if (this.templateFile == null)
            this.templateFile = getOption(this.options, OPTION_TEMPLATE_FILE, (Function<OptionValue, File>)
                (optionValue) ->
                {
                    return new File(optionValue.getSingle());
                },
                () -> new File(this.type == RequestType.WEB ? WEB_TEMPLATE_FILE : MAIL_TEMPLATE_FILE));

        return this.templateFile;
    }

    public Calendar getReportDate() throws ParserException
    {
        if (this.date == null)
            this.date = getOption(this.options, OPTION_DATE, (ParseValueFunction<Calendar>)
                (optionValue) ->
                {
                    return parseDate(optionValue.getSingle());
                },
                Calendar::getInstance);

        return this.date;
    }

    public List<String> getListsFullReport()
    {
        if (this.listsFullReport == null)
            this.listsFullReport = getOption(this.options, OPTION_FULL_REPORT,
                (Function<OptionValue, List<String>>) OptionValue::getList,
                List::of);

        return this.listsFullReport;
    }

    public List<String> getListsNoOrder()
    {
        if (this.listsNoOrder == null)
            this.listsNoOrder = getOption(this.options, OPTION_NO_ORDER,
                (Function<OptionValue, List<String>>) OptionValue::getList,
                List::of);

        return this.listsNoOrder;
    }

    //#endregion

    private static Calendar parseDate(String date) throws ParserException
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(dateFormat.parse(date));
            return cal;
        } catch (ParseException e) {
            throw new ParserException("Invalid date.", e);
        }
    }

    public static void printHelp() {
        System.out.println("-> report <web | mail> [options] <report-file-name.xlsx> <list of tests results file names>");
        System.out.println("Create a report of the specified type based on the tests results provided.\n");
        System.out.println(
            "[options]:\n" +

            "\t" + OPTION_TEMPLATE_FILE.getName() + " template-file-path -> The path to the report template. " +
            "If not defined, defaults to \"" + WEB_TEMPLATE_FILE + "\" for a report of type web or " +
            "\"" + MAIL_TEMPLATE_FILE + "\" for a report of type mail in the current directory.\n" +

            "\t" + OPTION_DATE.getName() +  " -> The date of the report in <" + DATE_FORMAT + "> format. " +
            "If not defined, defaults to the current date.\n" +

            "\t" + OPTION_FULL_REPORT.getName() + " list-name -> The report of the specified list name will have the full results. " +
            "This option can be repeated.\n"
        );
    }
}
