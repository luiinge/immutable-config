package imconfig.internal;

import imconfig.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unchecked")
public class MapBasedConfig extends AbstractConfig {

    private final Map<String,?> values;

    MapBasedConfig(
        ConfigFactory builder,
        Map<String,?> values,
        Map<String,PropertyDefinition> definitions
    ) {
        super(builder, definitions);
        this.values = Map.copyOf(values);
    }

    MapBasedConfig(
        ConfigFactory builder,
        Map<String,?> values,
        Collection<PropertyDefinition> definitions
    ) {
        this(builder,values,asMap(definitions));
    }


    @Override
    public Config filtered(Predicate<String> filter) {
        var newValues = values.entrySet().stream()
            .filter(it -> filter.test(it.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new MapBasedConfig(builder, newValues, definitions);
    }


    @Override
    public Config alteringKeys(UnaryOperator<String> alterOperation) {
        var newValues = values.entrySet().stream()
            .map(it -> Map.entry(alterOperation.apply(it.getKey()), it.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new MapBasedConfig(builder, newValues, definitions);
    }


    @Override
    public Config prefixing(String keyPrefix) {
        var newValues = values.entrySet().stream()
            .map(it -> Map.entry(keyPrefix+"."+it.getKey(), it.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new MapBasedConfig(builder, newValues, definitions);
    }


    @Override
    public Config inner(String keyPrefix) {
        return this
            .filtered(it -> it.startsWith(keyPrefix+"."))
            .alteringKeys(it -> it.substring(it.indexOf(keyPrefix+".")+keyPrefix.length()+1));
    }


    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }


    @Override
    public boolean hasProperty(String key) {
        return values.containsKey(key);
    }


    @Override
    public boolean hasMultivaluedProperty(String key) {
        return values.get(key) instanceof List;
    }


    @Override
    public Iterable<String> keys() {
        return List.copyOf(values.keySet());
    }


    @Override
    public Iterator<String> keyIterator() {
        return values.keySet().iterator();
    }


    @Override
    public Stream<String> keyStream() {
        return values.keySet().stream();
    }


    @Override
    public Optional<String> get(String key) {
        Object value = values.get(key);
        if (value == null) {
            return Optional.empty();
        } else if (value instanceof List) {
            return ((List<?>)value).stream().findFirst().map(String::valueOf);
        } else {
            return Optional.of((String)value);
        }
    }


    @Override
    public String get(String key, String fallback) {
        return get(key).orElse(fallback);
    }


    @Override
    public <T> Optional<T> get(String key, Function<String,T> mapper) {
        return get(key).map(mapper);
    }


    @Override
    public <T> T get(String key, Function<String, T> mapper, T fallback) {
        return get(key, mapper).orElse(fallback);
    }


    @Override
    public List<String> getList(String key) {
        Object value = values.get(key);
        if (value == null) {
            return List.of();
        } else if (value instanceof List) {
            return (List<String>) value;
        } else {
            return List.of((String)value);
        }
    }


    @Override
    public <T> List<T> getList(String key, Function<String, T> mapper) {
        return getStream(key).map(mapper).collect(Collectors.toUnmodifiableList());
    }


    @Override
    public Set<String> getSet(String key) {
        return Set.copyOf(getList(key));
    }


    @Override
    public <T> Set<T> getSet(String key, Function<String, T> mapper) {
        return getStream(key).map(mapper).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Stream<String> getStream(String key) {
        Object value = values.get(key);
        if (value == null) {
            return Stream.empty();
        } else if (value instanceof List) {
            return ((List<?>)value).stream().map(String::valueOf);
        } else {
            return Stream.of((String)value);
        }
    }


    @Override
    public Properties asProperties() {
        Properties properties = new Properties();
        values.forEach((key,value)-> {
            if (value instanceof List) {
                properties.setProperty(key, String.join(",", (List<String>)value));
            } else {
                properties.setProperty(key, (String) value);
            }
        });
        return properties;
    }


    @Override
    public Map<String, String> asMap() {
        Map<String,String> map = new LinkedHashMap<>();
        values.forEach((key,value)-> {
            if (value instanceof List) {
                map.put(key, String.join(",", (List<String>)value));
            } else {
                map.put(key, (String) value);
            }
        });
        return map;
    }


    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        values.keySet().stream().sorted().forEach(key -> {
            Object value = values.get(key);
            string.append(key).append(" : ");
            if (value instanceof List) {
                string.append("\n");
                ((List<?>)value).forEach(item -> string.append("  - ").append(item).append("\n"));
            } else {
                string.append(value).append("\n");
            }
        });
        return string.toString();
    }




    private static Map<String,PropertyDefinition> asMap(Collection<PropertyDefinition> definitions) {
        return definitions.stream().collect(Collectors.toMap(
            PropertyDefinition::property,
            Function.identity(),
            (a,b)->b,
            LinkedHashMap::new
        ));
    }




}
