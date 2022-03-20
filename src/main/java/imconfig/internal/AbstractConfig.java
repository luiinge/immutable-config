/*
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig.internal;



import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;
import imconfig.*;


public abstract class AbstractConfig implements Config {

    protected final ConfigFactory builder;
    protected final Map<String,PropertyDefinition> definitions;


    protected AbstractConfig(
        ConfigFactory builder,
        Map<String,PropertyDefinition> definitions
    ) {
        this.builder = builder;
        this.definitions = new LinkedHashMap<>(definitions);
    }


    @Override
    public Config append(Config otherConfig) {
        return builder.merge(this, otherConfig);
    }



    @Override
    public Map<String, PropertyDefinition> getDefinitions() {
        return new LinkedHashMap<>(definitions);
    }


    @Override
    public Optional<PropertyDefinition> getDefinition(String key) {
        return Optional.ofNullable(definitions.get(key));
    }


    @Override
    public boolean hasDefinition(String key) {
        return definitions.containsKey(key);
    }


    @Override
    public List<String> validations(String key) {
        return getDefinition(key).map(definition -> validations(key, definition)).orElseGet(List::of);
    }


    private List<String> validations(String key, PropertyDefinition definition) {
        List<String> values = definition.multivalue() ?
            getList(key) :
            get(key).map(List::of).orElseGet(List::of);
        return values
        .stream()
        .map(definition::validate)
        .flatMap(Optional::stream)
        .collect(toList());
    }


    @Override
    public Map<String,List<String>> validations() {
        var invalidValues = keyStream()
            .map(key -> Map.entry(key, validations(key)))
            .filter(entry -> !entry.getValue().isEmpty());
        var missingValues = definitions.values().stream()
            .filter(PropertyDefinition::required)
            .filter(it->!this.hasProperty(it.property()))
            .map(it->Map.entry(
                it.property(),
                it.validate(null).map(List::of).orElseGet(List::of)
            ));
        return Stream.concat(invalidValues,missingValues)
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    @Override
    public Config validate() {
        var validations = validations();
        if (!validations.isEmpty()) {
           var message = validations.entrySet().stream()
               .map(entry -> String.format(
                   "%s : %s",
                   entry.getKey(),
                   String.join("\n"+(" ").repeat(entry.getKey().length() + 3),  entry.getValue())
               ))
               .collect(Collectors.joining("\n\t", "The configuration contains one or more invalid values:\n\t",""));
           throw new ConfigException(message);
        }
        return this;
    }


    @Override
    public Config accordingDefinitions(Collection<PropertyDefinition> definitions) {
        return builder.merge(this, builder.accordingDefinitions(definitions));
    }



    @Override
    public String getDefinitionsToString() {
       return getDefinitions().values().stream()
           .map(PropertyDefinition::toString)
           .collect(Collectors.joining("\n"));
    }



}
