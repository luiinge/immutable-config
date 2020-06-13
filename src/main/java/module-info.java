module imconfig {

    exports imconfig;

    requires org.apache.commons.configuration2;
    requires commons.beanutils;
    requires org.yaml.snakeyaml;
    requires org.slf4j;
    requires org.apache.commons.lang3;

    uses imconfig.ConfigurationBuilder;

    provides imconfig.ConfigurationBuilder with imconfig.internal.ApacheConfiguration2Builder;

}