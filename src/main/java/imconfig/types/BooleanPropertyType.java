package imconfig.types;

import imconfig.PropertyType;
public class BooleanPropertyType implements PropertyType {


    @Override
    public String name() {
        return "boolean";
    }

    @Override
    public boolean accepts(String value) {
        return "true".equals(value) || "false".equals(value);
    }

    @Override
    public String hint() {
        return "true | false";
    }

}