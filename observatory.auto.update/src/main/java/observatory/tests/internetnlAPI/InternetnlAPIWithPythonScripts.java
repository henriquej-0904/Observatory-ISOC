package observatory.tests.internetnlAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import observatory.tests.internetnlAPI.config.RequestType;
import observatory.tests.internetnlAPI.config.TestInfo;
import observatory.tests.internetnlAPI.config.results.TestResult;

/**
 * Represents an interaction with the Internet.nl API using python scripts.
 */
public class InternetnlAPIWithPythonScripts implements InternetnlAPI
{
    
    private static final int MAX_TRIES = 3;

    private static final int SUCCESS_EXIT_STATUS = 0;

    private final File batchAPIScript, workdir;

    private final List<String> parameters;

    private final ObjectMapper mapper;

    /**
     * Creates a new instance with the specified batch API script.
     * @param batchAPIScript
     */
    public InternetnlAPIWithPythonScripts(File batchAPIScript)
    {
        Objects.requireNonNull(batchAPIScript);

        this.batchAPIScript = batchAPIScript.getAbsoluteFile();
        this.workdir = this.batchAPIScript.getParentFile();

        this.parameters = List.of("python3", this.batchAPIScript.getName());

        this.mapper = new ObjectMapper();
    }

    @Override
    public TestInfo submit(File domains, String sheetName, RequestType requestType) throws InternetnlAPIException
    {
        Objects.requireNonNull(domains);
        Objects.requireNonNull(sheetName);
        Objects.requireNonNull(requestType);

        List<String> parameters = new ArrayList<>(this.parameters.size() + 6);
        parameters.addAll(this.parameters);
        parameters.add("-d");
        parameters.add(domains.getAbsolutePath());

        parameters.add("-s");
        parameters.add(sheetName);

        parameters.add("sub");
        parameters.add(requestType.getType());

        return callBatchAPI(createBuilder(parameters), TestInfo.class);
    }

    @Override
    public TestInfo status(String requestId) throws InternetnlAPIException
    {
        Objects.requireNonNull(requestId);

        List<String> parameters = new ArrayList<>(this.parameters.size() + 2);
        parameters.addAll(this.parameters);
        parameters.add("stat");
        parameters.add(requestId);

        return callBatchAPI(createBuilder(parameters), TestInfo.class);
    }

    @Override
    public TestResult get(String requestId) throws InternetnlAPIException
    {
        Objects.requireNonNull(requestId);

        List<String> parameters = new ArrayList<>(this.parameters.size() + 2);
        parameters.addAll(this.parameters);
        parameters.add("get");
        parameters.add(requestId);

        return callBatchAPI(createBuilder(parameters), TestResult.class);
    }

    private ProcessBuilder createBuilder(List<String> parameters)
    {
        return new ProcessBuilder()
                    .command(parameters)
                    .directory(this.workdir);
    }

    /**
     * Calls the api with the specified process builder and value type.
     * 
     * @param <T> - The type of the result.
     * @param builder - The builder to create the process to call the API.
     * @param valueType - The type of the result (Class object).
     * @return The result of the operation.
     * @throws InternetnlAPIException If an error occurred while calling the API.
     */
    private <T> T callBatchAPI(ProcessBuilder builder, Class<T> valueType) throws InternetnlAPIException
    {
        int numberTries = MAX_TRIES;
        InternetnlAPIException error = null;

        while (numberTries > 0)
        {
            try
            {
                Process process = builder.start();
                String resultString = null;

                try
                (
                    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                )
                {
                    resultString = input.lines().collect(Collectors.joining());
                }

                int exitStatus = process.waitFor();
                if (exitStatus != SUCCESS_EXIT_STATUS)
                    error = new InternetnlAPIException("An error occurred calling the API.\n" + resultString);
                else
                {
                    return this.mapper.readValue(resultString, valueType);
                }
            }
            catch (Exception e) {
                error = new InternetnlAPIException(e);
            }

            if (error != null)
                numberTries--;
        }

        throw error;
    }

    @Override
    public void close() {}
}
