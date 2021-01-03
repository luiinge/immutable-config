package imconfig;

import java.util.Optional;

/**
 * This class instantiates an immutable value object that represents
 * the definition of a given property.
 * <p>
 * Property definitions can be created manually or read from a YAML file.
 *
 * @author Luis Iñesta Gelabert - luiinge@gmail.com
 *
 */
public class PropertyDefinition {

    /**
     * Get a new builder for this class
     */
    public static PropertyDefinitionBuilder builder() {
        return new PropertyDefinitionBuilder();
    }



    private final String property;
    private final String description;
    private final boolean required;
    private final String defaultValue;
    private final PropertyType propertyType;


    PropertyDefinition(
        String property,
        String description,
        boolean required,
        String defaultValue,
        PropertyType type
    ) {
        this.property = property;
        this.description = (description == null ? "" : description);
        this.defaultValue = defaultValue;
        this.required = required;
        this.propertyType = type;
    }


    public String property() {
        return property;
    }

    public String description() {
        return description;
    }

    public boolean required() {
        return required;
    }

    public Optional<String> defaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    public String type() {
        return propertyType.name();
    }

    public String hint() {
        return propertyType.hint();
    }

    public boolean accepts(String value) {
        if (required && (value == null || value.isBlank())) {
            return false;
        }
        return propertyType.accepts(value);
    }
}
