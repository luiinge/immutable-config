/*
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig.internal;


import imconfig.*;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;


public class ApacheConfiguration2 extends AbstractConfiguration {

    protected final org.apache.commons.configuration2.Configuration conf;


    protected ApacheConfiguration2(
        ConfigurationFactory builder,
        Map<String, PropertyDefinition> definitions,
        org.apache.commons.configuration2.Configuration conf
    ) {
        super(builder, definitions);
        this.conf = conf;
    }



    protected ApacheConfiguration2(
        ConfigurationFactory builder,
        org.apache.commons.configuration2.Configuration conf
    ) {
        super(builder, Map.of());
        this.conf = conf;
    }



    @Override
    public Configuration withPrefix(String keyPrefix) {
        BaseConfiguration innerConf = prepare(new BaseConfiguration());
        conf.getKeys().forEachRemaining(
            key -> innerConf.addProperty(keyPrefix + "." + key, conf.getProperty(key))
        );
        return new ApacheConfiguration2(builder, definitions, innerConf);
    }


    @Override
    public Configuration filtered(String keyPrefix) {
        BaseConfiguration innerConf = prepare(new BaseConfiguration());
        conf.getKeys(keyPrefix).forEachRemaining(key -> {
            if (key.startsWith(keyPrefix)) {
                innerConf.addProperty(key, conf.getProperty(key));
            }
        });
        return new ApacheConfiguration2(builder, definitions, innerConf);
    }


    @Override
    public Configuration inner(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isEmpty()) {
            return this;
        }
        return new ApacheConfiguration2(builder, definitions, conf.subset(keyPrefix));
    }


    @Override
    public boolean isEmpty() {
        return conf.isEmpty();
    }


    @Override
    public boolean hasProperty(String key) {
        return conf.containsKey(key);
    }


    @Override
    public Iterable<String> keys() {
        return keyList();
    }


    @Override
    public Iterator<String> keyIterator() {
        return conf.getKeys();
    }


    @Override
    public Stream<String> keyStream() {
        return keyList().stream();
    }


    private List<String> keyList() {
        List<String> keys = new ArrayList<>();
        conf.getKeys().forEachRemaining(keys::add);
        return keys;
    }


    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        var definition = definitions.get(key);
        String raw = conf.getString(key);
        boolean empty = (raw == null || "".equals(raw));
        if (definition != null) {
            var value = empty ? definition.defaultValue().orElse(null) : raw;
            if (value != null) {
                return Optional.of(convert(value, type));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.ofNullable(empty ? null : conf.get(type, key));
        }
    }


    @Override
    public <T> List<T> getList(String key, Class<T> type) {
        return Optional.ofNullable(conf.getList(type, key)).orElse(Collections.emptyList());
    }


    @Override
    public <T> Set<T> getSet(String key, Class<T> type) {
        return new HashSet<>(getList(key, type));
    }


    @Override
    public <T> Stream<T> getStream(String key, Class<T> type) {
        return getList(key, type).stream();
    }


    @Override
    public Properties asProperties() {
        Properties properties = new Properties();
        conf.getKeys().forEachRemaining(key -> properties.put(key, conf.getString(key)));
        return properties;
    }


    @Override
    public Map<String, String> asMap() {
        Map<String, String> map = new LinkedHashMap<>();
        conf.getKeys().forEachRemaining(key -> map.put(key, conf.getString(key)));
        return map;
    }


    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("configuration:\n---------------\n");
        conf.getKeys().forEachRemaining(key -> {
            final String[] values = conf.getStringArray(key);
            String value = "<undefined>";
            if (values.length == 1) {
                value = values[0];
            } else if (values.length > 1) {
                value = Arrays.toString(values);
            }
            string
                .append(key)
                .append(" : ")
                .append(value)
                .append("\n");
        });
        return string.append("---------------").toString();
    }


    @Override
    public void forEach(BiConsumer<String, String> consumer) {
        conf.getKeys().forEachRemaining(key -> consumer.accept(key, conf.get(String.class, key)));
    }




    private <T> T convert(String raw, Class<T> type) {
        var abstractConf = (org.apache.commons.configuration2.AbstractConfiguration)conf;
        return abstractConf.getConversionHandler().to(raw, type, abstractConf.getInterpolator());
    }


    private <T extends org.apache.commons.configuration2.AbstractConfiguration> T prepare(T abstractConfiguration) {
        if (builder.hasMultivalueSeparator()) {
            abstractConfiguration.setListDelimiterHandler(new DefaultListDelimiterHandler(builder.multivalueSeparator()));
        }
        return abstractConfiguration;
    }

}
