package observatory.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JSONconfig
{
    /**
     * Get a configured JSON mapper object.
     * @return Configured JSON mapper object.
     */
    public static JsonMapper getJSONmapper()
    {
        return JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    }
}
