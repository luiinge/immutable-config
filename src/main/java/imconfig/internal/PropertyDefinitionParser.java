package imconfig.internal;

import imconfig.*;

import imconfig.types.internal.PropertyTypeFactory;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class PropertyDefinitionParser {

    private final PropertyTypeFactory typeFactory = new PropertyTypeFactory();
    private final ExternalReaderFactory externalReaderFactory;


    public PropertyDefinitionParser(ExternalReaderFactory externalReaderFactory) {
        this.externalReaderFactory = externalReaderFactory;
    }

    @SuppressWarnings("unchecked")
    Collection<PropertyDefinition> read (String extension, URL url, Charset charset) {
        var reader = externalReaderFactory.readerForExtension(extension);
        Map<String,Map<String,Object>> map = (Map<String, Map<String, Object>>) reader.readObject(url, charset);
        return map.entrySet().stream().map(this::parseDefinition).collect(Collectors.toList());
    }



    @SuppressWarnings("unchecked")
    private PropertyDefinition parseDefinition(Entry<String, Map<String, Object>> entry) {
        try {
            var definition = entry.getValue();
            return PropertyDefinition.builder()
                .property(entry.getKey())
                .description((String) definition.get("description"))
                .required((Boolean)definition.get("required"))
                .defaultValue(toString(definition.get("defaultValue")))
                .propertyType(typeFactory.create(
                    (String)definition.get("type"),
                    (Map<String, Object>) definition.get("constraints")
                ))
                .build();
        } catch (RuntimeException e) {
            throw new ConfigException(
                "Bad configuration of property '"+entry.getKey()+"' : "+e.getMessage(), e
            );
        }
    }


    private String toString(Object object) {
        return object == null ? null : object.toString();
    }


}
