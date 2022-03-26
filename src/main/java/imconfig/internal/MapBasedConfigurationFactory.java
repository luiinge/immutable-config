package imconfig.internal;

import imconfig.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class MapBasedConfigurationFactory implements ConfigFactory {

    private final ExternalReaderFactory externalReaderFactory;
    private final PropertyDefinitionParser propertyDefinitionParser;



    public MapBasedConfigurationFactory() {
        this.externalReaderFactory = new ExternalReaderFactory();
        this.propertyDefinitionParser = new PropertyDefinitionParser(
            externalReaderFactory
        );
    }


    @Override
    public Config merge(Config base, Config delta) {
        Map<String,Object> map = new LinkedHashMap<>();
        for (String key : base.keys()) {
            if (base.hasMultivaluedProperty(key)) {
                map.put(key, base.getList(key));
            } else {
                map.put(key, base.get(key).orElseThrow());
            }
        }
        for (String key : delta.keys()) {
            if (delta.hasMultivaluedProperty(key)) {
                map.put(key, delta.getList(key));
            } else {
                map.put(key, delta.get(key).orElseThrow());
            }
        }
        Map<String, PropertyDefinition> definitions = new LinkedHashMap<>(base.getDefinitions());
        definitions.putAll(delta.getDefinitions());
        return new MapBasedConfig(this,map,definitions);
    }


    @Override
    public Config empty() {
        return EmptyConfig.INSTANCE;
    }


    @Override
    public Config fromAnnotation(Class<?> configuredClass) {
        return Optional.ofNullable(configuredClass.getAnnotation(AnnotatedConfig.class))
            .map(this::fromAnnotation)
            .orElseThrow(
                () -> new ConfigException(
                    configuredClass + " is not annotated with @Configurator"
                )
            );
    }


    @Override
    public Config fromAnnotation(AnnotatedConfig annotation) {
        Map<String,Object> map = new HashMap<>();
        for (Property property : annotation.value()) {
            String[] value = property.value();
            if (value.length == 1) {
                map.put(property.key(), value[0]);
            } else {
                map.put(property.key(), Arrays.asList(value));
            }
        }
        return fromRawMap(map);
    }


    @Override
    public Config fromEnvironment() {
        return fromMap(System.getenv());
    }


    @Override
    public Config fromSystem() {
        return fromMap(mapOf(System.getProperties()));
    }



    @Override
    public Config fromPath(Path path, Charset charset) {
        return fromURI(path.toUri(),charset);
    }


    @Override
    public Config fromURI(URI uri, Charset charset) {
        try {
            return fromRawMap(readURL(uri.toURL(), charset));
        } catch (MalformedURLException e) {
            throw new ConfigException(e);
        }
    }


    @Override
    public Config fromResource(String resource, Charset charset, ClassLoader classLoader) {
        URL url = classLoader.getResource(resource);
        if (url == null) {
            throw new ConfigException("Cannot find resource "+resource+" using classloader "+classLoader);
        }
        return fromRawMap(readURL(url, charset));
    }


    @Override
    public Config fromProperties(Properties properties) {
        return fromMap(mapOf(properties));
    }


    @Override
    public Config fromMap(Map<String,String> map) {
        return new MapBasedConfig(this, map, List.of());
    }


    @Override
    public Config accordingDefinitions(Collection<PropertyDefinition> definitions) {
        Map<String,String> defaultValues = definitions
            .stream()
            .filter(definition -> definition.defaultValue().isPresent())
            .collect(Collectors.toMap(
                PropertyDefinition::property,
                definition->definition.defaultValue().orElseThrow(),
                (a,b) -> b,
                LinkedHashMap::new
            ));
        return new MapBasedConfig(this, defaultValues, definitions);
    }


    @Override
    public Config accordingDefinitionsFromPath(Path path, Charset charset) {
        return accordingDefinitionsFromURI(path.toUri(),charset);
    }


    @Override
    public Config accordingDefinitionsFromURI(URI uri, Charset charset) {
        try {
            return accordingDefinitions(definitionsFromURL(uri.toURL(),charset));
        } catch (MalformedURLException e) {
            throw new ConfigException(e);
        }
    }


    @Override
    public Config accordingDefinitionsFromResource(String resource, Charset charset, ClassLoader classLoader) {
        URL url = classLoader.getResource(resource);
        if (url == null) {
            throw new ConfigException("Cannot find resource " + resource + " using classloader " + classLoader);
        }
        return accordingDefinitions(definitionsFromURL(url,charset));
    }



    private Map<String, ?> readURL(URL url, Charset charset) {
        String file = url.getFile().toLowerCase();
        String extension = file.substring(file.lastIndexOf('.')+1);
        ExternalReader reader = externalReaderFactory.readerForExtension(extension);
        return reader.readFlat(url, charset);
    }


    private Collection<PropertyDefinition> definitionsFromURL(URL url, Charset charset) {
        String file = url.getFile().toLowerCase();
        String extension = file.substring(file.lastIndexOf('.')+1);
        return propertyDefinitionParser.read(extension, url, charset);
    }


    private Config fromRawMap(Map<String,?> map) {
        return new MapBasedConfig(this, map, List.of());
    }


    private Map<String, String> mapOf(Properties properties) {
        Map<String,String> map = new HashMap<>();
        properties.forEach((key,value)->map.put(String.valueOf(key), String.valueOf(value)));
        return map;
    }


}
