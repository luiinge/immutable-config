/*
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig;


import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;


/**
 *  A configuration factory is responsible of creating new instances of
 * {@link Config} via multiple alternative sources such as URI,
 * classpath resources, maps, and properties objects. Any new configuration
 * object should be created using a factory.
 * <p>
 *  On building a configuration from a external resource, the builder would
 *  autodetect the resource type (usually looking at its file extension) and
 *  treat the content properly, accepting multiple formats as JSON, YAML, XML,
 *  and .properties files.
 */

public interface ConfigFactory {

    /**
     * @return A new configuration factory
     */
    static ConfigFactory instance() {
        try {
            return ServiceLoader.load(ConfigFactory.class).stream()
                .findFirst()
                .orElseThrow()
                .type()
                .getConstructor()
                .newInstance();
        } catch (ReflectiveOperationException e) {
            throw new ConfigException(e);
        }
    }


    /**
     * Create a new configuration composed of two other configurations. When the same
     * property is present in two or more configurations, the value from the
     * delta configuration will prevail (except when it has an empty value)
     */
    Config merge(Config base, Config delta);


    /**
     * Create a new empty configuration
     */
    Config empty();


    /**
     * Create a new configuration from a class annotated with
     * {@link AnnotatedConfig}
     *
     * @param configuredClass Class annotated with {@link AnnotatedConfig}
     * @throws ConfigException if the configuration was not loaded
     */
    Config fromAnnotation(Class<?> configuredClass);


    /**
     * Create a new configuration from a {@link AnnotatedConfig} annotation
     *
     * @throws ConfigException if the configuration was not loaded
     */
    Config fromAnnotation(AnnotatedConfig annotation);


    /**
     * Create a new configuration from the OS environment properties
     */
    Config fromEnvironment();


    /**
     * Create a new configuration from the {@link System} properties
     */
    Config fromSystem();


    /**
     * Create a new configuration from the resource of the specified path
     *
     * @throws ConfigException if the configuration was not loaded
     */
    Config fromPath(Path path, Charset charset);



    /**
     * Create a new configuration from the specified URI.
     *
     * @throws ConfigException if the configuration was not loaded
     */
    Config fromURI(URI uri, Charset charset);


    /**
     * Create a new configuration from the specified classpath resource.
     *
     * @throws ConfigException if the configuration was not loaded
     */
    Config fromResource(String resource, Charset charset, ClassLoader classLoader);



    /**
     * Create a new configuration from a {@link Properties} object
     */
    Config fromProperties(Properties properties);


    /**
     * Create a new configuration from a {@link Map} object
     */
    Config fromMap(Map<String, String> propertyMap);



    /**
     * Create a new configuration from directly passed strings, using each two entries as a pair of
     * <tt>key,value</tt>.
     * @throws IllegalArgumentException if the number of strings is not even
     */
    default Config fromPairs(String... pairs) {
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
    Config accordingDefinitions(Collection<PropertyDefinition> definitions);



    /**
     * Create a new empty configuration according the property definitions from the given path.
     * <p>
     * Defined properties will be set to their default value if it exists
     * @see PropertyDefinition
     */
    Config accordingDefinitionsFromPath(Path path, Charset charset);


    /**
     * Create a new empty configuration according the property definitions from the given URI.
     * <p>
     * You can use the schema <pre>classpath:</pre> to reference classpath resources.
     * <p>
     * Defined properties will be set to their default value if it exists
     * @see PropertyDefinition
     */
    Config accordingDefinitionsFromURI(URI uri, Charset charset);


    /**
     * Create a new empty configuration according the property definitions from the given
     * classpath resource.
     * <p>
     * Defined properties will be set to their default value if it exists
     * @see PropertyDefinition
     */
    Config accordingDefinitionsFromResource(String resource, Charset charset, ClassLoader classLoader);















}
