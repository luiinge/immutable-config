/**
 * @author Luis Iñesta Gelabert - luiinge@gmail.com
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
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
import imconfig.PropertyDefinition;


public class ApacheConfiguration2Factory implements ConfigurationFactory {

    private final ConversionHandler conversionHandler = new ApacheConfiguration2ConversionHandler();
    private final PropertyDefinitionParser parser = new PropertyDefinitionParser();


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
        Map<String, PropertyDefinition> definitions = new HashMap<>(base.getDefinitions());
        definitions.putAll(delta.getDefinitions());

        return new ApacheConfiguration2(this, definitions, result);
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



    private Configuration fromMap(Map<String, ?> properties, Collection<PropertyDefinition> defs) {
        final BaseConfiguration configuration = configure(new BaseConfiguration());
        for (final Entry<String, ?> property : properties.entrySet()) {
            configuration.addProperty(property.getKey(), property.getValue());
        }
        var definitions = defs.stream()
            .collect(Collectors.toMap(PropertyDefinition::property,x->x));
        return new ApacheConfiguration2(this, definitions, configuration);
    }



    @Override
    public Configuration fromURI(URI uri) {
        return fromURI(uri,null);
    }


    @Override
    public Configuration fromResource(String resource, ClassLoader classLoader) {
        return fromURI(URI.create("classpath:///"+resource),classLoader);
    }



    private Configuration fromURI(URI uri, ClassLoader classLoader) {
       try {
           return buildFromURL(adaptURI(uri, classLoader));
        } catch (MalformedURLException e) {
           throw new ConfigurationException(e);
        }
    }



    @Override
    public Configuration accordingDefinitions(Collection<PropertyDefinition> definitions) {
        Map<String,String> defaultValues = definitions
            .stream()
            .filter(definition -> definition.defaultValue().isPresent())
            .collect(Collectors.toMap(
                PropertyDefinition::property,
                definition->definition.defaultValue().get()
            ));
        return fromMap(defaultValues,definitions);
    }



    @Override
    public Configuration accordingDefinitionsFromURI(URI uri) {
        return accordingDefinitionsFromURI(uri,null);
    }


    private Configuration accordingDefinitionsFromURI(URI uri, ClassLoader classLoader) {
        try (var inputStream = adaptURI(uri, classLoader).openStream()) {
            return accordingDefinitions(parser.read(inputStream));
        } catch (final IOException e) {
            throw new ConfigurationException(e);
        }
    }



    @Override
    public Configuration accordingDefinitionsFromPath(Path path) {
        return accordingDefinitionsFromURI(path.toUri());
    }


    @Override
    public Configuration accordingDefinitionsFromResource(String resource,ClassLoader classLoader) {
        return accordingDefinitionsFromURI(URI.create("classpath:///"+resource),classLoader);
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



    private Configuration buildFromJSON(URL url) {
        try (InputStream stream = url.openStream()) {
            JSONConfiguration json = configure(new JSONConfiguration());
            json.read(stream);
            return new ApacheConfiguration2(this, Map.of(), json);
        } catch (IOException | org.apache.commons.configuration2.ex.ConfigurationException e) {
            throw new ConfigurationException(e);
        }
    }


    private Configuration buildFromYAML(URL url) {
        try (InputStream stream = url.openStream()) {
            YAMLConfiguration yaml = configure(new YAMLConfiguration());
            yaml.read(stream);
            return new ApacheConfiguration2(this, Map.of(), yaml);
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



    private URL adaptURI(URI uri, ClassLoader classLoader) throws MalformedURLException {
        if ("classpath".equals(uri.getScheme())) {
            return new URL("classpath",null, -1, uri.getPath(), new ClasspathURLStreamHandler(classLoader));
        } else {
            return uri.toURL();
        }
    }

}
