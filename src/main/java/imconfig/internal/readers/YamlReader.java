package imconfig.internal.readers;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class YamlReader extends JacksonReader {

    public YamlReader() {
        super(new YAMLMapper());
    }

}
