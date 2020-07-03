/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig;


import java.net.URI;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;


/**
 * The main interface used to get configuration values and create derived configurations.
 */
public interface Configuration {



    /**
     * Creates a new configuration resulting of adding the given prefix to every
     * key
     */
    Configuration withPrefix(String keyPrefix);


    /**
     * Creates a new configuration resulting of filtering the properties starting
     * with the given prefix
     */
    Configuration filtered(String keyPrefix);


    /**
     * Creates a new configuration resulting of filtering the properties starting
     * with the given prefix, and the removing it
     */
    Configuration inner(String keyPrefix);


    /**
     * @return <code>true</code> if there is no properties in this configuration
     */
    boolean isEmpty();


    /** @return <code>true</code> if there is a valued property with the given key */
    boolean hasProperty(String key);


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
     * @return An optional value of the specified type, empty if the key does not
     *         exist
     */
    <T> Optional<T> get(String key, Class<T> type);


    default <T> Configuration ifPresent(String key, Class<T> type, Consumer<T> consumer) {
        get(key,type).ifPresent(consumer);
        return this;
    }


    /**
     * @return A list with values of the specified type, empty if the key does
     *         not exist
     */
    <T> List<T> getList(String key, Class<T> type);


    /**
     * @return A set with values of the specified type, empty if the key does not
     *         exist
     */
    <T> Set<T> getSet(String key, Class<T> type);


    /**
     * @return A stream with values of the specified type, empty if the key does
     *         not exist
     */
    <T> Stream<T> getStream(String key, Class<T> type);


    /** @return The configuration represented as a {@link Properties} object */
    Properties asProperties();


    /** @return The configuration represented as a {@link Map} object */
    Map<String, String> asMap();


    /** @return A textual representation of the configuration */
    @Override
    String toString();


    /** Perform an action for each pair <code>[key,value]</code> */
    void forEach(BiConsumer<String, String> consumer);


    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from a class annotated with {@link AnnotatedConfiguration}
     *
     * @param configuredClass Class annotated with {@link AnnotatedConfiguration}
     * @throws ConfigurationException if the configuration was not loaded
     */
    Configuration appendFromAnnotation(Class<?> configuredClass);


    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from a {@link AnnotatedConfiguration} annotation
     *
     * @param annotation
     * @throws ConfigurationException if the configuration was not loaded
     */
    Configuration appendFromAnnotation(AnnotatedConfiguration annotation);


    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from the environment properties
     */
    Configuration appendFromEnvironment();


    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from the {@link System} properties
     */
    Configuration appendFromSystem();


    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from the resource of the specified path
     *
     * @throws ConfigurationException if the configuration was not loaded
     */
    Configuration appendFromPath(Path path);



    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from the specified URI
     *
     * @throws ConfigurationException if the configuration was not loaded
     */
    Configuration appendFromURI(URI uri);


    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from either a Java classpath resource (if the path
     * starts with the pseudo-schema <code>classpath:</code>) or a regular URI
     * resource
     */
    Configuration appendFromClasspathResourceOrURI(String path);


    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from a {@link Properties} object
     */
    Configuration appendFromProperties(Properties properties);


    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from a {@link Map} object
     */
    Configuration appendFromMap(Map<String, ?> propertyMap);


    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from one or several Java class resources resolved
     * using the {@link ClassLoader#getResources(String)} method of the thread
     * static class loader
     */
    Configuration appendFromClasspathResource(String resourcePath);


    /**
     * Create a new configuration resulting the merge the current configuration
     * with the configuration from one or several Java class resources resolved
     * using the {@link ClassLoader#getResources(String)} method of the specified
     * class loader
     */
    Configuration appendFromClasspathResource(String resourcePath, ClassLoader classLoader);


    /**
     * Create a new configuration resulting the merge the current configuration
     * with another one
     */
    Configuration append(Configuration otherConfiguration);


    /**
     * Create a new configuration resulting of adding or replacing a property to
     * the current configuration. Since this method creates a new object each
     * time, it should not be used as the primary way to create large
     * configurations but rather to tweak existing ones.
     */
    Configuration appendProperty(String localRepository, String toString);




    /**
     * Create a new configuration resulting of merge the current configuration with
     * the configuration from a set of directly passed strings, using each two entries as a pair of
     * <tt>key,value</tt>.
     * @throws IllegalArgumentException if the number of strings is not even
     */
    default Configuration appendFromPairs(String... pairs) {
        return append(ConfigurationFactory.instance().fromPairs(pairs));
    }


}
