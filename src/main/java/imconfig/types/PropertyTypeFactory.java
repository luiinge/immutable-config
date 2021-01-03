package imconfig.types;

import java.util.List;
import java.util.Map;
import imconfig.ConfigurationException;
import imconfig.PropertyType;

public final class PropertyTypeFactory {


    @SuppressWarnings("unchecked")
    public PropertyType create(String type, Map<String,Object> arguments) {
        if (type == null) {
            throw new ConfigurationException("type must be defined");
        }
        if (arguments == null) {
            arguments = Map.of();
        }
        try {
            if ("text".equals(type)) {
                return new TextPropertyType((String)arguments.get("pattern"));
            }
            if ("integer".equals(type)) {
                return new IntegerPropertyType(
                    (Number)arguments.get("min"),
                    (Number)arguments.get("max")
                );
            }
            if ("decimal".equals(type)) {
                return new DecimalPropertyType(
                    (Number)arguments.get("min"),
                    (Number)arguments.get("max")
                );
            }
            if ("enum".equals(type)) {
                return new EnumPropertyType((List<String>)arguments.get("values"));
            }
            throw new ConfigurationException(
                "Undefined property type: "+type+
                " . Accepted values are: text, integer, decimal, enum"
            );
        } catch (ConfigurationException e)  {
            throw e;
        } catch (RuntimeException e)  {
            throw new ConfigurationException("Bad property definition",e);
        }
    }

}
