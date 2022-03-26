package imconfig.internal.readers;

import com.fasterxml.jackson.databind.ObjectMapper;
import imconfig.internal.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import imconfig.ConfigException;
import java.util.stream.Collectors;

public abstract class JacksonReader implements ExternalReader {

    private final ObjectMapper mapper;


    protected JacksonReader(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public Map<String, ?> readFlat(URL url, Charset charset) {
        Map<String,?> map = readObject(url,charset);
        Map<String,Object> result = new LinkedHashMap<>();
        fill("", map, result);
        return result;
    }


    @SuppressWarnings("unchecked")
    private void fill(String prefix, Map<String,?> object, Map<String,Object> result) {
        object.forEach((key,value)->{
            if (value instanceof List) {
                result.put(prefix+key, ((List<?>)value).stream().map(String::valueOf).collect(Collectors.toList()) );
            } else if (value instanceof Map) {
                fill(prefix+key+".", (Map<String, ?>)value, result);
            } else {
                result.put(prefix+key, String.valueOf(value));
            }
        });
    }



    @Override
    @SuppressWarnings("unchecked")
    public Map<String, ?> readObject(URL url, Charset charset) {
        try (Reader reader = reader(url,charset)) {
            return mapper.readValue(reader, HashMap.class);
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }


}
