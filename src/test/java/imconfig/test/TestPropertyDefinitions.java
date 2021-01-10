package imconfig.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import java.util.regex.PatternSyntaxException;
import org.junit.Test;
import imconfig.PropertyDefinition;

public class TestPropertyDefinitions {

    @Test
    public void testRequiredPropertyDoNotAcceptEmptyOrNullValues() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .required()
            .textType()
            .build();
        assertThat(definition.accepts("")).isFalse();
        assertThat(definition.accepts(null)).isFalse();
    }


    @Test
    public void testTextWithoutPatternAcceptsAnyValue() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .textType()
            .build();
        assertThat(definition.accepts("")).isTrue();
        assertThat(definition.accepts("dasdjhsajkdhsakjd")).isTrue();
        assertThat(definition.accepts("12312321123")).isTrue();
        assertThat(definition.accepts(")(*%^&%*^&%^&%#*&^)&*(&()*&)(*&")).isTrue();
    }



    @Test
    public void testTextWithInvalidPatternThrowsException() {
        var builder = PropertyDefinition.builder().property("test");
        assertThatCode(()->builder.textType("A\\d{*\\dB"))
        .isExactlyInstanceOf(PatternSyntaxException.class)
        .hasMessageStartingWith("Illegal");
    }



    @Test
    public void testTextWithPatternAcceptsOnlyMatchingValues() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .textType("A\\d\\dB")
            .build();
        assertThat(definition.accepts("")).isFalse();
        assertThat(definition.accepts("dasdjhsajkdhsakjd")).isFalse();
        assertThat(definition.accepts("A1B")).isFalse();
        assertThat(definition.accepts("A12B")).isTrue();
    }


    @Test
    public void testIntegerOnlyAcceptsNumericIntegerValue() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .integerType()
            .build();
        assertThat(definition.accepts("")).isFalse();
        assertThat(definition.accepts("dasdjhsajkdhsakjd")).isFalse();
        assertThat(definition.accepts("12.65")).isFalse();
        assertThat(definition.accepts("13")).isTrue();
    }


    @Test
    public void testIntegerWithoutBoundsAcceptsAnyValue() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .integerType()
            .build();
        assertThat(definition.accepts("-32")).isTrue();
        assertThat(definition.accepts("0")).isTrue();
        assertThat(definition.accepts("1")).isTrue();
        assertThat(definition.accepts("43247329874239874")).isTrue();
    }


    @Test
    public void testIntegerWithInvalidBoundsThrowsException() {
        var builder = PropertyDefinition.builder().property("test");;
        assertThatCode(()->builder.integerType(6,3))
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage("Minimum value cannot be greater than maximum value");
    }


    @Test
    public void testIntegerWithBoundsOnlyAcceptsValuesInRange() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .integerType(3,6)
            .build();
        assertThat(definition.accepts("2")).isFalse();
        assertThat(definition.accepts("3")).isTrue();
        assertThat(definition.accepts("6")).isTrue();
        assertThat(definition.accepts("7")).isFalse();
    }


    @Test
    public void testDecimalOnlyAcceptsNumericValues() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .decimalType()
            .build();
        assertThat(definition.accepts("")).isFalse();
        assertThat(definition.accepts("dasdjhsajkdhsakjd")).isFalse();
        assertThat(definition.accepts("12.65")).isTrue();
        assertThat(definition.accepts("13")).isTrue();
    }


    @Test
    public void testDecimalWithoutBoundsAcceptsAnyValue() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .decimalType()
            .build();
        assertThat(definition.accepts("-32.0")).isTrue();
        assertThat(definition.accepts("0.0")).isTrue();
        assertThat(definition.accepts("1.7")).isTrue();
        assertThat(definition.accepts("43247329874239874.4243242343243")).isTrue();
    }


    @Test
    public void testDecimalWithInvalidBoundsThrowsException() {
        var builder = PropertyDefinition.builder().property("test");
        assertThatCode(()->builder.decimalType(4.6,4.3))
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessage("Minimum value cannot be greater than maximum value");
    }


    @Test
    public void testDecimalWithBoundsOnlyAcceptsValuesInRange() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .decimalType(4.3,4.6)
            .build();
        assertThat(definition.accepts("4.2")).isFalse();
        assertThat(definition.accepts("4.3")).isTrue();
        assertThat(definition.accepts("4.6")).isTrue();
        assertThat(definition.accepts("4.7")).isFalse();
    }


    @Test
    public void testEnumOnlyAcceptsValuesInList() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .enumType("hello","goodbay")
            .build();
        assertThat(definition.accepts("hello")).isTrue();
        assertThat(definition.accepts("HELLO")).isTrue();
        assertThat(definition.accepts("Hello")).isTrue();
        assertThat(definition.accepts("goodbay")).isTrue();
        assertThat(definition.accepts("GOODBAY")).isTrue();
        assertThat(definition.accepts("Goodbay")).isTrue();
        assertThat(definition.accepts("later")).isFalse();
    }


    @Test
    public void testBooleanOnlyAcceptsTrueOrFalse() {
        var definition = PropertyDefinition.builder()
            .property("test")
            .booleanType()
            .build();
        assertThat(definition.accepts("true")).isTrue();
        assertThat(definition.accepts("false")).isTrue();
        assertThat(definition.accepts("other")).isFalse();
    }

}
