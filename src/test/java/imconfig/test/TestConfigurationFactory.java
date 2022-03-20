/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig.test;


import imconfig.*;
import java.nio.charset.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import imconfig.AnnotatedConfig;


public class TestConfigurationFactory {

    private static final ClassLoader CLASS_LOADER = TestConfigurationFactory.class.getClassLoader();
    public static final Charset UTF8 = StandardCharsets.UTF_8;
    
    private static final String KEY_ENV = "test.env.key";
    private static final String VAL_ENV = "Test Environment Value";

    private static final String KEY_STRING = "properties.test.key.string";
    private static final String KEY_STRINGS = "properties.test.key.strings";
    private static final String KEY_BOOL = "properties.test.key.bool";
    private static final String KEY_BOOLS = "properties.test.key.bools";
    private static final String KEY_INTEGER = "properties.test.key.integer";
    private static final String KEY_INTEGERS = "properties.test.key.integers";
    private static final String KEY_LONG = "properties.test.key.long";
    private static final String KEY_LONGS = "properties.test.key.longs";
    private static final String KEY_DOUBLE = "properties.test.key.double";
    private static final String KEY_DOUBLES = "properties.test.key.doubles";
    private static final String KEY_FLOAT = "properties.test.key.float";
    private static final String KEY_FLOATS = "properties.test.key.floats";
    private static final String KEY_BIGDECIMAL = "properties.test.key.bigdecimal";
    private static final String KEY_BIGDECIMALS = "properties.test.key.bigdecimals";
    private static final String KEY_BIGINTEGER = "properties.test.key.biginteger";
    private static final String KEY_BIGINTEGERS = "properties.test.key.bigintegers";

    private static final String VAL_STRING = "Properties Test String Value";
    private static final String VAL_STRINGS_1 = "Properties Array Value 1";
    private static final String VAL_STRINGS_2 = "Properties Array Value 2";
    private static final String VAL_BOOL = "true";
    private static final String VAL_BOOLS_1 = "true";
    private static final String VAL_BOOLS_2 = "false";
    private static final String VAL_BOOLS_3 = "true";
    private static final String VAL_INTEGER = "77";
    private static final String VAL_INTEGERS_1 = "77";
    private static final String VAL_INTEGERS_2 = "79";
    private static final String VAL_INTEGERS_3 = "83";
    private static final String VAL_LONG = "54353";
    private static final String VAL_LONGS_1 = "54353";
    private static final String VAL_LONGS_2 = "65256";
    private static final String VAL_LONGS_3 = "98432";
    private static final String VAL_DOUBLE = "3.45";
    private static final String VAL_DOUBLES_1 = "3.45";
    private static final String VAL_DOUBLES_2 = "6.76";
    private static final String VAL_DOUBLES_3 = "9.32";
    private static final String VAL_FLOAT = "6.98";
    private static final String VAL_FLOATS_1 = "6.98";
    private static final String VAL_FLOATS_2 = "2.23";
    private static final String VAL_FLOATS_3 = "1.24";
    private static final String VAL_BIGDECIMAL = "755.87";
    private static final String VAL_BIGDECIMALS_1 = "755.87";
    private static final String VAL_BIGDECIMALS_2 = "876.43";
    private static final String VAL_BIGDECIMALS_3 = "908.32";
    private static final String VAL_BIGINTEGER = "123456789";
    private static final String VAL_BIGINTEGERS_1 = "123456789";
    private static final String VAL_BIGINTEGERS_2 = "543987532";
    private static final String VAL_BIGINTEGERS_3 = "549874348";


    @Rule
    public final EnvironmentVariables env = new EnvironmentVariables();

    private ConfigFactory factory;



    @AnnotatedConfig({
        @Property(key = KEY_ENV, value = VAL_ENV),
        @Property(key = KEY_STRING, value = VAL_STRING),
        @Property(key = KEY_STRINGS, value = { VAL_STRINGS_1, VAL_STRINGS_2 }),
        @Property(key = KEY_BOOL, value = VAL_BOOL),
        @Property(key = KEY_BOOLS, value = { VAL_BOOLS_1, VAL_BOOLS_2, VAL_BOOLS_3 }),
        @Property(key = KEY_INTEGER, value = VAL_INTEGER),
        @Property(key = KEY_INTEGERS, value = { VAL_INTEGERS_1, VAL_INTEGERS_2,
                        VAL_INTEGERS_3 }),
        @Property(key = KEY_LONG, value = VAL_LONG),
        @Property(key = KEY_LONGS, value = { VAL_LONGS_1, VAL_LONGS_2, VAL_LONGS_3 }),
        @Property(key = KEY_FLOAT, value = VAL_FLOAT),
        @Property(key = KEY_FLOATS, value = { VAL_FLOATS_1, VAL_FLOATS_2,
                        VAL_FLOATS_3 }),
        @Property(key = KEY_DOUBLE, value = VAL_DOUBLE),
        @Property(key = KEY_DOUBLES, value = { VAL_DOUBLES_1, VAL_DOUBLES_2,
                        VAL_DOUBLES_3 }),
        @Property(key = KEY_BIGDECIMAL, value = VAL_BIGDECIMAL),
        @Property(key = KEY_BIGDECIMALS, value = {
                        VAL_BIGDECIMALS_1,
                        VAL_BIGDECIMALS_2,
                        VAL_BIGDECIMALS_3
        }),
        @Property(key = KEY_BIGINTEGER, value = VAL_BIGINTEGER),
        @Property(key = KEY_BIGINTEGERS, value = {
                        VAL_BIGINTEGERS_1,
                        VAL_BIGINTEGERS_2,
                        VAL_BIGINTEGERS_3
        }),
        @Property(key = "properties2.test2.key.string", value = VAL_STRING)
    })
    public static class ConfAnnotatedProps {    }




    @Before
    public void prepare() {
        factory = ConfigFactory.instance();
        env.set(KEY_ENV, VAL_ENV);
    }


    @Test
    public void createConfigurationFromEnvironmentProperties() {
        Config conf = factory.fromEnvironment();
        assertEnvironmentPropertiesExist(conf);
    }


    @Test
    public void createConfigurationFromYAMLFile() {
        Config conf = factory.fromResource("test-conf.yaml", UTF8, CLASS_LOADER);
        assertExpectedPropertiesExist(conf);

    }


    @Test
    public void createConfigurationFromPropertiesFile() throws ConfigException {
        Config conf = factory.fromResource("test-conf.properties", UTF8, CLASS_LOADER);
        assertExpectedPropertiesExist(conf);
    }


    @Test
    public void createConfigurationFromJSONFile() throws ConfigException {
        Config conf = factory.fromResource("test-conf.json", UTF8, CLASS_LOADER);
        assertExpectedPropertiesExist(conf);
    }


    @Test
    public void createConfigurationFromXMLFile() throws ConfigException {
        Config conf = factory.fromResource("test-conf.xml", UTF8, CLASS_LOADER);
        assertExpectedPropertiesExist(conf);
    }


    @Test
    public void createConfigurationFromTOMLFile() throws ConfigException {
        Config conf = factory.fromResource("test-conf.toml", UTF8, CLASS_LOADER);
        assertExpectedPropertiesExist(conf);
    }


    @Test
    public void appendEnvironmentConfigurationWithPropertiesFile() throws ConfigException {
        ConfigFactory fact = Config.factory();
        Config conf = fact.fromEnvironment()
            .append(fact.fromResource("test-conf.properties", UTF8, CLASS_LOADER));
        assertExpectedPropertiesExist(conf);
        assertEnvironmentPropertiesExist(conf);
    }



    @Test
    public void createConfigurationFromAnnotatedClass() throws ConfigException {
        Config conf = factory.fromAnnotation(ConfAnnotatedProps.class);
        assertExpectedPropertiesExist(conf);
    }


    @Test
    public void appendProperty() {
        Config conf = factory
            .fromAnnotation(ConfAnnotatedProps.class)
            .append(factory.fromPairs("appended.property", "appendedValue"));
        assertExpectedPropertiesExist(conf);
        assertThat(conf.get("appended.property")).contains("appendedValue");
    }


    @Test(expected = ConfigException.class)
    public void cannotCreateConfigurationFromMalformedFile() throws ConfigException {
        factory.fromResource("malformed-conf.xml", UTF8, CLASS_LOADER);
    }


    @Test(expected = ConfigException.class)
    public void cannotCreateConfigurationFromFileWithUnrecognizedFormat() throws ConfigException {
        factory.fromResource("unrecognized-format.xyq", UTF8, CLASS_LOADER);
    }


    @Test(expected = ConfigException.class)
    public void cannotCreateConfigurationFromNonExistingFile() throws ConfigException {
        factory.fromResource("unexisting-file", UTF8, CLASS_LOADER);
    }


    @Test
    public void invokingToStringReturnsEveryValuedProperty() throws ConfigException {
        Assertions.assertThat(factory.fromAnnotation(ConfAnnotatedProps.class).toString())
            .isEqualTo(
                "properties.test.key.bigdecimal : 755.87\n" +
                "properties.test.key.bigdecimals : \n" +
                "  - 755.87\n" +
                "  - 876.43\n" +
                "  - 908.32\n" +
                "properties.test.key.biginteger : 123456789\n" +
                "properties.test.key.bigintegers : \n" +
                "  - 123456789\n" +
                "  - 543987532\n" +
                "  - 549874348\n" +
                "properties.test.key.bool : true\n" +
                "properties.test.key.bools : \n" +
                "  - true\n" +
                "  - false\n" +
                "  - true\n" +
                "properties.test.key.double : 3.45\n" +
                "properties.test.key.doubles : \n" +
                "  - 3.45\n" +
                "  - 6.76\n" +
                "  - 9.32\n" +
                "properties.test.key.float : 6.98\n" +
                "properties.test.key.floats : \n" +
                "  - 6.98\n" +
                "  - 2.23\n" +
                "  - 1.24\n" +
                "properties.test.key.integer : 77\n" +
                "properties.test.key.integers : \n" +
                "  - 77\n" +
                "  - 79\n" +
                "  - 83\n" +
                "properties.test.key.long : 54353\n" +
                "properties.test.key.longs : \n" +
                "  - 54353\n" +
                "  - 65256\n" +
                "  - 98432\n" +
                "properties.test.key.string : Properties Test String Value\n" +
                "properties.test.key.strings : \n" +
                "  - Properties Array Value 1\n" +
                "  - Properties Array Value 2\n" +
                "properties2.test2.key.string : Properties Test String Value\n" +
                "test.env.key : Test Environment Value\n"
            );
    }


    @Test
    public void testFilter() throws ConfigException {
        Config conf = factory
            .fromAnnotation(ConfAnnotatedProps.class)
            .filtered(it -> it.startsWith("properties2"));
        assertThat(conf.get("properties.test.key.string")).isEmpty();
        assertThat(conf.get("properties2.test2.key.string")).contains(VAL_STRING);
    }


    @Test
    public void testCompose() {
        Map<String, String> map1 = new HashMap<>();
        map1.put("property.a", "a");
        map1.put("property.b", "b");
        map1.put("property.c", "");
        map1.put("property.d","d");
        Map<String, String> map2 = new HashMap<>();
        map2.put("property.a", "aa");
        map2.put("property.c", "c");
        map2.put("property.d", "");

        Config conf1 = factory.fromMap(map1).append(factory.fromMap(map2));
        assertThat(conf1.get("property.a")).contains("aa");
        assertThat(conf1.get("property.b")).contains("b");
        assertThat(conf1.get("property.c")).contains("c");
        assertThat(conf1.get("property.d")).isNotEmpty();
        assertThat(conf1.getList("property.a")).hasSize(1);

        Config conf2 = factory.fromMap(map2).append(factory.fromMap(map1));
        assertThat(conf2.get("property.a")).contains("a");
        assertThat(conf2.get("property.b")).contains("b");
        assertThat(conf2.get("property.c")).contains("");
        assertThat(conf1.get("property.d")).contains("");
        assertThat(conf2.getList("property.a")).hasSize(1);

    }



    @Test
    public void invokingGetOnEmptyPropertyReturnsEmptyOptional() {
        Properties properties = new Properties();
        properties.setProperty("property.a","");
        properties.setProperty("property.b", "");
        properties.setProperty("property.c", "c");
        var conf = factory.fromProperties(properties);
        assertThat(conf.keys()).contains("property.a","property.b","property.c");
        assertThat(conf.hasProperty("property.a")).isTrue();
        assertThat(conf.hasProperty("property.b")).isTrue();
        assertThat(conf.hasProperty("property.c")).isTrue();
        assertThat(conf.get("property.a")).isNotEmpty();
        assertThat(conf.get("property.b")).contains("");
        assertThat(conf.get("property.c")).isNotEmpty();
    }



    @Test
    public void invokingGetListOnEmptyPropertyReturnsEmptyList() {
        var conf = factory.fromMap(Map.of(
            "property.a", "",
            "property.b", "",
            "property.c", "c"
        ));
        assertThat(conf.keys()).contains("property.a","property.b","property.c");
        assertThat(conf.hasProperty("property.a")).isTrue();
        assertThat(conf.hasProperty("property.b")).isTrue();
        assertThat(conf.hasProperty("property.c")).isTrue();
        assertThat(conf.getList("property.a")).isNotEmpty();
        assertThat(conf.getList("property.b")).isNotEmpty();
        assertThat(conf.getList("property.c")).isNotEmpty();
    }



    @Test
    public void deriveConfigurationAddingPrefix() {
        var conf = factory.fromPairs(
            "property.a", "",
            "property.b", "",
            "property.c", "c"
        ).prefixing("prefix");
        assertThat(conf.keys()).contains("prefix.property.a","prefix.property.b","prefix.property.c");
        assertThat(conf.hasProperty("prefix.property.a")).isTrue();
        assertThat(conf.hasProperty("prefix.property.b")).isTrue();
        assertThat(conf.hasProperty("prefix.property.c")).isTrue();
        assertThat(conf.getList("prefix.property.b")).isNotEmpty();
    }


    @Test
    public void transformConfigurationToMap() {
        var confAsMap = factory.fromMap(Map.of(
            "property.a", "",
            "property.b", "",
            "property.c", "c"
        )).asMap();
        assertThat(confAsMap).contains(
            Map.entry("property.a", ""),
            Map.entry("property.b", ""),
            Map.entry("property.c", "c")
        );
    }


    @Test
    public void transformConfigurationToProperties() {
        var confAsProperties = factory.fromMap(Map.of(
            "property.a", "",
            "property.b", "",
            "property.c", "c"
        )).asProperties();
        assertThat(confAsProperties).contains(
            Map.entry("property.a", ""),
            Map.entry("property.b", ""),
            Map.entry("property.c", "c")
        );
    }



    private void assertExpectedPropertiesExist(Config conf) {

        System.out.println(conf);

        Assertions.assertThat(conf.get(KEY_STRING)).contains(VAL_STRING);
        Assertions.assertThat(conf.getList(KEY_STRINGS)).containsExactlyInAnyOrder(
            VAL_STRINGS_1,
            VAL_STRINGS_2
        );
        Assertions.assertThat(conf.get(KEY_BOOL).map(Boolean::valueOf)).contains(true);
        Assertions.assertThat(conf.getList(KEY_BOOLS, Boolean::valueOf))
            .containsExactlyInAnyOrder(true, false, true);
        Assertions.assertThat(conf.get(KEY_INTEGER,Integer::valueOf)).contains(77);
        Assertions.assertThat(conf.getList(KEY_INTEGERS, Integer::valueOf))
            .containsExactlyInAnyOrder(77, 79, 83);
        Assertions.assertThat(conf.get(KEY_LONG,Long::valueOf)).contains(54353L);
        Assertions.assertThat(conf.getList(KEY_LONGS, Long::valueOf))
            .containsExactlyInAnyOrder(54353L, 65256L, 98432L);
        Assertions.assertThat(conf.get(KEY_DOUBLE, Double::valueOf)).contains(3.45);
        Assertions.assertThat(conf.getList(KEY_DOUBLES, Double::valueOf))
            .containsExactlyInAnyOrder(3.45, 6.76, 9.32);
        Assertions.assertThat(conf.get(KEY_FLOAT, Float::valueOf)).contains(6.98f);
        Assertions.assertThat(conf.getList(KEY_FLOATS, Float::valueOf))
            .containsExactlyInAnyOrder(6.98f, 2.23f, 1.24f);
        Assertions.assertThat(conf.get(KEY_BIGDECIMAL, BigDecimal::new).get())
            .isEqualByComparingTo(new BigDecimal(VAL_BIGDECIMAL));
        Assertions.assertThat(conf.getList(KEY_BIGDECIMALS, BigDecimal::new))
            .containsExactlyInAnyOrder(
                new BigDecimal(VAL_BIGDECIMALS_1),
                new BigDecimal(VAL_BIGDECIMALS_2),
                new BigDecimal(VAL_BIGDECIMALS_3)
            );
        Assertions.assertThat(conf.get(KEY_BIGINTEGER, BigInteger::new).get())
            .isEqualByComparingTo(new BigInteger(VAL_BIGINTEGER));
        Assertions.assertThat(conf.getList(KEY_BIGINTEGERS, BigInteger::new))
            .containsExactlyInAnyOrder(
                new BigInteger(VAL_BIGINTEGERS_1),
                new BigInteger(VAL_BIGINTEGERS_2),
                new BigInteger(VAL_BIGINTEGERS_3)
            );

        assertNullProperties(conf);
    }


    private void assertNullProperties(Config conf) {
        String nonExistingKey = "xxx";
        Assertions.assertThat(conf.get(nonExistingKey)).isEmpty();
        Assertions.assertThat(conf.getList(nonExistingKey)).isEmpty();
    }


    private void assertEnvironmentPropertiesExist(Config conf) {
        Assertions.assertThat(conf.get(KEY_ENV)).contains(VAL_ENV);
    }

}
