package imconfig.test;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import imconfig.PropertyDefinition;
import imconfig.internal.PropertyDefinitionParser;

public class TestDefinitionParser {

    private final PropertyDefinitionParser parser = new PropertyDefinitionParser();

    @BeforeClass
    public static void grantPrivateAccess() {
        PropertyDefinition.class.getModule().addOpens("imconfig", Assertions.class.getModule());
        PropertyDefinition.class.getModule().addOpens("imconfig.types", Assertions.class.getModule());
        Assertions.setAllowExtractingPrivateFields(true);
        Assertions.setAllowComparingPrivateFields(true);
    }


    @Test
    public void testParseDefinitionFile() throws IOException {

        File file = Path.of("src","test","resources","definition.yaml").toFile();

        try (InputStream inputStream = new FileInputStream(file)) {
            var contents = parser.read(inputStream).iterator();

            assertThat(contents.next())
            .hasFieldOrPropertyWithValue("property","defined.property.required")
            .hasFieldOrPropertyWithValue("description", "This is a test property that is required")
            .hasFieldOrPropertyWithValue("required", true)
            .hasFieldOrPropertyWithValue("type","text");

            assertThat(contents.next())
            .hasFieldOrPropertyWithValue("property","defined.property.regex-text")
            .hasFieldOrPropertyWithValue("type", "text")
            .extracting("propertyType")
                .hasFieldOrPropertyWithValue("pattern", "A\\d\\dB");

            assertThat(contents.next())
            .hasFieldOrPropertyWithValue("property","defined.property.with-default-value")
            .hasFieldOrPropertyWithValue("description", "This is a property with a default value")
            .hasFieldOrPropertyWithValue("defaultValue", Optional.of("5"))
            .hasFieldOrPropertyWithValue("type", "integer");


            assertThat(contents.next())
            .hasFieldOrPropertyWithValue("property","defined.property.min-max-number")
            .hasFieldOrPropertyWithValue("type", "integer")
            .extracting("propertyType")
                .hasFieldOrPropertyWithValue("min", 2L)
                .hasFieldOrPropertyWithValue("max", 3L);

            assertThat(contents.next())
            .hasFieldOrPropertyWithValue("property","defined.property.enumeration")
            .hasFieldOrPropertyWithValue("type", "enum")
            .extracting("propertyType")
                .hasFieldOrPropertyWithValue("values", List.of("red","yellow","orange"));
        }
    }

}
