Immutable Configurations
================================================================================

![GitHub](https://img.shields.io/github/license/luiinge/immutable-config?style=plastic)
![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/luiinge/immutable-config/quality%20check/master?style=plastic)
![Maven Central](https://img.shields.io/maven-central/v/io.github.luiinge/immutable-config?style=plastic)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=luiinge_immutable-config&metric=alert_status)](https://sonarcloud.io/dashboard?id=luiinge_immutable-config)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=luiinge_immutable-config&metric=ncloc)](https://sonarcloud.io/dashboard?id=luiinge_immutable-config)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=luiinge_immutable-config&metric=coverage)](https://sonarcloud.io/dashboard?id=luiinge_immutable-config)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=luiinge_immutable-config&metric=bugs)](https://sonarcloud.io/dashboard?id=luiinge_immutable-config)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=luiinge_immutable-config&metric=code_smells)](https://sonarcloud.io/dashboard?id=luiinge_immutable-config)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=luiinge_immutable-config&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=luiinge_immutable-config)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=luiinge_immutable-config&metric=sqale_index)](https://sonarcloud.io/dashboard?id=luiinge_maven-immutable-config)




> A multi-purpose, immutable configuration interface

This library provides a simple interface in order to load and consume *configurations*,
which are mainly a set of valued properties that can be parsed to specific Java types.
The primary focus of the library is **null-safety**, **immutability**, and **fluency**.

The `Config` class is immutable in order to ensure the values are not modified
by any process, but it can build derived configurations. Also, when a property is not defined,
the `get` and `getList` methods return an empty `Optional` and an empty immutable `List`, 
respectively, instead of `null`.

In addition, this library implements a simple approach to create *property definitions*, so that
each property in a configuration can have some meta-data regarding the actual values expected
by the consumer system.

There are a wide range of builder methods to get configurations from different sources, such as:
- OS environment variables
- Java system properties
- Java `.properties` files
- JSON files
- XAML files
- TOML files
- `Map` and `Properties` objects
- *In-code* pairs of <key, value>


Usage
-----------------------------------------------------------------------------------------

### Maven dependency

```xml
<dependency>
    <groupId>io.github.luiinge</groupId>
    <artifactId>immutable-config</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Loading configurations

In order to obtain a configuration, simply use one of the static methods in `ConfigFactory`:

```java
Config conf = Config.factory().fromPath(Path.of("myConfig.yaml"));
```

Two configurations can be merged, using one of them as base:

```java
Config confA = Config.factory().fromEnvironment();
Config confB = Config.factory().fromPath(Path.of("myConfig.yaml"));
Config confC = confA.append(confB);
```

You can create a new configuration from Java objects:
```java
Map<String,String> map = Map.of(
    "propertyA","valueA",
    "propertyB","valueB"
);
Config conf = Config.factory().fromMap(map);
```
```java
Config conf = Config.factory().fromPairs(
    "propertyA","valueA",
    "propertyB","valueB"
);
```

In addition, you can annotate any class and use it as a configuration source:

```java
@AnnotatedConfig(properties={
  @Property(key="propertyA", value="valueA"),
  @Property(key="propertyB", value="valueB")
})
class MyConfigClass { }
```
```java
Config conf = Config.factory().fromAnnotation(MyConfigClass.class);
```

### Retrieving values
- All values are stored either as single `String` literals or lists of `String` literals.
- The `get` method returns an `Optional`, so you are forced to deal with 
possible nulls. 
- In case you want to parse the raw value into a custom type, you can either make use of the 
`Optional` API, or pass directly the parsing function to the getter method

```java
  Config config = ... 
  Optional<String> user = config.get("user");
  Optional<Integer> year = config.get("year").map(Integer::valueOf);
  Locale locale = config.get("language", Locale::new).orElse(Locale.ENGLISH);
```

#### Multi-valued properties
This library support _multi-valued properties_, that is, properties that have a list 
of values instead of a single one. For that, the method `getList` works similarly to 
`get` but returns a `List` (potentially empty) instead of an `Optional`.

```java
  Configuration configuration = ... 
  List<String> servers = configuration.getList("servers");
```
When invoked aiming a single-value property, it would return a collection of 1 item.
Alternatively, invoking `get` aiming a multi-valued property, it would return the 
first element present.

### Loading configurations from external sources
By using the methods `fromPath`, `fromResource`, `fromURI` you can create 
a `Config` instance reflecting the configuration defined in such sources.
The accepted formats are Java Properties files, YAML, JSON, XML and TOML.
Some of these formats would require additional dependencies to work:


| format | required dependencies |
| --- | --- |
| `properties` | none |
| `JSON` | `com.fasterxml.jackson.core:jackson-databind` |
| `YAML` | `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml` |
| `XML` | `com.fasterxml.jackson.dataformat:jackson-dataformat-xml` |
| `TOML` | `com.fasterxml.jackson.dataformat:jackson-dataformat-toml` |


> These extra dependencies are *NOT* transitive dependencies, that is, they 
would be not included directly. The main reason behind this decision is 
to avoid adding dependencies that only would be use in specific scenarios.
It is responsibility of the client to add them to the build in case they 
were required.


### Property definitions

You can also create _definitions_ to express what properties your application expects, including 
some basic validations and default values. Each expected property is defined by its key, data type, 
default value, and additional constraints regarding the data type.
Supported types are:

| type      | description           | additional constraints |
| --------- | --------------------- | ---------------------- |
| `text`    | plain text            | regular expression     |
| `enum`    | strict list of values |                        |
| `boolean` | `true` or `false`     |                        |
| `integer` | integer number        | min and/or max bounds  |
| `decimal` | decimal number        | min and/or max bounds  |

Property definitions can be either read from YAML files (as a kind of _meta-configuration_), or
created programmatically, using any of the existing methods starting with `according...` . Notice that
the definition is always applied to a `Configuration` object.

Once a configuration has one or more definitions applied, you can validate it using 
the method `validate()`. If there are invalid property values itt would throw an 
`InvalidConfigException`, along with a descriptive message informing of every 
invalid value. You can also get the list of violations calling the method `validations()`.

Notice that you can retrieve invalid values normally using the `get()` and `getList()` 
methods.

#### Loading property definitions from external file

Property definitions can be easily readed from YAML, JSON or XML files using the following method:

```java
    Config definition = Config.factory().accordingDefinitionsFromPath(Path.of("my-definition.yaml"));
```

The property definition file uses the following structure:
```
<property-key>:
    type: <text|enum|boolean|integer|decimal>
    [description: <optional description>]
    [requires: <true|false> (false if ommitted)]
    [defaultValue: <optional default value>]
    [constrains: (regarding the property type)
       <min | max | pattern | values>: <constraint-value> 
       ...
    ]
```

as shown is this example:

```yaml
my-properties.property-required:
   description: This is a test property that is required
   required: true
   type: text

my-properties.property-with-default-value:
   description: This is a property with a default value
   type: integer
   defaultValue: 5

my-properties.property-regex-text:
   type: text
   constraints:
      pattern: A\d\dB

my-properties.property-min-max-number:
   type: integer
   constraints:
      min: 2
      max: 3

my-properties.property-enumeration:
   type: enum
   constraints:
      values:
         - red
         - yellow
         - orange

my-properties.property-boolean:
   type: boolean
```

#### Create property definitions programmatically

If you prefer to encapsulate the properties definition within your code, it is possible to 
create the definitions programatically and then apply them to a configuration.

```java
var definitions = List.of(
    PropertyDefinition.builder("my-properties.property-required")
        .description("This is a test property that is required")
        .required()
        .textType()
        .build(),
    PropertyDefinition.builder("my-properties.property-with-default-value")
        .description("my-properties.property-with-default-value")
        .integerType()
        .defaultValue(5)
        .build(),
    PropertyDefinition.builder("my-properties.property-min-max-number")
        .integerType(2,3)
        .build()
);
var configuration = Config.factory().accordingDefinitions(definitions);
```


### Maven dependency
```xml
<dependency>
    <groupId>io.github.luiinge</groupId>
    <artifactId>immutable-config</artifactId>
    <version>2.0.0</version>
</dependency>
```


Requirements
-----------------------------------------------------------------------------------------
- Java 11 or newer.


## Contributing

## Authors

- Luis Iñesta Gelabert - luiinge@gmail.com


License
-----------------------------------------------------------------------------------------
```
MIT License

Copyright (c) 2020 Luis Iñesta Gelabert - luiinge@gmail.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

