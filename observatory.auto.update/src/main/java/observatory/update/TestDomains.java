package observatory.update;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import observatory.internetnlAPI.InternetnlAPI;
import observatory.internetnlAPI.InternetnlAPIException;
import observatory.internetnlAPI.config.RequestType;

/**
 * A class to test the domains in a set of lists.
 */
public class TestDomains
{
    private static Logger log = Logger.getLogger(TestDomains.class.getSimpleName());

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

        try
        (
            XSSFWorkbook domainsExcel = new XSSFWorkbook(domains);
        )
        {
            if (domainsExcel.getNumberOfSheets() == 0)
            throw new IllegalArgumentException("There is no domains to test.");

            this.listsOfDomains = new ArrayList<>(domainsExcel.getNumberOfSheets());

            for (Sheet sheet : domainsExcel)
                this.listsOfDomains.add(sheet.getSheetName());

        } catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

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

        try
        (
            XSSFWorkbook domainsExcel = new XSSFWorkbook(domains);
        )
        {
            if (domainsExcel.getNumberOfSheets() == 0)
                throw new IllegalArgumentException("There is no domains to test.");

            for (String list : Objects.requireNonNull(listsOfDomains)) {
                if (domainsExcel.getSheet(list) == null)
                    throw new IllegalArgumentException(
                        String.format("There is no list named %s in the specified domains file.", list)
                    );
            }

        } catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        this.listsOfDomains = List.copyOf(listsOfDomains);
        this.api = Objects.requireNonNull(api);
    }

    /**
     * Start testing.
     * 
     * @param overwrite - True to test the list if it was already tested.
     * 
     * @throws InternetnlAPIException
     * @throws IOException
     */
    public void Start(boolean overwrite) throws InternetnlAPIException, IOException
    {
        this.webResults.mkdirs();
        this.mailResults.mkdirs();

        for (String list : this.listsOfDomains)
        {
            testList(list, RequestType.WEB, overwrite);
            testList(list, RequestType.MAIL, overwrite);
        }
    }

    /**
     * Start testing.
     * 
     * @param type - The type of test to perform.
     * @param overwrite - True to test the list if it was already tested.
     * 
     * @throws InternetnlAPIException
     * @throws IOException
     */
    public void Start(RequestType type, boolean overwrite) throws InternetnlAPIException, IOException
    {
        if (type == RequestType.WEB)
            this.webResults.mkdirs();
        else
            this.mailResults.mkdirs();

        for (String list : this.listsOfDomains)
            testList(list, type, overwrite);
    }

    /**
     * Test the domains in the specified list.
     * 
     * @param list - The name of the list
     * @param type - The type of test.
     * @param overwrite - True to test the list if it was already tested.
     * 
     * @throws InternetnlAPIException
     * @throws IOException
     */
    private void testList(String list, RequestType type, boolean overwrite)
        throws InternetnlAPIException, IOException
    {
        try
        {
            File results = createResultFile(list, type);

            if (overwrite || !results.exists())
            {
                log.info(String.format("Starting %s test on list %s", type.getType(), list));
                startTest(list, type, results).dump();
                log.info(String.format("Finished %s test on list %s", type.getType(), list));
            }
            else
                log.info(String.format("Already tested list %s of type %s", list, type.getType()));
        } catch (Exception e) {
            log.severe(String.format("An error occurred during %s test on list %s.",
                type.getType(), list));
            throw e;
        }
    }

    /**
     * Start the test associated to the specified list and type.
     * 
     * @param list - The name of the list
     * @param type - The type of test.
     * @param results - The file to store the results of the test.
     * 
     * @return A new test.
     * @throws InternetnlAPIException
     */
    private RunningTest startTest(String list, RequestType type, File results) throws InternetnlAPIException
    {
        return new RunningTest(
            this.api.submit(this.domains, list, type).getRequest().getRequest_id(),
            results,
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
