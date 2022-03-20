/*
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig;


import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;


/**
 * The main interface used to get configuration values and create derived configurations.
 */
public interface Config {


    /** Return a new configuration factory. Equivalent to invoke {@link ConfigFactory#instance()} */
    static ConfigFactory factory() {
        return ConfigFactory.instance();
    }


    /**
     * Creates a new configuration resulting of altering the property keys
     */
    Config alteringKeys(UnaryOperator<String> alterOperation);


    /**
     * Creates a new configuration resulting of filtering the property keys
     */
    Config filtered(Predicate<String> filter);


    /**
     * Creates a new configuration resulting of filtering the properties starting
     * with the given prefix, and the removing it
     */
    Config inner(String keyPrefix);

    /**
     * Creates a new configuration resulting of adding the given prefix to every the property keys
     */
    Config prefixing(String prefix);


    /**
     * @return <code>true</code> if there is no properties in this configuration
     */
    boolean isEmpty();


    /** @return <code>true</code> if there is a valued property with the given key */
    boolean hasProperty(String key);


    /** @return <code>true</code> if there is a multi-valued property with the given key */
    boolean hasMultivaluedProperty(String key);


    /** @return An iterable object over all the keys of the configuration,
     *  even for those which have no value */
    Iterable<String> keys();


    /** @return An iterator over all the keys of the configuration,
     *  even for those which have no value */
    Iterator<String> keyIterator();


    /** @return A stream from all the keys of the configuration,
     *  even for those which have no value */
    Stream<String> keyStream();


    /**
     * @return An optional string value, empty if the key does not exist
     */
    Optional<String> get(String key);

    /**
     * @return An optional mapped value, empty if the key does not exist
     */
    <T> Optional<T> get(String key, Function<String,T> mapper);

    /**
     * @return A list with string values, empty if the key does not exist
     */
    List<String> getList(String key);

    /**
     * @return A list with mapped values, empty if the key does not exist
     */
    <T> List<T> getList(String key, Function<String,T> mapper);

    /**
     * @return A set with values of the specified type, empty if the key does not
     *         exist
     */
    Set<String> getSet(String key);

    /**
     * @return A set with mapped values, empty if the key does not exist
     */
    <T> Set<T> getSet(String key, Function<String,T> mapper);


    /**
     * @return A stream with string values, empty if the key does not exist
     */
    Stream<String> getStream(String key);


    /** @return The configuration represented as a {@link Properties} object */
    Properties asProperties();


    /** @return The configuration represented as a {@link Map} object. */
    Map<String,String> asMap();


    /**
     * Create a new configuration resulting in the merge the current configuration
     * with another one
     */
    Config append(Config otherConfig);



    /**
     * @return whether there is a definition for the given property
     */
    boolean hasDefinition(String key);


    /**
     * Check whether the current value for the given property is valid according its definition.
     * If the property is multi-valued, it may return a different validation for each value
     * @param key The property key
     * @return The validation messages, or empty if the value is valid
     */
    List<String> validations(String key);



    /**
     * Retrieve the property definition for a given property
     */
    Optional<PropertyDefinition> getDefinition(String key);


    /**
     * Retrieve every property definition defined for this configuration
     * @return An unmodifiable map in the form of <property,definition>
     */
    Map<String,PropertyDefinition> getDefinitions();


    /**
     * Return a map in form of <tt>property=[validation_message1,...]</tt>
     * with the validation error messages for all invalid properties values
     * according the current definition.
     * <p>
 *     Configurations without definition will always return an empty map.
     * </p>
     */
    Map<String,List<String>> validations();


    /**
     * Ensures that all property values are valid according the current definition.
     * Otherwise, it will raise a {@link ConfigException} with a list of every
     * invalid value.
     * <p>
 *     Configurations without definition will never raise an exception using this method
     * </p>
     * @throws ConfigException if one or more properties have invalid values
     * @return The same instance, for convenience
     */
    Config validate() throws ConfigException;


    /**
     * Create a new configuration according the given property definitions.
     * <p>
     * Defined properties will be set to their default value if it exists and no current value is
     * set.
     * @see PropertyDefinition
     */
    Config accordingDefinitions(Collection<PropertyDefinition> definitions);


    /**
     * Get a textual representation of all defined properties
     */
    String getDefinitionsToString();
}
