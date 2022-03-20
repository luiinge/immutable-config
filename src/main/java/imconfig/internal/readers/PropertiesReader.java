package imconfig.internal.readers;

import imconfig.ConfigException;
import imconfig.internal.ExternalReader;
import java.io.*;
import java.net.URL;
import java.nio.charset.*;
import java.util.*;

public class PropertiesReader implements ExternalReader {


    @Override
    public Map<String, ?> readFlat(URL url, Charset charset) {
        return read(url, charset);
    }


    @Override
    public Map<String, ?> readObject(URL url, Charset charset) {
        return read(url, charset);
    }


    private Map<String, ?> read(URL url, Charset charset) {
        try (Reader reader = reader(url,charset)) {
            Properties properties = new Properties();
            properties.load(reader);
            Map<String,Object> map = new HashMap<>();
            properties.forEach((key,value)-> {
                String[] split = ((String)value).split(",");
                if (split.length == 1) {
                    map.put(String.valueOf(key), String.valueOf(value));
                } else {
                    map.put(String.valueOf(key), Arrays.asList(split));
                }
            });
            return map;
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }

}
