import imconfig.ConfigFactory;

/**
 This module provides a simple interface in order to load and consume configurations,
 which are mainly a set of valued properties that can be parsed from a wide range of sources
 (such as JSON, YAML or .properties files, Map and Properties objects, or even plain pairs of
 values) to specific Java types.
 <p>
 The primary focus of the module is null-safety, immutability, and fluency.
 */
module imconfig {

    exports imconfig;

    requires org.slf4j;

    requires static com.fasterxml.jackson.core;
    requires static com.fasterxml.jackson.databind;
    requires static com.fasterxml.jackson.dataformat.xml;
    requires static com.fasterxml.jackson.dataformat.yaml;
    requires static com.fasterxml.jackson.dataformat.toml;

    uses ConfigFactory;

    provides ConfigFactory with imconfig.internal.MapBasedConfigurationFactory;
}