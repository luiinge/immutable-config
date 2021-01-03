package imconfig;

import java.util.Optional;

public interface DefinedConfiguration extends Configuration {


    /**
     * Check whether the current value for the given property is valid according its definition
     * @param key The property key
     * @return The validation message, or empty if the value is valid
     */
    Optional<String> validation(String key);


    /**
     * Retrieve the property definition for a given property
     */
    Optional<PropertyDefinition> getDefinition(String key);



}
