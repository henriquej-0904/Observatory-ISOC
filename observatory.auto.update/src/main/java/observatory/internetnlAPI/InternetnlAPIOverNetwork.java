package observatory.internetnlAPI;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import observatory.internetnlAPI.config.RequestType;
import observatory.internetnlAPI.config.TestInfo;
import observatory.internetnlAPI.config.results.TestResult;

public class InternetnlAPIOverNetwork implements InternetnlAPI
{
    private static final int MAX_TRIES = 3;
    private static final int TIMEOUT_MILLIS = 15 * 1000;

    private static final int CONNECT_TIMEOUT = 5;
    private static final int READ_TIMEOUT = 10;

    private Client client;
    private URI endpoint;
    
    private Pair<String, String> authHeader;

    private ObjectMapper mapper;


    /**
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

        this.mapper = new ObjectMapper();
    }

    @Override
    public TestInfo submit(File domains, String sheetName, RequestType requestType) throws InternetnlAPIException
    {
        SubmitRequestInfo info = getRequestInfo(domains, sheetName, requestType);

        return request(this.client.target(this.endpoint)
            .request().accept(MediaType.APPLICATION_JSON)
            .header(this.authHeader.getLeft(), this.authHeader.getRight())
            .buildPost(Entity.json(info)), TestInfo.class);
    }

    @Override
    public TestInfo status(String requestId) throws InternetnlAPIException
    {
        return request(this.client.target(this.endpoint).path(requestId)
            .request().accept(MediaType.APPLICATION_JSON)
            .header(this.authHeader.getLeft(), this.authHeader.getRight())
            .buildGet(), TestInfo.class);
    }

    @Override
    public TestResult get(String requestId) throws InternetnlAPIException {
        return request(this.client.target(this.endpoint).path(requestId)
            .path("results")
            .request().accept(MediaType.APPLICATION_JSON)
            .header(this.authHeader.getLeft(), this.authHeader.getRight())
            .buildGet(), TestResult.class);
    }

    private SubmitRequestInfo getRequestInfo(File domains, String sheetName, RequestType requestType)
        throws InternetnlAPIException
    {
        try
        (
            Workbook domainsExcel = new XSSFWorkbook(domains);
        )
        {
            Sheet sheet = domainsExcel.getSheet(sheetName);

            if (sheet == null)
                throw new IllegalArgumentException(
                    String.format("There is no list named %s in the specified domains file.", sheetName)
                );

            Iterator<Row> rows = sheet.iterator();

            if (!rows.hasNext())
                throw new IllegalArgumentException(
                    String.format("Invalid format for domains file. There is no column named " + requestType.getType(), sheetName)
                );

            Row row = rows.next();
            int column = -1;
            // find column
            for (Cell cell : row) {

                String value = null;
                try {
                    value = cell.getStringCellValue();
                } catch (Exception e) {}

                if (value.equals(requestType.getType()))
                    column = cell.getColumnIndex();
            }

            if (column == -1)
                throw new IllegalArgumentException(
                    String.format("Invalid format for domains file. There is no column named " + requestType.getType(), sheetName)
                );

            List<String> domainsList = new LinkedList<>();
            while (rows.hasNext()) {
                row = rows.next();
                Cell cell = row.getCell(column);
                String domain = null;
                if (cell != null && cell.getCellType() == CellType.STRING
                    && !(domain = cell.getStringCellValue()).isEmpty())
                    domainsList.add(domain);
            }
            
            if (domainsList.isEmpty())
                throw new IllegalArgumentException(
                    String.format("Invalid format for domains file. There are no domains to test. " + requestType.getType(), sheetName)
                );

            return new SubmitRequestInfo(sheetName, requestType.getType(), domainsList.toArray(new String[0]));
        }
        catch (Exception e) {
            throw new InternetnlAPIException(e);
        }
    }

    private <T> T request(Invocation invocation, Class<T> responseType) throws InternetnlAPIException
    {
        int numberTries = MAX_TRIES;
        InternetnlAPIException error = null;

        while (numberTries > 0)
        {
            try (Response response = invocation.invoke();)
            {
                if (response.getStatusInfo().toEnum() != Status.OK)
                    throw new InternetnlAPIException(
                        String.format("An error occurred calling the API: The server replied with code: %d - %s",
                        response.getStatus(), response.getStatusInfo().getReasonPhrase()
                        ));

                return this.mapper.readValue(response.readEntity(InputStream.class), responseType);
            }
            catch (InternetnlAPIException e) {
                error = e;
            }
            catch (Exception e) {
                error = new InternetnlAPIException("An error occurred calling the API.");
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
