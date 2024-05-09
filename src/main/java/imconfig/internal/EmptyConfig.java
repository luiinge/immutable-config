package imconfig.internal;

import imconfig.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class EmptyConfig implements Config {

    final static Config INSTANCE = new EmptyConfig();


    @Override
    public Config alteringKeys(UnaryOperator<String> alterOperation) {
        return this;
    }


    @Override
    public Config filtered(Predicate<String> filter) {
        return this;
    }


    @Override
    public Config inner(String keyPrefix) {
        return this;
    }


    @Override
    public Config prefixing(String prefix) {
        return this;
    }


    @Override
    public boolean isEmpty() {
        return true;
    }


    @Override
    public boolean hasProperty(String key) {
        return false;
    }


    @Override
    public boolean hasMultivaluedProperty(String key) {
        return false;
    }

    @Override
    public String key() {
        return null;
    }

    @Override
    public Iterable<String> keys() {
        return List.of();
    }


    @Override
    public Iterator<String> keyIterator() {
        return Collections.emptyIterator();
    }


    @Override
    public Stream<String> keyStream() {
        return Stream.empty();
    }


    @Override
    public Optional<String> get(String key) {
        return Optional.empty();
    }


    @Override
    public String get(String key, String fallback) {
        return fallback;
    }


    @Override
    public <T> Optional<T> get(String key, Function<String, T> mapper) {
        return Optional.empty();
    }


    @Override
    public <T> T get(String key, Function<String, T> mapper, T fallback) {
        return fallback;
    }


    @Override
    public List<String> getList(String key) {
        return List.of();
    }


    @Override
    public <T> List<T> getList(String key, Function<String, T> mapper) {
        return List.of();
    }


    @Override
    public Set<String> getSet(String key) {
        return Set.of();
    }


    @Override
    public <T> Set<T> getSet(String key, Function<String, T> mapper) {
        return Set.of();
    }


    @Override
    public Stream<String> getStream(String key) {
        return null;
    }


    @Override
    public Properties asProperties() {
        return new Properties();
    }


    @Override
    public Map<String, String> asMap() {
        return Map.of();
    }



   @Override
    public Config append(Config otherConfig) {
        return otherConfig;
    }



    @Override
    public boolean hasDefinition(String key) {
        return false;
    }


    @Override
    public List<String> validations(String key) {
        return List.of();
    }


    @Override
    public Optional<PropertyDefinition> getDefinition(String key) {
        return Optional.empty();
    }


    @Override
    public Map<String, PropertyDefinition> getDefinitions() {
        return Map.of();
    }


    @Override
    public Map<String, List<String>> validations() {
        return Map.of();
    }


    @Override
    public Config validate() throws ConfigException {
        return this;
    }


    @Override
    public Config accordingDefinitions(Collection<PropertyDefinition> definitions) {
        return Config.factory().accordingDefinitions(definitions);
    }



    @Override
    public String getDefinitionsToString() {
        return "";
    }

}
