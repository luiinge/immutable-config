package imconfig.internal.readers;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;

public class TomlReader extends JacksonReader {

    public TomlReader() {
        super(new TomlMapper());
    }

}
