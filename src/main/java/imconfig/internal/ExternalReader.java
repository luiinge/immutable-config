package imconfig.internal;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Map;

public interface ExternalReader {

    Map<String,?> readFlat(URL url, Charset charset);

    Map<String, ?> readObject(URL url, Charset charset);

    default Reader reader(URL url, Charset charset) throws IOException {
        return new BufferedReader(new InputStreamReader(url.openStream(), charset));
    }

}
