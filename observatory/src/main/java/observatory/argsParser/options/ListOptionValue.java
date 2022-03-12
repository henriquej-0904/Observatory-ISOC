package observatory.argsParser.options;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Henrique Campos Ferreira
 */
public class ListOptionValue extends OptionValue
{
    /**
     * @param value
     */
    public ListOptionValue(List<String> value) {
        super(value);
    }

    /**
     * @param value
     */
    public ListOptionValue() {
        super(new LinkedList<>());
    }

    @SuppressWarnings("unchecked")
    void addValue(String value)
    {
        ((List<String>)this.value).add(value);
    }

    @SuppressWarnings("unchecked")
    public List<String> getValue() {
        return (List<String>)this.value;
    }
}
