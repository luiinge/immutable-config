package imconfig.internal;

import java.io.*;
import java.net.*;

import static java.util.Objects.requireNonNull;

/**
 * This class implements the virtual protocol 'classpath:' to be used in URLs
 */
public class ClasspathURLStreamHandler extends URLStreamHandler {

    private final ClassLoader classLoader;

    public ClasspathURLStreamHandler(ClassLoader classLoader) {
        this.classLoader = requireNonNull(classLoader,"A class loader is required for 'classpath:' schema");
    }


    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        var path = url.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        final URL resourceUrl = classLoader.getResource(path);
        if (resourceUrl == null) {
            throw new FileNotFoundException("Cannot access to classpath resource "+url);
        }
        return resourceUrl.openConnection();
    }

}