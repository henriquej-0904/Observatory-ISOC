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

    public ListTestCollection(File resultsFolder, RequestType type)
    {
        this.resultsFolder = Util.getResultsFolder(Objects.requireNonNull(resultsFolder), type);
        this.resultsFolder.mkdirs();
        if (!resultsFolder.isDirectory())
            throw new IllegalArgumentException("Invalid results location.");
        
        this.index = getIndex(this.resultsFolder);
    }

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

    public ListInfo getListInfo(String list)
    {
        ListInfo info = this.index.getIndex().get(list);
        if (info != null)
            info = new ListInfo(info);

        return info;
    }

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

    public Map<String, ListTest> getResults() throws IOException
    {
        return getResults(new HashMap<>());
    }

    public SortedMap<String, ListTest> getSortedResults() throws IOException
    {
        return (SortedMap<String, ListTest>) getResults(new TreeMap<>());
    }

    private Map<String, ListTest> getResults(Map<String, ListTest> result) throws IOException
    {
        for (Entry<String, ListInfo> entry : this.index.getIndex().entrySet())
        {
            if (entry.getValue().getOk())
                result.put(entry.getKey(), getListResults(entry.getKey()));
        }

        return result;
    }

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

    private File getListResultsFile(String list)
    {
        return new File(this.resultsFolder, list + ".json");
    }
}
