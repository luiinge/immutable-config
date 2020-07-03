/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig;

/**
 * Multi-purpose runtime exception for any error occurred during the creation of a
 * new configuration.
 */
public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 7175876124782335084L;


    public ConfigurationException(Throwable throwable) {
        super(throwable);
    }


    public ConfigurationException(String message) {
        super(message);
    }
}
