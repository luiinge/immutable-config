/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig;

/**
 * Multi-purpose runtime exception for any error occurred during the creation of a
 * new configuration.
 */
public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = 7175876124782335084L;


    public ConfigException(Throwable throwable) {
        super(throwable);
    }


    public ConfigException(String message) {
        super(message);
    }


    public ConfigException(String message, Throwable throwable) {
        super(message,throwable);
    }
}
