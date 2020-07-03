/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig.internal;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.convert.ConversionHandler;

import imconfig.AnnotatedConfiguration;
import imconfig.Configuration;
import imconfig.ConfigurationException;
import imconfig.ConfigurationFactory;
import imconfig.Property;


public class ApacheConfiguration2Factory implements ConfigurationFactory {

    private final ConversionHandler conversionHandler = new ApacheConfiguration2ConversionHandler();


    @Override
    public Configuration merge(Configuration base, Configuration delta) {

        AbstractConfiguration result = new BaseConfiguration();
        for (String property : delta.keys()) {
            var existing = base.getList(property,String.class);
            var added = delta.getList(property,String.class);
            if (existing.isEmpty() && added.isEmpty()) {
                result.setProperty(property,"");
            } else if (!added.isEmpty()) {
                added.forEach(value -> result.addProperty(property,value));
            }
        }
        for (String property : base.keys()) {
            if (result.containsKey(property)) {
                continue;
            }
            base.getList(property,String.class).forEach(value -> result.addProperty(property,value));
        }
        return new ApacheConfiguration2(this, result);
    }





    @Override
    public Configuration empty() {
        return new ApacheConfiguration2(this, new BaseConfiguration());
    }


    @Override
    public Configuration fromAnnotation(Class<?> configuredClass) {
        return Optional.ofNullable(configuredClass.getAnnotation(AnnotatedConfiguration.class))
            .map(this::fromAnnotation)
            .orElseThrow(
                () -> new ConfigurationException(
                    configuredClass + " is not annotated with @Configurator"
                )
            );
    }


    @Override
    public Configuration fromAnnotation(AnnotatedConfiguration annotation) {
        BaseConfiguration configuration = configure(new BaseConfiguration());
        for (Property property : annotation.value()) {
            String[] value = property.value();
            if (value.length == 1) {
                configuration.addProperty(property.key(), value[0]);
            } else {
                configuration.addProperty(property.key(), value);
            }
        }
        return new ApacheConfiguration2(this, configuration);

    }


    @Override
    public Configuration fromEnvironment() {
        return new ApacheConfiguration2(this, new EnvironmentConfiguration());
    }


    @Override
    public Configuration fromSystem() {
        return new ApacheConfiguration2(this, new SystemConfiguration());
    }


    @Override
    public Configuration fromPath(Path path) {
        return fromURI(path.toUri());
    }


    @Override
    public Configuration fromClasspathResourceOrURI(String path) {
        if (path.startsWith("classpath:")) {
            return fromClasspathResource(path.substring("classpath:".length()));
        } else {
            return fromURI(URI.create(path));
        }
    }


    @Override
    public Configuration fromProperties(Properties properties) {
        final BaseConfiguration configuration = configure(new BaseConfiguration());
        for (final Entry<Object, Object> property : properties.entrySet()) {
            configuration.addProperty(property.getKey().toString(), property.getValue());
        }
        return new ApacheConfiguration2(this, configuration);
    }


    @Override
    public Configuration fromMap(Map<String, ?> properties) {
        final BaseConfiguration configuration = configure(new BaseConfiguration());
        for (final Entry<String, ?> property : properties.entrySet()) {
            configuration.addProperty(property.getKey(), property.getValue());
        }
        return new ApacheConfiguration2(this, configuration);
    }


    @Override
    public Configuration fromClasspathResource(String resourcePath, ClassLoader classLoader) {
        try {
            Configuration base = empty();
            List<Configuration> urlConfs = buildFromURLEnum(
                classLoader.getResources(resourcePath),
                resourcePath
            );
            for (Configuration urlConf : urlConfs) {
                base = base.append(urlConf);
            }
            return base;
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }


    @Override
    public Configuration fromClasspathResource(String resourcePath) {
        return fromClasspathResource(resourcePath, getClass().getClassLoader());
    }


    @Override
    public Configuration fromURI(URI uri) {
        try {
            if (uri.getScheme() == null) {
                Path path = Paths.get(uri.getPath());
                return fromPath(path);
            }
            return buildFromURL(uri.toURL());
        } catch (final MalformedURLException e) {
            throw new ConfigurationException(e);
        }
    }


    private Configuration buildFromURL(URL url) {
        Configuration configuration;
        if (url.getFile().endsWith(".properties")) {
            configuration = buildFromPropertiesFile(url);
        } else if (url.getFile().endsWith(".json")) {
            configuration = buildFromJSON(url);
        } else if (url.getFile().endsWith(".xml")) {
            configuration = buildFromXML(url);
        } else if (url.getFile().endsWith(".yaml")) {
            configuration = buildFromYAML(url);
        } else {
            throw new ConfigurationException("Cannot determine resource type of " + url);
        }
        return configuration;
    }


    private List<Configuration> buildFromURLEnum(Enumeration<URL> urls, String resourcePath) {
        final List<Configuration> configurations = new ArrayList<>();
        if (!urls.hasMoreElements()) {
            throw new ConfigurationException("Cannot find resource " + resourcePath);
        } else {
            for (URL url : distinctURLs(urls)) {
                configurations.add(buildFromURL(url));
            }
        }
        return configurations;
    }



    private Configuration buildFromJSON(URL url) {
        try (InputStream stream = url.openStream()) {
            JSONConfiguration json = configure(new JSONConfiguration());
            json.read(stream);
            return new ApacheConfiguration2(this, json);
        } catch (IOException | org.apache.commons.configuration2.ex.ConfigurationException e) {
            throw new ConfigurationException(e);
        }
    }


    private Configuration buildFromYAML(URL url) {
        try (InputStream stream = url.openStream()) {
            YAMLConfiguration yaml = configure(new YAMLConfiguration());
            yaml.read(stream);
            return new ApacheConfiguration2(this, yaml);
        } catch (IOException | org.apache.commons.configuration2.ex.ConfigurationException e) {
            throw new ConfigurationException(e);
        }
    }


    private Configuration buildFromPropertiesFile(URL url) {
        try (InputStream stream = url.openStream(); Reader reader = new InputStreamReader(stream)) {
            PropertiesConfiguration properties = configure(new PropertiesConfiguration());
            properties.read(reader);
            return new ApacheConfiguration2(this, properties);
        } catch (IOException | org.apache.commons.configuration2.ex.ConfigurationException e) {
            throw new ConfigurationException(e);
        }
    }


    private Configuration buildFromXML(URL url) {
        try {
            XMLConfiguration xml = configure(new Configurations().xml(url));
            return new ApacheConfiguration2(this, xml);
        } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
            throw new ConfigurationException(e);
        }
    }




    private <T extends AbstractConfiguration> T configure(T configuration) {
        configuration.setConversionHandler(conversionHandler);
        return configuration;
    }

    private Set<URL> distinctURLs (Enumeration<URL> urls) {
        return Collections.list(urls).stream().collect(Collectors.toSet());
    }
}
