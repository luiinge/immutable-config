package imconfig.internal;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import imconfig.ConfigurationException;

public class ClasspathURLStreamHandler extends URLStreamHandler {

    private final ClassLoader classLoader;

    public ClasspathURLStreamHandler(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new ConfigurationException("A class loader is required for 'classpath:' schema");
        }
        this.classLoader = classLoader;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        var path = url.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        final URL resourceUrl = classLoader.getResource(path);
        return resourceUrl.openConnection();
    }

}