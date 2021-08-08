package imconfig.types;

import imconfig.PropertyType;

public class IntegerPropertyType implements PropertyType {

    private final Long min;
    private final Long max;

    public IntegerPropertyType(Number min, Number max) {
        this.min = (min == null ? null : min.longValue());
        this.max = (max == null ? null : max.longValue());
        if (this.min != null && this.max != null && this.min > this.max) {
            throw new IllegalArgumentException("Minimum value cannot be greater than maximum value");
        }
    }

    @Override
    public String name() {
        return "integer";
    }

    @Override
    public String hint() {
        String hint;
        if (min==null && max==null) {
            hint = "Any integer number";
        } else if (min==null) {
            hint = "Integer number less than "+max;
        } else if (max==null) {
            hint = "Integer number greater than "+min;
        } else {
            hint = "Integer number between "+min+" and "+max;
        }
        return hint;
    }


    @Override
    public boolean accepts(String value) {
        boolean accepted = true;
        try {
            Long parsedValue = Long.valueOf(value);
            if (min != null && parsedValue.compareTo(min) < 0) {
                accepted = false;
            }
            if (max != null && parsedValue.compareTo(max) > 0) {
                accepted = false;
            }
            return accepted;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}