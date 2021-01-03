/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package imconfig.test;


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

import imconfig.AnnotatedConfiguration;
import imconfig.Configuration;
import imconfig.ConfigurationFactory;
import imconfig.ConfigurationException;
import imconfig.Property;


public class TestConfigurationFactory {

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

    private ConfigurationFactory factory;



    @AnnotatedConfiguration({
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
        factory = ConfigurationFactory.instance();
        env.set(KEY_ENV, VAL_ENV);
    }


    @Test
    public void createConfigurationFromEnvironmentProperties() {
        Configuration conf = factory.fromEnvironment();
        assertEnvironmentPropertiesExist(conf);
    }


    @Test
    public void createConfigurationFromYAMLFile() {
        Configuration conf = factory.fromClasspathResource("test-conf.yaml");
        assertExpectedPropertiesExist(conf);

    }


    @Test
    public void createConfigurationFromPropertiesFile() throws ConfigurationException {
        Configuration conf = factory.fromClasspathResource("test-conf.properties");
        assertExpectedPropertiesExist(conf);
    }


    @Test
    public void createConfigurationFromJSONFile() throws ConfigurationException {
        Configuration conf = factory.fromClasspathResource("test-conf.json");
        assertExpectedPropertiesExist(conf);
    }


    @Test
    public void createConfigurationFromXMLFile() throws ConfigurationException {
        Configuration conf = factory.fromClasspathResource("test-conf.xml");
        assertExpectedPropertiesExist(conf);
    }


    @Test
    public void appendEnvironmentConfigurationWithPropertiesFile() throws ConfigurationException {
        Configuration conf = factory
            .fromEnvironment()
            .appendFromClasspathResource("test-conf.properties");
        assertExpectedPropertiesExist(conf);
        assertEnvironmentPropertiesExist(conf);
    }



    @Test
    public void createConfigurationFromAnnotatedClass() throws ConfigurationException {
        Configuration conf = factory.fromAnnotation(ConfAnnotatedProps.class);
        assertExpectedPropertiesExist(conf);
    }


    @Test
    public void appendProperty() {
        Configuration conf = factory
            .fromAnnotation(ConfAnnotatedProps.class)
            .appendProperty("appended.property", "appendedValue");
        assertExpectedPropertiesExist(conf);
        assertThat(conf.get("appended.property", String.class)).contains("appendedValue");
    }


    @Test(expected = ConfigurationException.class)
    public void cannotCreateConfigurationFromMalformedFile() throws ConfigurationException {
        factory.fromClasspathResource("malformed-conf.xml");
    }


    @Test(expected = ConfigurationException.class)
    public void cannotCreateConfigurationFromFileWithUnrecognizedFormat() throws ConfigurationException {
        factory.fromClasspathResource("unrecognized-format.xyq");
    }


    @Test(expected = ConfigurationException.class)
    public void cannotCreateConfigurationFromNonExistingFile() throws ConfigurationException {
        factory.fromClasspathResource("unexisting-file");
    }


    @Test
    public void invokingToStringReturnsEveryValuedProperty() throws ConfigurationException {
        Assertions.assertThat(factory.fromAnnotation(ConfAnnotatedProps.class).toString())
            .isEqualTo(
                "configuration:\n" +
                "---------------\n" +
                "test.env.key : Test Environment Value\n" +
                "properties.test.key.string : Properties Test String Value\n" +
                "properties.test.key.strings : [Properties Array Value 1, Properties Array Value 2]\n" +
                "properties.test.key.bool : true\n" +
                "properties.test.key.bools : [true, false, true]\n" +
                "properties.test.key.integer : 77\n" +
                "properties.test.key.integers : [77, 79, 83]\n" +
                "properties.test.key.long : 54353\n" +
                "properties.test.key.longs : [54353, 65256, 98432]\n" +
                "properties.test.key.float : 6.98\n" +
                "properties.test.key.floats : [6.98, 2.23, 1.24]\n" +
                "properties.test.key.double : 3.45\n" +
                "properties.test.key.doubles : [3.45, 6.76, 9.32]\n" +
                "properties.test.key.bigdecimal : 755.87\n" +
                "properties.test.key.bigdecimals : [755.87, 876.43, 908.32]\n" +
                "properties.test.key.biginteger : 123456789\n" +
                "properties.test.key.bigintegers : [123456789, 543987532, 549874348]\n" +
                "properties2.test2.key.string : Properties Test String Value\n" +
                "---------------"
            );
    }


    @Test
    public void testFilter() throws ConfigurationException {
        Configuration conf = factory
            .fromAnnotation(ConfAnnotatedProps.class)
            .filtered("properties2");
        Assertions.assertThat(conf.get("properties.test.key.string", String.class)).isEmpty();
        Assertions.assertThat(
            conf.get("properties2.test2.key.string", String.class).get()
        ).isEqualTo(VAL_STRING);
    }


    @Test
    public void testCompose() {
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("property.a", "a");
        map1.put("property.b", "b");
        map1.put("property.c", "");
        map1.put("property.d","d");
        HashMap<String, String> map2 = new HashMap<>();
        map2.put("property.a", "aa");
        map2.put("property.c", "c");
        map2.put("property.d", "");

        Configuration conf1 = factory.fromMap(map1).appendFromMap(map2);
        assertThat(conf1.get("property.a", String.class)).contains("aa");
        assertThat(conf1.get("property.b", String.class)).contains("b");
        assertThat(conf1.get("property.c", String.class)).contains("c");
        assertThat(conf1.get("property.d", String.class)).contains("d");
        assertThat(conf1.getList("property.a", String.class)).hasSize(1);

        Configuration conf2 = factory.fromMap(map2).appendFromMap(map1);
        assertThat(conf2.get("property.a", String.class)).contains("a");
        assertThat(conf2.get("property.b", String.class)).contains("b");
        assertThat(conf2.get("property.c", String.class)).contains("c");
        assertThat(conf1.get("property.d", String.class)).contains("d");
        assertThat(conf2.getList("property.a", String.class)).hasSize(1);

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
        assertThat(conf.get("property.a", String.class)).isEmpty();
        assertThat(conf.get("property.b", Integer.class)).isEmpty();
        assertThat(conf.get("property.c", String.class)).isNotEmpty();
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
        assertThat(conf.getList("property.a", String.class)).isEmpty();
        assertThat(conf.getList("property.b", Integer.class)).isEmpty();
        assertThat(conf.getList("property.c",String.class)).isNotEmpty();
    }



    @Test
    public void deriveConfigurationAddingPrefix() {
        var conf = factory.fromPairs(
            "property.a", "",
            "property.b", "",
            "property.c", "c"
        ).withPrefix("prefix");
        assertThat(conf.keys()).contains("prefix.property.a","prefix.property.b","prefix.property.c");
        assertThat(conf.hasProperty("prefix.property.a")).isTrue();
        assertThat(conf.hasProperty("prefix.property.b")).isTrue();
        assertThat(conf.hasProperty("prefix.property.c")).isTrue();
        assertThat(conf.get("prefix.property.a", String.class)).isEmpty();
        assertThat(conf.get("prefix.property.a", Integer.class)).isEmpty();
        assertThat(conf.getList("prefix.property.b",String.class)).isEmpty();
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



    private void assertExpectedPropertiesExist(Configuration conf) {

        System.out.println(conf);

        Assertions.assertThat(conf.get(KEY_STRING, String.class).get()).isEqualTo(VAL_STRING);
        Assertions.assertThat(conf.getList(KEY_STRINGS, String.class)).containsExactlyInAnyOrder(
            VAL_STRINGS_1,
            VAL_STRINGS_2
        );
        Assertions.assertThat(conf.get(KEY_BOOL, Boolean.class).get()).isEqualTo(true);
        Assertions.assertThat(conf.getList(KEY_BOOLS, Boolean.class))
            .containsExactlyInAnyOrder(true, false, true);
        Assertions.assertThat(conf.get(KEY_INTEGER, Integer.class).get()).isEqualTo(77);
        Assertions.assertThat(conf.getList(KEY_INTEGERS, Integer.class))
            .containsExactlyInAnyOrder(77, 79, 83);
        Assertions.assertThat(conf.get(KEY_LONG, Long.class).get()).isEqualTo(54353L);
        Assertions.assertThat(conf.getList(KEY_LONGS, Long.class))
            .containsExactlyInAnyOrder(54353L, 65256L, 98432L);
        Assertions.assertThat(conf.get(KEY_DOUBLE, Double.class).get()).isEqualTo(3.45);
        Assertions.assertThat(conf.getList(KEY_DOUBLES, Double.class))
            .containsExactlyInAnyOrder(3.45, 6.76, 9.32);
        Assertions.assertThat(conf.get(KEY_FLOAT, Float.class).get()).isEqualTo(6.98f);
        Assertions.assertThat(conf.getList(KEY_FLOATS, Float.class))
            .containsExactlyInAnyOrder(6.98f, 2.23f, 1.24f);
        Assertions.assertThat(conf.get(KEY_BIGDECIMAL, BigDecimal.class).get())
            .isEqualByComparingTo(new BigDecimal(VAL_BIGDECIMAL));
        Assertions.assertThat(conf.getList(KEY_BIGDECIMALS, BigDecimal.class))
            .containsExactlyInAnyOrder(
                new BigDecimal(VAL_BIGDECIMALS_1),
                new BigDecimal(VAL_BIGDECIMALS_2),
                new BigDecimal(VAL_BIGDECIMALS_3)
            );
        Assertions.assertThat(conf.get(KEY_BIGINTEGER, BigInteger.class).get())
            .isEqualByComparingTo(new BigInteger(VAL_BIGINTEGER));
        Assertions.assertThat(conf.getList(KEY_BIGINTEGERS, BigInteger.class))
            .containsExactlyInAnyOrder(
                new BigInteger(VAL_BIGINTEGERS_1),
                new BigInteger(VAL_BIGINTEGERS_2),
                new BigInteger(VAL_BIGINTEGERS_3)
            );

        assertNullProperties(conf);
    }


    private void assertNullProperties(Configuration conf) {
        String nonExistingKey = "xxx";
        Assertions.assertThat(conf.get(nonExistingKey, String.class)).isEmpty();
        Assertions.assertThat(conf.getList(nonExistingKey, String.class)).isEmpty();
    }


    private void assertEnvironmentPropertiesExist(Configuration conf) {
        Assertions.assertThat(conf.get(KEY_ENV, String.class).get()).isEqualTo(VAL_ENV);
    }

}
