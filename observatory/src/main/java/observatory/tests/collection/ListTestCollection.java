package observatory.tests.collection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;

import observatory.internetnlAPI.config.RequestType;
import observatory.tests.ListTest;
import observatory.util.Util;

/**
 * Represents a collection of results.
 */
public class ListTestCollection
{
    private File resultsFolder;

    private Index index;

    /**
     * Initializes a new collection of results from the specified directory and type.
     * 
     * @param resultsFolder - The directory with the results.
     * @param type - The type of results.
     */
    public ListTestCollection(File resultsFolder, RequestType type)
    {
        this.resultsFolder = Util.getResultsFolder(Objects.requireNonNull(resultsFolder), type);
        this.resultsFolder.mkdirs();
        if (!resultsFolder.isDirectory())
            throw new IllegalArgumentException("Invalid results location.");
        
        this.index = getIndex(this.resultsFolder);
    }

    /**
     * Save the test id of the specified list.
     * 
     * @param list - The name of the list associated with the testId.
     * @param testId - The id of the test.
     * @throws IOException
     */
    public void saveTestId(String list, String testId) throws IOException
    {
        ListInfo info = this.index.getIndex().get(list);
        if (info == null)
        {
            info = new ListInfo();
            this.index.getIndex().put(list, info);
        }

        info.setTestId(testId);
        saveIndex();
    }

    /**
     * Get info about a list.
     * @param list
     * @return List info.
     */
    public ListInfo getListInfo(String list)
    {
        ListInfo info = this.index.getIndex().get(list);
        if (info != null)
            info = new ListInfo(info);

        return info;
    }

    /**
     * Get the results of a list.
     * 
     * @param list
     * @return The results of a list.
     * @throws IOException
     */
    public ListTest getListResults(String list) throws IOException
    {
        ListInfo info = getListInfo(list);
        if (info == null || !info.getOk())
            return null;

        try
        {
            return new ObjectMapper().readValue(getListResultsFile(list), ListTest.class);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
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
        this.index.getIndex().put(list.getName(),
            new ListInfo(list.getResults().getRequest().getRequest_id(), true));
        
        try
        {
            new ObjectMapper().writeValue(getListResultsFile(list.getName()), list);
            saveIndex();
        } catch (IOException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Get the results of all lists.
     * @return A collection of results.
     * @throws IOException
     */
    public Map<String, ListTest> getResults() throws IOException
    {
        return getResults(new HashMap<>());
    }

    /**
     * Get the results of all lists ordered by the name of the list.
     * @return An ordered collection of results.
     * @throws IOException
     */
    public SortedMap<String, ListTest> getSortedResults() throws IOException
    {
        return (SortedMap<String, ListTest>) getResults(new TreeMap<>());
    }

    /**
     * Get the results of all lists.
     * 
     * @param result - The collection to put the results.
     * 
     * @return The same collection passed in the function arg.
     * @throws IOException
     */
    private Map<String, ListTest> getResults(Map<String, ListTest> result) throws IOException
    {
        for (Entry<String, ListInfo> entry : this.index.getIndex().entrySet())
        {
            if (entry.getValue().getOk())
                result.put(entry.getKey(), getListResults(entry.getKey()));
        }

        return result;
    }

    /**
     * Get the index info.
     * 
     * @param resultsFolder - The directory of results.
     * @return The index info.
     */
    private static Index getIndex(File resultsFolder)
    {
        File indexFile = new File(resultsFolder, "index.json");
        try
        {
            return new ObjectMapper().readValue(indexFile, Index.class);
        } catch (Exception e) {
            return new Index(new HashMap<>());
        }
    }

    /**
     * Save the index info.
     * @throws IOException
     */
    private void saveIndex() throws IOException
    {
        File indexFile = new File(this.resultsFolder, "index.json");
        try
        {
            new ObjectMapper().writeValue(indexFile, this.index);
        } catch (IOException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
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
