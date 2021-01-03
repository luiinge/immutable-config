/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig;


import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;


/**
 *  A configuration factory is responsible of creating new instances of
 * {@link Configuration} via multiple alternative sources such as URI,
 * classpath resources, maps, and properties objects. Any new configuration
 * object should be created using a factory.
 * <p>
 *  On building a configuration from a external resource, the builder would
 *  autodetect the resource type (usually looking at its file extension) and
 *  treat the content properly, accepting multiple formats as JSON, YAML, XML,
 *  and .properties files.
 */

public interface ConfigurationFactory {

    static ConfigurationFactory instance() {
        return ServiceLoader.load(ConfigurationFactory.class).iterator().next();
    }


    /**
     * Create a new configuration composed of two other configurations. When the same
     * property is present in two or more configurations, the value from the
     * delta configuration will prevail (except when it has an empty value)
     */
    Configuration merge(Configuration base, Configuration delta);


    /**
     * Create a new empty configuration
     */
    Configuration empty();


    /**
     * Create a new configuration from a class annotated with
     * {@link AnnotatedConfiguration}
     *
     * @param configuredClass Class annotated with {@link AnnotatedConfiguration}
     * @throws ConfigurationException if the configuration was not loaded
     */
    Configuration fromAnnotation(Class<?> configuredClass);


    /**
     * Create a new configuration from a {@link AnnotatedConfiguration} annotation
     *
     * @param annotation
     * @throws ConfigurationException if the configuration was not loaded
     */
    Configuration fromAnnotation(AnnotatedConfiguration annotation);


    /**
     * Create a new configuration from the OS environment properties
     */
    Configuration fromEnvironment();


    /**
     * Create a new configuration from the {@link System} properties
     */
    Configuration fromSystem();


    /**
     * Create a new configuration from the resource of the specified path
     *
     * @throws ConfigurationException if the configuration was not loaded
     */
    Configuration fromPath(Path path);



    /**
     * Create a new configuration from the specified URI
     *
     * @throws ConfigurationException if the configuration was not loaded
     */
    Configuration fromURI(URI uri);


    /**
     * Create a new configuration from either a Java classpath resource (if the
     * path starts with the pseudo-schema <code>classpath:</code>) or a regular
     * URI resource
     */
    Configuration fromClasspathResourceOrURI(String path);


    /**
     * Create a new configuration from a {@link Properties} object
     */
    Configuration fromProperties(Properties properties);


    /**
     * Create a new configuration from a {@link Map} object
     */
    Configuration fromMap(Map<String, ?> propertyMap);


    /**
     * Create a new configuration from one or several Java class resources
     * resolved using the {@link ClassLoader#getResources(String)} method of the
     * thread default class loader
     */
    Configuration fromClasspathResource(String resourcePath);


    /**
     * Create a new configuration from one or several Java class resources
     * resolved using the {@link ClassLoader#getResources(String)} method of the
     * specified class loader
     */
    Configuration fromClasspathResource(String resourcePath, ClassLoader classLoader);



    /**
     * Create a new configuration from directly passed strings, using each two entries as a pair of
     * <tt>key,value</tt>.
     * @throws IllegalArgumentException if the number of strings is not even
     */
    default Configuration fromPairs(String... pairs) {
        if (pairs.length % 2 == 1) {
            throw new IllegalArgumentException("Number of arguments must be even");
        }
        Map<String,String> map = new LinkedHashMap<>();
        for (int i=0;i<pairs.length;i+=2) {
            map.put(pairs[i],pairs[i+1]);
        }
        return fromMap(map);
    }



    /**
     * Create a new empty configuration according the given property definitions
     * <p>
     * Defined properties will be set to their default value if it exists
     * @see PropertyDefinition
     */
    Configuration accordingDefinitions(Collection<PropertyDefinition> definitions);


    /**
     * Create a new empty configuration according the property definitions from the given URL.
     * <p>
     * Defined properties will be set to their default value if it exists
     * @see PropertyDefinition
     */
    Configuration accordingDefinitionsFromURL(URL url);


    /**
     * Create a new empty configuration according the property definitions from the given path.
     * <p>
     * Defined properties will be set to their default value if it exists
     * @see PropertyDefinition
     */
    Configuration accordingDefinitionsFromPath(Path path);


    /**
     * Create a new empty configuration according the property definitions from the given URI.
     * <p>
     * Defined properties will be set to their default value if it exists
     * @see PropertyDefinition
     */
    Configuration accordingDefinitionsFromURI(URI uri);












}
