package imconfig.internal;

import imconfig.*;
import imconfig.internal.readers.*;
import java.util.*;

public class ExternalReaderFactory {

    public static final String JACKSON_DATABIND = "com.fasterxml.jackson.core:jackson-databind";

    private final Map<String,ExternalReader> externalReaders = new HashMap<>();




    public ExternalReader readerForExtension(String extension) {
        switch (extension) {
            case "properties":
                return externalReaders.computeIfAbsent(extension, x->new PropertiesReader());
            case "json":
                return externalReaders.computeIfAbsent(extension, x->newJsonReader());
            case "xml":
                return externalReaders.computeIfAbsent(extension, x->newXmlReader());
            case "yaml": case "yml":
                return externalReaders.computeIfAbsent(extension, x->newYamlReader());
            case "toml":
                return externalReaders.computeIfAbsent(extension, x->newTomlReader());
            default:
                throw new ConfigException("No reader implemented for resource of type "+extension);
        }
    }


    private JsonReader newJsonReader() {
        try {
            return new JsonReader();
        } catch (NoClassDefFoundError e) {
            throw new OptionalOperationException(
                e,
                JACKSON_DATABIND
            );
        }
    }

    private YamlReader newYamlReader() {
        try {
            return new YamlReader();
        } catch (NoClassDefFoundError e) {
            throw new OptionalOperationException(
                e,
                JACKSON_DATABIND,
                "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml"
            );
        }
    }

    private XmlReader newXmlReader() {
        try {
            return new XmlReader();
        } catch (NoClassDefFoundError e) {
            throw new OptionalOperationException(
                e,
                JACKSON_DATABIND,
                "com.fasterxml.jackson.dataformat:jackson-dataformat-xml"
            );
        }
    }


    private TomlReader newTomlReader() {
        try {
            return new TomlReader();
        } catch (NoClassDefFoundError e) {
            throw new OptionalOperationException(
                e,
                JACKSON_DATABIND,
                "com.fasterxml.jackson.dataformat:jackson-dataformat-toml"
            );
        }
    }

}
