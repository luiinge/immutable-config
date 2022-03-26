package imconfig.internal.readers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlReader extends JacksonReader {

    public XmlReader() {
        super(new XmlMapper());
    }

}
