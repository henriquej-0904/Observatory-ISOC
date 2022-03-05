package observatory.tests;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import observatory.internetnlAPI.InternetnlAPI;
import observatory.internetnlAPI.InternetnlAPIException;
import observatory.internetnlAPI.TestIdNotFoundException;
import observatory.internetnlAPI.config.RequestType;
import observatory.internetnlAPI.config.TestInfo;
import observatory.internetnlAPI.config.results.TestResult;
import observatory.internetnlAPI.config.results.domain.DomainResults;
import observatory.tests.collection.ListTestCollection;
import observatory.tests.index.Index;
import observatory.util.Logging;
import observatory.util.Util;

/**
 * A class to test the domains in a set of lists.
 */
public class TestDomains implements Closeable
{
    public static final String DEFAULT_DOMAINS_WORKBOOK_FILE_NAME = "domains.xlsx";

    private final RequestType type;

    private final InternetnlAPI api;

    private final ListTestCollection listTestCollection;

    private final InputStream domainsInputStream;
    private final Workbook domains;

    private final SortedSet<String> listsToTest;

    private final Index index;

    private Logger logger;

    private Consumer<TestInfo> listSubmittedListener;
    private Consumer<ListTest> listFetchedResultsListener;

    public TestDomains(RequestType type, InternetnlAPI api, Index index, File resultsFolder)
        throws IOException, InvalidFormatException
    {
        this(type, api, index, resultsFolder,
            new File(Objects.requireNonNull(resultsFolder), DEFAULT_DOMAINS_WORKBOOK_FILE_NAME));
    }

    public TestDomains(RequestType type, InternetnlAPI api, Index index, File resultsFolder, File domainsWorkbookFile)
        throws IOException, InvalidFormatException
    {
        this.type = type;
        this.api = Objects.requireNonNull(api);
        this.index = Objects.requireNonNull(index);
        this.listTestCollection = new ListTestCollection(resultsFolder);

        this.domainsInputStream = new FileInputStream(domainsWorkbookFile);
        this.domains = Util.openWorkbook(this.domainsInputStream);

        if (this.domains.getNumberOfSheets() == 0)
            throw new IllegalArgumentException("There is no domains to test.");

        this.listsToTest = new TreeSet<>();

        for (Sheet sheet : this.domains)
            this.listsToTest.add(sheet.getSheetName().toUpperCase());
    }

    public TestDomains(RequestType type, InternetnlAPI api, Index index, File resultsFolder, Set<String> listsToTest)
        throws IOException, InvalidFormatException
    {
        this(type, api, index, resultsFolder,
            new File(Objects.requireNonNull(resultsFolder), DEFAULT_DOMAINS_WORKBOOK_FILE_NAME), listsToTest);
    }

    public TestDomains(RequestType type, InternetnlAPI api, Index index, File resultsFolder,
        File domainsWorkbookFile, Set<String> listsToTest)
        throws IOException, InvalidFormatException
    {
        this.type = type;
        this.api = Objects.requireNonNull(api);
        this.index = Objects.requireNonNull(index);
        this.listTestCollection = new ListTestCollection(resultsFolder);

        this.domainsInputStream = new FileInputStream(domainsWorkbookFile);
        this.domains = Util.openWorkbook(this.domainsInputStream);

        if (this.domains.getNumberOfSheets() == 0)
            throw new IllegalArgumentException("There is no domains to test.");

        this.listsToTest = checkListsToTest(Objects.requireNonNull(listsToTest), this.domains);
    }

    /**
     * Set a listener for submitted lists to test.
     * @param listSubmittedListener - The function to execute when a list is submitted to test.
     */
    public void setListSubmittedListener(Consumer<TestInfo> listSubmittedListener)
    {
        this.listSubmittedListener = listSubmittedListener;
    }

    /**
     * Set a listener for fetched results of a list.
     * @param listFetchedResultsListener - The function to execute when the results of a list are fetched.
     */
    public void setListFetchedResultsListener(Consumer<ListTest> listFetchedResultsListener)
    {
        this.listFetchedResultsListener = listFetchedResultsListener;
    }

    /**
     * Check the lists to test.
     * 
     * @param listsToTest
     * @param domains
     * @return An ordered set of lists.
     */
    private static SortedSet<String> checkListsToTest(Set<String> listsToTest, Workbook domains)
    {
        SortedSet<String> toReturn =
        listsToTest.stream()
        .map(String::toUpperCase)
        .collect(Collectors.toCollection(() -> new TreeSet<String>()));

        for (String list : toReturn) {
            if (domains.getSheet(list) == null)
                throw new IllegalArgumentException(
                    String.format("There is no list named %s in the specified domains file.", list)
                );
        }

        return toReturn;
    }

    /**
     * Start testing.
     * 
     * @throws InternetnlAPIException
     * @throws IOException
     */
    public void start() throws InternetnlAPIException, IOException
    {
        createLogger();
        this.logger.info("Starting tests...");
        
        for (String list : this.listsToTest)
            testList(list);

        this.logger.info("All tests completed successfully!");
    }

    /**
     * Test the domains in the specified list.
     * 
     * @param list - The name of the list
     * 
     * @throws InternetnlAPIException
     * @throws IOException
     */
    private void testList(String list)
        throws InternetnlAPIException, IOException
    {
        try
        {
            if (this.listTestCollection.isListResultsAvailable(list))
            {
                logger.info(String.format("Already tested list %s of type %s", list, this.type.getType()));
                return;
            }

            String[] domainsList = Util.getDomainsList(this.domains, list, this.type);

            if (!this.index.hasList(list))
            {
                RunningTest test = startTest(list, domainsList);
                waitAndSaveResults(test, list, domainsList);
            }
            else
            {
                //try to get the results of a previous test. If not, launch a new one.
                String testId = this.index.get(list);

                logger.info(String.format("Already started %s test on list %s with test id: %s",
                    this.type.getType(), list, testId));

                RunningTest test = new RunningTest(testId, api);
                try
                {
                    waitAndSaveResults(test, list, domainsList);
                } catch (TestIdNotFoundException e) {
                    logger.warning(e.getMessage());

                    // Previous test failed...
                    // Submit a new test and wait for the results.
                    test = startTest(list, domainsList);
                    waitAndSaveResults(test, list, domainsList);
                }
            }
        } catch (IOException | InternetnlAPIException e) {
            logger.severe(String.format("An error occurred during %s test on list %s.\n%s",
                type.getType(), list, e.getMessage()));
            throw e;
        }
    }

    /**
     * Start the test associated to the specified list and type.
     * 
     * @param list - The name of the list
     * @param domainsList - The list of domains to test.
     * 
     * @return A new test.
     * @throws InternetnlAPIException
     */
    private RunningTest startTest(String list, String[] domainsList)
        throws IOException, InternetnlAPIException
    {
        logger.info(String.format("Starting %s test on list %s", this.type.getType(), list));

        TestInfo testInfo = this.api.submit(list, domainsList, this.type);
        String testId = testInfo.getRequest().getRequest_id();
        logger.info(String.format("Started %s test on list %s with id %s", type.getType(), list, testId));

        // save id in case of an error.
        this.index.assocList(list, testId);

        if (this.listSubmittedListener != null)
            this.listSubmittedListener.accept(testInfo);

        return new RunningTest(testId, this.api);
    }

    /**
     * Waits and saves the results of a list test.
     * 
     * @param test - The test.
     * @param list - The name of the list.
     * @param domainsList - The list of domains.
     * @return The results of the test.
     * @throws IOException If an error occurred while saving the results.
     * @throws InternetnlAPIException
     */
    private ListTest waitAndSaveResults(RunningTest test, String list, String[] domainsList)
        throws IOException, InternetnlAPIException
    {
        logger.info(String.format("Waiting for %s test on list %s", this.type.getType(), list));
        TestResult result = orderDomains(test.waitFor(), domainsList);
        logger.info(String.format("Finished %s test on list %s and got results.", this.type.getType(), list));

        ListTest listResults = new ListTest(list, result);
        listTestCollection.saveListResults(listResults);
        logger.info(String.format("Successfully saved results of %s test on list %s.", this.type.getType(), list));

        if (this.listFetchedResultsListener != null)
            this.listFetchedResultsListener.accept(listResults);
        
        return listResults;
    }

    /**
     * Order the domains by the specified domains List.
     * 
     * @param result - The result that contains the domains results.
     * @param domainsList - Order the domains by the specified list.
     * 
     * @return The result.
     */
    private TestResult orderDomains(TestResult result, String[] domainsList)
    {
        LinkedHashMap<String, DomainResults> unorderedDomains = result.getDomains();
        LinkedHashMap<String, DomainResults> orderedDomains = new LinkedHashMap<>(result.getDomains().size());
        
        for (String domain : domainsList)
            orderedDomains.put(domain, unorderedDomains.get(domain));

        result.setDomains(orderedDomains);

        return result;
    }

    private void createLogger() throws IOException
    {
        this.logger = Logging.configLogger(Logger.getLogger(TestDomains.class.getCanonicalName()),
            Logging.createLogFileName(this.listTestCollection.resultsFolder, "test"), true);
    }

    @Override
    public void close() throws IOException
    {
        this.domainsInputStream.close();
        this.domains.close();
    }    
}
