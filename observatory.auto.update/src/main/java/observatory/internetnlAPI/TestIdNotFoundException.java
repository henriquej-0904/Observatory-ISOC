package observatory.internetnlAPI;

/**
 * Thrown when there is no test with the specified ID.
 */
public class TestIdNotFoundException extends InternetnlAPIException
{
    private static final String DEFAULT_MSG_FORMAT = "There is no test with the specified id: %s";


    /**
     * @param id - The id of the invalid test.
     */
    public TestIdNotFoundException(String id)
    {
        super(String.format(DEFAULT_MSG_FORMAT, id));
    }

    /**
     * @param cause
     */
    public TestIdNotFoundException(String id, Throwable cause) {
        super(String.format(DEFAULT_MSG_FORMAT, id), cause);
    }
}
