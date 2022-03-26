/*
 * @author Luis Iñesta Gelabert - luiinge@gmail.com
 */
package imconfig.test;


import imconfig.*;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;


public class TestConfigurationFactoryDefinitions {


    private final ConfigFactory factory = ConfigFactory.instance();
    private final Path definitionPath = Path.of("src", "test", "resources", "definition.yaml");


    @Test
    public void testBuildEmptyConfigurationWithDefinitionFromURI() {
        var conf = factory.accordingDefinitionsFromURI(definitionPath.toUri(), StandardCharsets.UTF_8);
        assertConfiguration(conf);
    }

    @Test
    public void testBuildEmptyConfigurationWithDefinitionFromPath() {
        var conf = factory.accordingDefinitionsFromPath(definitionPath, StandardCharsets.UTF_8);
        assertConfiguration(conf);
    }


    @Test
    public void testAttachDefinitionFromURI() {
        var conf = factory.empty()
            .append(factory.accordingDefinitionsFromURI(definitionPath.toUri(), StandardCharsets.UTF_8));
        assertConfiguration(conf);
    }


    @Test
    public void testAttachDefinitionFromPath() {
        var conf = factory.empty()
            .append(factory.accordingDefinitionsFromPath(definitionPath, StandardCharsets.UTF_8));
        assertConfiguration(conf);
    }


    @Test
    public void testConfigurationValidation() {
        var conf = factory
            .fromPairs("defined.property.min-max-number", "6")
            .append(factory.accordingDefinitionsFromPath(definitionPath,StandardCharsets.UTF_8));
        assertThat(conf.validations("defined.property.min-max-number"))
        .contains("Invalid value '6', expected: Integer number between 2 and 3");
    }


    private void assertConfiguration(Config conf) {
        assertThat(conf.getDefinitions()).hasSize(6);
        assertThat(conf.getDefinition("defined.property.required")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.with-default-value")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.regex-text")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.min-max-number")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.enumeration")).isNotEmpty();
        assertThat(conf.getDefinition("defined.property.boolean")).isNotEmpty();
        assertThat(conf.getDefinition("undefined.property")).isEmpty();
        assertThat(conf.get("defined.property.regex-text")).isEmpty();
        assertThat(conf.get("defined.property.with-default-value", Integer::valueOf)).hasValue(5);
    }

}
