package imconfig;

import java.util.List;
import java.util.Objects;
import imconfig.types.DecimalPropertyType;
import imconfig.types.EnumPropertyType;
import imconfig.types.IntegerPropertyType;
import imconfig.types.TextPropertyType;

/**
 * This class allows you to create new {@link PropertyDefinition} objects in a fluent
 * manner, setting only the actual information you required. Invoke {@link #build()}
 * after setting the attributes to obtain the created object.
 * @author Luis Iñesta Gelabert - luiinge@gmail.com
 *
 */
public class PropertyDefinitionBuilder {

    private String property;
    private String description;
    private boolean required;
    private String defaultValue;
    private PropertyType propertyType;


    public PropertyDefinitionBuilder property(String property) {
        this.property = property;
        return this;
    }

    public PropertyDefinitionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public PropertyDefinitionBuilder required() {
        this.required = true;
        return this;
    }


    public PropertyDefinitionBuilder required(Boolean required) {
        this.required = Boolean.TRUE.equals(required);
        return this;
    }


    public PropertyDefinitionBuilder defaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }


    public PropertyDefinitionBuilder textType(String pattern) {
        this.propertyType = new TextPropertyType(pattern);
        return this;
    }

    public PropertyDefinitionBuilder textType() {
        return textType(null);
    }


    public PropertyDefinitionBuilder integerType(Number min, Number max) {
        this.propertyType = new IntegerPropertyType(min, max);
        return this;
    }

    public PropertyDefinitionBuilder integerType() {
        return integerType(null,null);
    }

    public PropertyDefinitionBuilder decimalType(Number min, Number max) {
        this.propertyType = new DecimalPropertyType(min, max);
        return this;
    }

    public PropertyDefinitionBuilder decimalType() {
        return decimalType(null,null);
    }

    public PropertyDefinitionBuilder enumType(String... values) {
        this.propertyType = new EnumPropertyType(List.of(values));
        return this;
    }


    public PropertyDefinitionBuilder propertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
        return this;
    }


    public PropertyDefinition build() {
        Objects.requireNonNull(property);
        Objects.requireNonNull(propertyType);
        return new PropertyDefinition(property, description, required, defaultValue, propertyType);
    }



}