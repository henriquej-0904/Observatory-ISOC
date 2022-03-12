package observatory.argsParser.options;

/**
 * @author Henrique Campos Ferreira
 */
public class SingleOptionValue extends OptionValue
{
    /**
     * @param value
     */
    public SingleOptionValue(String value) {
        super(value);
    }

    public String getValue() {
        return (String)this.value;
    }
}
