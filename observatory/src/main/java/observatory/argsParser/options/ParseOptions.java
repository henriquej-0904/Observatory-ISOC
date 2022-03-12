package observatory.argsParser.options;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import observatory.argsParser.ParserException;

/**
 * A class to parse options from a list of arguments.
 */
public class ParseOptions
{
    private final Map<String, Option> options;

    /**
     * Init the parser from the specified set of available options.
     * 
     * @param options - A set of options.
     */
    public ParseOptions(Set<Option> options) {
        this.options = options.stream()
            .collect(Collectors.toUnmodifiableMap(Option::getName, (option) -> option));
    }

    /**
     * Parse a list of arguments and return a map for the parsed options.
     * This operation removes all the arguments parsed from the args list.
     * 
     * @param args - A list of arguments to parse.
     * @return A map of (option; value).
     * @throws ParserException
     */
    public Map<Option, OptionValue> parse(List<String> args) throws ParserException {
        Map<Option, OptionValue> result = new HashMap<>(this.options.size());

        boolean end = false;
        Iterator<String> argsIt = args.iterator();

        while (!end && argsIt.hasNext()) {
            Option option;
            if ((option = this.options.get(argsIt.next())) == null) {
                end = true;
                continue;
            } else
                argsIt.remove();

            if (!argsIt.hasNext())
                throw new ParserException("Missing argument for option: " + option.getName());

            String value = argsIt.next();
            argsIt.remove();

            switch (option.getType()) {
                case SINGLE:
                    parseSingleValue(option, value, result);
                    break;
            
                case LIST:
                    parseListValue(option, value, result);
                    break;
            }
        }

        return result;
    }

    private OptionValue parseSingleValue(Option option, String value, Map<Option, OptionValue> result)
        throws ParserException
    {
        OptionValue optionValue = result.get(option);
        if (optionValue != null)
            throw new ParserException(
                String.format("The option %s cannot be repeated.", option.getName()));

        optionValue = new SingleOptionValue(value);
        result.put(option, optionValue);
        return optionValue;
    }

    private OptionValue parseListValue(Option option, String value, Map<Option, OptionValue> result)
        throws ParserException
    {
        ListOptionValue optionValue = (ListOptionValue)result.computeIfAbsent(option, (op) -> new ListOptionValue());
        optionValue.addValue(value);
        return optionValue;
    }
}
