/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig.internal;


import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import imconfig.AnnotatedConfiguration;
import imconfig.Configuration;
import imconfig.ConfigurationFactory;
import imconfig.PropertyDefinition;


public abstract class AbstractConfiguration implements Configuration {

    protected final ConfigurationFactory builder;
    protected final Map<String,PropertyDefinition> definitions;


    protected AbstractConfiguration(
        ConfigurationFactory builder,
        Map<String,PropertyDefinition> definitions
    ) {
        this.builder = builder;
        this.definitions = definitions;
    }


    @Override
    public Configuration append(Configuration otherConfiguration) {
        return builder.merge(this, otherConfiguration);
    }


    @Override
    public Configuration appendFromAnnotation(Class<?> configuredClass) {
        return builder.merge(this, builder.fromAnnotation(configuredClass));
    }


    @Override
    public Configuration appendFromAnnotation(AnnotatedConfiguration annotation) {
        return builder.merge(this, builder.fromAnnotation(annotation));
    }


    @Override
    public Configuration appendFromResource(String resourcePath, ClassLoader classLoader) {
        return builder.merge(this, builder.fromResource(resourcePath, classLoader));
    }


    @Override
    public Configuration appendFromEnvironment() {
        return builder.merge(this, builder.fromEnvironment());
    }


    @Override
    public Configuration appendFromSystem() {
        return builder.merge(this, builder.fromSystem());
    }


    @Override
    public Configuration appendFromMap(Map<String, ?> propertyMap) {
        return builder.merge(this, builder.fromMap(propertyMap));
    }


    @Override
    public Configuration appendFromPath(Path path) {
        return builder.merge(this, builder.fromPath(path));
    }


    @Override
    public Configuration appendFromProperties(Properties properties) {
        return builder.merge(this, builder.fromProperties(properties));
    }


    @Override
    public Configuration appendFromURI(URI uri) {
        return builder.merge(this, builder.fromURI(uri));
    }


    @Override
    public Configuration appendProperty(String property, String value) {
        Map<String, String> singlePropertyMap = new HashMap<>();
        singlePropertyMap.put(property, value);
        return builder.merge(this, builder.fromMap(singlePropertyMap));
    }


    @Override
    public Map<String, PropertyDefinition> getDefinitions() {
        return Collections.unmodifiableMap(definitions);
    }


    @Override
    public Optional<PropertyDefinition> getDefinition(String key) {
        return Optional.ofNullable(definitions.get(key));
    }


    @Override
    public Optional<String> validation(String key) {
        String value = get(key, String.class).orElse(null);
        if (value == null) {
            return Optional.empty();
        }
        return getDefinition(key)
            .filter(definition->!definition.accepts(value))
            .map(PropertyDefinition::hint);
    }


    @Override
    public Configuration accordingDefinitions(Collection<PropertyDefinition> definitions) {
        return builder.merge(this, builder.accordingDefinitions(definitions));
    }


    @Override
    public Configuration accordingDefinitionsFromPath(Path path) {
        return builder.merge(this, builder.accordingDefinitionsFromPath(path));
    }


    @Override
    public Configuration accordingDefinitionsFromURI(URI uri) {
        return builder.merge(this, builder.accordingDefinitionsFromURI(uri));
    }


    @Override
    public Configuration accordingDefinitionsFromResource(String resource,ClassLoader classLoader) {
        return builder.merge(
            this,
            builder.accordingDefinitionsFromResource(resource, classLoader)
        );
    }
}
