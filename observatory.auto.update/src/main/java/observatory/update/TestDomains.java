package observatory.update;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.gembox.spreadsheet.ExcelFile;
import com.gembox.spreadsheet.ExcelWorksheetCollection;

import observatory.internetnlAPI.InternetnlAPI;
import observatory.internetnlAPI.InternetnlAPIException;
import observatory.internetnlAPI.config.RequestType;

/**
 * A class to test the domains in a set of lists.
 */
public class TestDomains
{
    private static Logger log = Logger.getLogger(TestDomains.class.getName());

    private final File domains, webResults, mailResults;

    private final List<String> listsOfDomains;

    private final InternetnlAPI api;

    /**
     * Creates a new Test configuration.
     * 
     * @param domains - The file with the domains.
     * @param resultsFolder - The folder to output the results.
     * @param api - The Internet.nl API.
     * @throws IOException
     */
    public TestDomains(File domains, File resultsFolder, InternetnlAPI api) throws IOException
    {
        this.domains = domains;
        Objects.requireNonNull(resultsFolder);
        this.webResults = new File(resultsFolder, "web");
        this.mailResults = new File(resultsFolder, "mail");

        ExcelFile domainsExcel = ExcelFile.load(domains.getPath());
        ExcelWorksheetCollection worksheets = domainsExcel.getWorksheets();
        
        if (worksheets.size() == 0)
            throw new IllegalArgumentException("There is no domains to test.");

        this.listsOfDomains = new ArrayList<>(worksheets.size());
        worksheets.forEach((worksheet) -> this.listsOfDomains.add(worksheet.getName()));

        this.api = Objects.requireNonNull(api);
    }

    /**
     * Creates a new Test configuration.
     * 
     * @param domains - The file with the domains.
     * @param listsOfDomains - The name of the lists to test.
     * @param resultsFolder - The folder to output the results.
     * @param api - The Internet.nl API.
     * @throws IOException
     */
    public TestDomains(File domains, List<String> listsOfDomains, File resultsFolder,
        InternetnlAPI api) throws IOException
    {
        this.domains = domains;
        Objects.requireNonNull(resultsFolder);
        this.webResults = new File(resultsFolder, "web");
        this.mailResults = new File(resultsFolder, "mail");

        ExcelFile domainsExcel = ExcelFile.load(domains.getPath());
        ExcelWorksheetCollection worksheets = domainsExcel.getWorksheets();
        
        if (worksheets.size() == 0)
            throw new IllegalArgumentException("There is no domains to test.");

        for (String list : Objects.requireNonNull(listsOfDomains)) {
            if (!worksheets.contains(list))
                throw new IllegalArgumentException(
                    String.format("There is no list named %s in the specified domains file.", list)
                );
        }

        this.listsOfDomains = List.copyOf(listsOfDomains);
        this.api = Objects.requireNonNull(api);
    }

    /**
     * Start testing.
     * 
     * @throws InternetnlAPIException
     * @throws IOException
     */
    public void Start() throws InternetnlAPIException, IOException
    {
        this.webResults.mkdirs();
        this.mailResults.mkdirs();

        for (String list : this.listsOfDomains)
        {
            testList(list, RequestType.WEB);
            testList(list, RequestType.MAIL);
        }
    }

    /**
     * Start testing.
     * 
     * @param type - The type of test to perform.
     * 
     * @throws InternetnlAPIException
     * @throws IOException
     */
    public void Start(RequestType type) throws InternetnlAPIException, IOException
    {
        if (type == RequestType.WEB)
            this.webResults.mkdirs();
        else
            this.mailResults.mkdirs();

        for (String list : this.listsOfDomains)
            testList(list, type);
    }

    /**
     * Test the domains in the specified list.
     * 
     * @param list - The name of the list
     * @param type - The type of test.
     * 
     * @throws InternetnlAPIException
     * @throws IOException
     */
    private void testList(String list, RequestType type) throws InternetnlAPIException, IOException
    {
        try
        {
            log.info(String.format("Starting %s test on list %s", type.getType(), list));
            startTest(list, type).dump();
            log.info(String.format("Finished %s test on list %s", type.getType(), list));
        } catch (Exception e) {
            log.severe(String.format("An error occurred during %s test on list %s.\n%s",
                type.getType(), list, e.getMessage()));
        }
    }

    /**
     * Start the test associated to the specified list and type.
     * 
     * @param list - The name of the list
     * @param type - The type of test.
     * 
     * @return A new test.
     * @throws InternetnlAPIException
     */
    private RunningTest startTest(String list, RequestType type) throws InternetnlAPIException
    {
        return new RunningTest(
            this.api.submit(this.domains, list, type).getRequest().getRequest_id(),
            createResultFile(list, type),
            this.api);
    }

    /**
     * Create the results file for the specified list and type.
     * 
     * @param list
     * @param type
     * @return The new File.
     */
    private File createResultFile(String list, RequestType type)
    {
        return new File(type == RequestType.WEB ? this.webResults : this.mailResults, list + ".json");
    }
}
