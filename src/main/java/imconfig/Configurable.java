package imconfig;

/**
 * A convenient interface for objects that can apply a configuration to themselves.
 * <p>
 * The main difference with {@link Configurer} is that, while a `Configurer` is a third
 * object that applies a configuration to others, a `Configurable` applies the configuration
 * to itself.
 */
public interface Configurable {

    /**
     * Apply the given configuration
     * @param configuration
     */
    void configure(Configuration configuration);
}
