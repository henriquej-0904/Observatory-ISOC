package observatory.internetnlAPI;

import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.json.JsonMapper;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import observatory.internetnlAPI.config.RequestType;
import observatory.internetnlAPI.config.TestInfo;
import observatory.internetnlAPI.config.testResult.TestResult;
import observatory.util.JSONconfig;
import observatory.util.restResult.Result;

/**
 * An implementation of InternetnlAPI that communicates via HTTP.
 * 
 * @author Henrique Campos Ferreira
 */
public class InternetnlAPIOverNetwork implements InternetnlAPI
{
    private static final int MAX_TRIES = 3;
    private static final int TIMEOUT_MILLIS = 15 * 1000;

    private static final int CONNECT_TIMEOUT = 60;
    private static final int READ_TIMEOUT = 60 * 3;

    private Client client;
    private URI endpoint;
    
    private Pair<String, String> authHeader;

    private JsonMapper mapper;


    /**
     * Creates a new HTTP client to communicate with the Internetnl API.
     * 
     * @param endpoint
     * @param username
     * @param password
     */
    public InternetnlAPIOverNetwork(URI endpoint, String username, String password)
    {
        ClientBuilder builder = ClientBuilder.newBuilder().connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        this.client = builder.build();

        this.endpoint = endpoint;
        this.authHeader = new ImmutablePair<>("Authorization",
            "Basic " +
            Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));

        this.mapper = JSONconfig.getJSONmapper();
    }

    @Override
    public TestInfo submit(String name, String[] domains, RequestType type) throws InternetnlAPIException
    {
        SubmitRequestInfo info = new SubmitRequestInfo(name, type.getType(), domains);

        return checkResult(request(this.client.target(this.endpoint)
            .request().accept(MediaType.APPLICATION_JSON)
            .header(this.authHeader.getLeft(), this.authHeader.getRight())
            .buildPost(Entity.json(info)), TestInfo.class));
    }

    @Override
    public TestInfo status(String requestId) throws TestIdNotFoundException, InternetnlAPIException
    {
        return checkResult(
            request(this.client.target(this.endpoint).path(requestId)
            .request().accept(MediaType.APPLICATION_JSON)
            .header(this.authHeader.getLeft(), this.authHeader.getRight())
            .buildGet(), TestInfo.class),

            requestId);
    }

    @Override
    public TestResult get(String requestId) throws TestIdNotFoundException, InternetnlAPIException {
        return checkResult(
            request(this.client.target(this.endpoint).path(requestId)
            .path("results")
            .request().accept(MediaType.APPLICATION_JSON)
            .header(this.authHeader.getLeft(), this.authHeader.getRight())
            .buildGet(), TestResult.class),

            requestId);
    }

    /**
     * Check the result for HTTP errors.
     * @param <T> - The type of the response.
     * @param result - The result wrapper of the specified type.
     * @return The result.
     * @throws InternetnlAPIException If an error occurrs.
     */
    private <T> T checkResult(Result<T> result) throws InternetnlAPIException
    {
        if (result.isOK())
            return result.value();
        
        throw statusCodeError(result.error());
    }

    /**
     * Check the result for HTTP errors.
     * @param <T> - The type of the response.
     * @param result - The result wrapper of the specified type.
     * @param requestId - The request id of the submitted test.
     * @return The result.
     * @throws TestIdNotFoundException If the specified request id does not exist.
     * @throws InternetnlAPIException If an error occurrs.
     */
    private <T> T checkResult(Result<T> result, String requestId)
        throws TestIdNotFoundException, InternetnlAPIException
    {
        if (result.isOK())
            return result.value();

        if (result.error() == Status.NOT_FOUND)
            throw new TestIdNotFoundException(requestId);
        
        throw statusCodeError(result.error());
    }

    /**
     * Send a HTTP request.
     * @param <T> - The type of the response.
     * @param invocation
     * @param responseType - The class object of the response type.
     * @return A result of the specified type.
     * @throws InternetnlAPIException If an error occurrs.
     */
    private <T> Result<T> request(Invocation invocation, Class<T> responseType) throws InternetnlAPIException
    {
        int numberTries = MAX_TRIES;
        InternetnlAPIException error = null;

        while (numberTries > 0)
        {
            try (Response response = invocation.invoke();)
            {
                Result<T> result = parseResponse(response, responseType);

                if (result.error() != Status.BAD_REQUEST)
                    return result;

                /**
                 * sometimes the server responds with 400.
                 * Try again...
                **/
                error = statusCodeError(result.error());
            }
            catch (InternetnlAPIException e) {
                error = e;
            }
            catch (Exception e) {
                error = new InternetnlAPIException("An error occurred calling the API:\n" + e.getMessage(), e);
            }

            if (error != null)
            {
                numberTries--;

                try {
                    Thread.sleep(TIMEOUT_MILLIS);
                } catch (Exception e) {}
            }
        }

        throw error;
    }

    /**
     * Parse the response to the specified type.
     * @param <T> - The type of the response.
     * @param response
     * @param responseType - The class object of the response type.
     * @return A result of the specified type.
     * @throws InternetnlAPIException If an error occurrs.
     */
    private <T> Result<T> parseResponse(Response response, Class<T> responseType) throws InternetnlAPIException
    {
        Status status = response.getStatusInfo().toEnum();
        if (status == Status.OK)
        {
            try
            {
                InputStream input = response.readEntity(InputStream.class);
                T value = this.mapper.readValue(input, responseType);
                return Result.ok(value);
            } catch (Exception e)
            {
                throw new InternetnlAPIException("An error occurred calling the API:\n" + e.getMessage(), e);
            }
        }
        else if (status == Status.NO_CONTENT)
            return Result.ok();
        else
            return Result.error(status);
    }

    private InternetnlAPIException statusCodeError(Status status)
    {
        return new InternetnlAPIException(
            String.format("An error occurred calling the API: The server replied with code: %d - %s",
            status.getStatusCode(), status.getReasonPhrase()
            ));
    }

    @Override
    public void close()
    {
        this.client.close();
    }


    public static class SubmitRequestInfo
    {
        private String name;

        private String type;

        private String[] domains;

        /**
         * 
         */
        public SubmitRequestInfo() {
        }

        /**
         * @param name
         * @param type
         * @param domains
         */
        public SubmitRequestInfo(String name, String type, String[] domains) {
            this.name = name;
            this.type = type;
            this.domains = domains;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return the domains
         */
        public String[] getDomains() {
            return domains;
        }

        /**
         * @param domains the domains to set
         */
        public void setDomains(String[] domains) {
            this.domains = domains;
        }
    }
}
