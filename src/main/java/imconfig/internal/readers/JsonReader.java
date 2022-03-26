package imconfig.internal.readers;

import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonReader extends JacksonReader {

    public JsonReader() {
        super(new JsonMapper());
    }

}
