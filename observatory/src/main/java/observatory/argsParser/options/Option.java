package observatory.argsParser.options;

/**
 * Represents an option.
 * 
 * @author Henrique Campos Ferreira
 */
public class Option
{
    public final String name;

    public final OptionType type;

    /**
     * @param name
     * @param type
     */
    public Option(String name, OptionType type) {
        this.name = name;
        this.type = type;
    }

    

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }



    /**
     * @return the type
     */
    public OptionType getType() {
        return type;
    }



    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
            return true;

        if (!(obj instanceof Option))
            return false;

        Option otherOption = (Option)obj;
        return this.name.equals(otherOption.name) &&
            this.type == otherOption.type;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() + this.type.hashCode();
    }
}
