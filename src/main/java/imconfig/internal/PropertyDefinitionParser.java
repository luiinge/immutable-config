package imconfig.internal;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.Yaml;
import imconfig.ConfigurationException;
import imconfig.PropertyDefinition;
import imconfig.types.PropertyTypeFactory;

public class PropertyDefinitionParser {

    private final Yaml yaml = new Yaml();
    private final PropertyTypeFactory typeFactory = new PropertyTypeFactory();


    @SuppressWarnings("unchecked")
    public Collection<PropertyDefinition> read (Reader reader) {
        Map<String,Map<String,Object>> map = yaml.loadAs(reader, HashMap.class);
        return map.entrySet().stream().map(this::parseDefinition).collect(Collectors.toList());
    }


    @SuppressWarnings("unchecked")
    public Collection<PropertyDefinition> read (InputStream inputStream) {
        Map<String,Map<String,Object>> map = yaml.loadAs(inputStream, HashMap.class);
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
            throw new ConfigurationException(
                "Bad configuration of property '"+entry.getKey()+"' : "+e.getMessage()
            );
        }
    }


    private String toString(Object object) {
        return object == null ? null : object.toString();
    }


}
