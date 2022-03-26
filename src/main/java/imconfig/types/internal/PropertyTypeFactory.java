package imconfig.types.internal;

import java.util.*;
import imconfig.*;
import imconfig.types.*;

public final class PropertyTypeFactory {

    public PropertyType create(String type, Map<String, Object> arguments) {
        if (type == null) {
            throw new ConfigException("type must be defined");
        }
        if (arguments == null) {
            arguments = Map.of();
        }
        try {
            return createrPropertyType(type, arguments);
        } catch (ConfigException e)  {
            throw e;
        } catch (RuntimeException e)  {
            throw new ConfigException("Bad property definition",e);
        }
    }


    @SuppressWarnings("unchecked")
    private PropertyType createrPropertyType(String type, Map<String, Object> arguments) {
        if ("text".equals(type)) {
            return new TextPropertyType((String) arguments.get("pattern"));
        }
        if ("integer".equals(type)) {
            return new IntegerPropertyType(
                (Number) arguments.get("min"),
                (Number) arguments.get("max")
            );
        }
        if ("decimal".equals(type)) {
            return new DecimalPropertyType(
                (Number) arguments.get("min"),
                (Number) arguments.get("max")
            );
        }
        if ("enum".equals(type)) {
            return new EnumPropertyType((List<String>) arguments.get("values"));
        }
        if ("boolean".equals(type)) {
            return new BooleanPropertyType();
        }
        throw new ConfigException(
            "Undefined property type: "+ type +
            " . Accepted values are: text, integer, decimal, enum"
        );
    }

}
