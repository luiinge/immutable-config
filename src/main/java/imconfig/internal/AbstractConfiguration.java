/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig.internal;


import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import imconfig.AnnotatedConfiguration;
import imconfig.Configuration;
import imconfig.ConfigurationFactory;


public abstract class AbstractConfiguration implements Configuration {

    protected final ConfigurationFactory builder;


    protected AbstractConfiguration(ConfigurationFactory builder) {
        this.builder = builder;
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
    public Configuration appendFromClasspathResource(String resourcePath) {
        return builder.merge(this, builder.fromClasspathResource(resourcePath));
    }


    @Override
    public Configuration appendFromClasspathResource(String resourcePath, ClassLoader classLoader) {
        return builder.merge(this, builder.fromClasspathResource(resourcePath, classLoader));
    }


    @Override
    public Configuration appendFromClasspathResourceOrURI(String path) {
        return builder.merge(this, builder.fromClasspathResourceOrURI(path));
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
}
