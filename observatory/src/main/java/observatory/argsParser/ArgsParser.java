package observatory.argsParser;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import observatory.argsParser.options.Option;
import observatory.argsParser.options.OptionValue;
import observatory.internetnlAPI.config.RequestType;

/**
 * @author Henrique Campos Ferreira
 */
class ArgsParser
{
    /**
     * Parse a type.
     * @param type
     * @return The type.
     * @throws ParserException if an illegal type is specified.
     */
    public static RequestType parseType(String type) throws ParserException
    {
        try {
            return RequestType.parseType(type);
        } catch (IllegalArgumentException e) {
            throw new ParserException(e.getMessage(), e);
        }
    }

    public static <T> T getOption(Map<Option, OptionValue> options, Option option,
        Function<OptionValue, T> parseValueFunc, Supplier<T> defaultValueFunc)
    {
        OptionValue optionValue = options.get(option);

        if (optionValue != null)
            return parseValueFunc.apply(optionValue);

        return defaultValueFunc.get();
    }

    public static <T> T getOption(Map<Option, OptionValue> options, Option option,
        ParseValueFunction<T> parseValueFunc, Supplier<T> defaultValueFunc) throws ParserException
    {
        OptionValue optionValue = options.get(option);

        if (optionValue != null)
            return parseValueFunc.parse(optionValue);

        return defaultValueFunc.get();
    }

    /**
     * A function to parse an option value.
     */
    static interface ParseValueFunction<T>
    {
        /**
         * Parse a value.
         * 
         * @param value - The value to parse.
         * @return The parsed result.
         * @throws ParserException if an error occurred while parsing the specified value.
         */
        T parse(OptionValue value) throws ParserException;
    }
}
