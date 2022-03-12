package observatory.argsParser.options;

import java.util.List;

/**
 * Represents the value of an option.
 * 
 * @author Henrique Campos Ferreira
 */
public abstract class OptionValue
{
    protected final Object value;

    /**
     * @param value
     */
    public OptionValue(Object value) {
        this.value = value;
    }

    public String getSingle()
    {
        return (String)this.value;
    }

    @SuppressWarnings("unchecked")
    public List<String> getList()
    {
        return (List<String>)this.value;
    }
}
