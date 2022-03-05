package observatory.tests.collection;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import observatory.internetnlAPI.config.results.TestResult;
import observatory.tests.ListTest;

/**
 * Represents a collection of results.
 */
public class ListTestCollection
{
    public final File resultsFolder;

    private final ObjectMapper mapper;

    /**
     * Initializes a new collection of results from the specified directory.
     * 
     * @param resultsFolder - The directory with the results.
     */
    public ListTestCollection(File resultsFolder) throws IOException
    {
        this.resultsFolder = Objects.requireNonNull(resultsFolder);
        this.resultsFolder.mkdirs();
        if (!resultsFolder.isDirectory())
            throw new IOException("Invalid results location.");
        
        this.mapper = new ObjectMapper();
    }

    /**
     * Get the results of a list.
     * 
     * @param list
     * @return The results of a list.
     * @throws InvalidResultsFileException
     */
    public ListTest getListResults(String list) throws InvalidResultsFileException
    {
        try
        {
            TestResult result = this.mapper.readValue(getListResultsFile(list), TestResult.class);
            return new ListTest(list, result);
        } catch (Exception e) {
            throw new InvalidResultsFileException(e);
        }
    }

    /**
     * Save the results of list.
     * 
     * @param list
     * @throws IOException
     */
    public void saveListResults(ListTest list) throws IOException
    {
        try
        {
            this.mapper.writeValue(getListResultsFile(list.getName()), list.getResults());
        } catch (IOException e)
        {
            throw e;
        }
    }

    /**
     * Checks if the results of the specified list are available.
     * @param listName
     * @return true if the results are available or false otherwise.
     */
    public boolean isListResultsAvailable(String listName)
    {
        return getListResultsFile(listName).isFile();
    }


    /**
     * Get the file associated with the specified list.
     * @param list
     * @return The file associated with the specified list.
     */
    private File getListResultsFile(String list)
    {
        return new File(this.resultsFolder, list + ".json");
    }
}
