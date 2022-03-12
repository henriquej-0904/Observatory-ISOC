package observatory.tests.index;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import observatory.util.InvalidFormatException;

/**
 * Represents an index that maps a list name to a test id.
 * 
 * @author Henrique Campos Ferreira
 */
public class Index implements Map<String, String>
{
    public static final String DEFAULT_FILE_NAME = "index.conf";

    private final Map<String, String> index;   
    
    private final File indexFile;

    private final ObjectMapper mapper;

    /**
     * 
     * @param index
     */
    private Index(Map<String, String> index, File indexFile, ObjectMapper mapper) {
        this.index = index;
        this.indexFile = indexFile;
        this.mapper = mapper;
    }

    /**
     * Constructs an index from the contents of the specified file.
     * @param indexFile
     * @return The index.
     * @throws InvalidFormatException if an error occurred while creating the index.
     */
    public static Index fromFile(File indexFile) throws IOException, InvalidFormatException
    {
        TypeFactory typeFactory = TypeFactory.defaultInstance();
        MapType type = typeFactory.constructMapType(HashMap.class, String.class, String.class);

        ObjectMapper mapper = new ObjectMapper();
        
        try
        {
            Map<String, String> index = mapper.readValue(Objects.requireNonNull(indexFile), type);
            return new Index(index, indexFile, mapper);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidFormatException(e);
        }
    }

    /**
     * Creates a new empty Index.
     * 
     * @param indexFile - The file to save the index contents when saved.
     * @return A new empty Index.
     */
    public static Index empty(File indexFile)
    {
        return new Index(new HashMap<>(), indexFile, new ObjectMapper());
    }

    /**
     * Checks if the specified list exists in the Index.
     * @param list - The name of the list.
     * @return true if it exists or false otherwise.
     */
    public boolean hasList(String list)
    {
        return this.index.containsKey(list);
    }

    /**
     * Associates the specified list to the test id.
     * @param list - The name of the list.
     * @param testId - The test id.
     * @return The previous test id associated with this list or null
     * if none.
     */
    public String assocList(String list, String testId)
    {
        return this.index.put(list, testId);
    }

    /**
     * Save the index.
     * @throws IOException
     */
    public void save() throws IOException
    {
        try
        {
            mapper.writeValue(this.indexFile, this.index);
        } catch (IOException e) {
            throw e;
        }
    }


    @Override
    public int size() {
        return this.index.size();
    }


    @Override
    public boolean isEmpty() {
        return this.index.isEmpty();
    }


    @Override
    public boolean containsKey(Object key) {
        return this.index.containsKey(key);
    }


    @Override
    public boolean containsValue(Object value) {
        return this.index.containsValue(value);
    }


    @Override
    public String get(Object key) {
        return this.index.get(key);
    }


    @Override
    public String put(String key, String value) {
        return this.index.put(key, value);
    }


    @Override
    public String remove(Object key) {
        return this.index.remove(key);
    }


    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        this.index.putAll(m);
    }


    @Override
    public void clear() {
        this.index.clear();
    }


    @Override
    public Set<String> keySet() {
        return this.index.keySet();
    }


    @Override
    public Collection<String> values() {
        return this.index.values();
    }


    @Override
    public Set<Entry<String, String>> entrySet() {
        return this.index.entrySet();
    }    
}
