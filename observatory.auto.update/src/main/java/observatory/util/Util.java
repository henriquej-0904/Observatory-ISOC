package observatory.util;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import observatory.internetnlAPI.config.RequestType;

public class Util
{
    /**
     * An Alphabetic order comparator.
     */
    public static final Comparator<String> ALPHABETIC_ORDER =
    (v1, v2) -> {
        int compare = v1.compareTo(v2);
        if (compare > 0)
            compare = -1;
        else if (compare < 0)
            compare = 1;

        return compare;
    };

    public static File getResultsFolder(File resultsFolder, RequestType type)
    {
        return new File(resultsFolder, type.getType());
    }

    public static List<ListTest> readResultsFromFolder(File resultsFolder, RequestType type) throws IOException
    {
        resultsFolder = getResultsFolder(resultsFolder, type);

        File[] results = resultsFolder.listFiles(
            (folder, name) -> name.endsWith(".json")
        );

        try
        {
            return Stream.of(results)
                .map((result) ->
                {
                    try
                    {
                        return new ListTest(result);
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                })
                .sorted((v1, v2) ->  String.CASE_INSENSITIVE_ORDER.compare(v1.getName(), v2.getName()))
                .collect(Collectors.toUnmodifiableList());
        } catch (IllegalArgumentException e) {
            throw (IOException)e.getCause();
        }
    }
}
