package imconfig;

public class OptionalOperationException extends ConfigException {


    public OptionalOperationException(Throwable e, String... dependencies) {
        super(
            "Some optional libraries required for the operation are not present:\n    -"+
                String.join("\n    -",dependencies),
            e
        );
    }

}
