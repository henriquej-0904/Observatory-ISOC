package observatory.argsParser.options;

/**
 * The type of option.
 * 
 * @author Henrique Campos Ferreira
 */
public enum OptionType {
    /**
     * An option of this type means that cannot have more than one value.
     */
    SINGLE,

    /**
     * An option of this type means that can have multiple values (a list of
     * values).
     */
    LIST
}