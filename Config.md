# jd.commons.io.config

The `config` package contains the abstract `Config` class and various implementations.

`Config` represents a map-like structure of String keys mapped to String values.
`Config`  is thought as a replacement for `java.util.Properties`.

It allows:
- to easily get a Config value converted to a variety of (primitive or non-primitive) types
- to easily set a Config value from a variety of (primitive or non-primitive) types
- to concatenate Configs, to provide default values
- to have different `Config` backends, e.g based on `java.util.Map`, `java.util.Properties`, JNDI context, environment variables...
- to easily load or save Configs based on `java.util.Properties`

## Accessing Values
`Config` allows for easy conversion of String values to/from other types:

	Config config = ...
	
	// get values
	String name = config.get("name").value(); // return string value
	int itemCount = config.get("itemCount").asInt(); // convert to int
	File installDir = config.get("installDir").asFile(); // convert to File
	
	// set values
	config.set("price").to(4.5); // stores "4.5" under key "price"
	config.set("flag").to(true); // stores "true" under key "flag"

Replace missing values by defaults:

	itemCount = config.get("itemCount").asInt(); // fails for missing value
	itemCount = config.get("itemCount").asInt(0); // returns 0 if value missing
	
Provoke exceptions for missing values:

	installDir = config.get("installDir").asFile(); // returns null for missing value
	installDir = config.get("installDir").notEmpty().asFile(); // will complain if value missing or empty

## Primary Configs

`Config` itself is an abstract class. 
Primary configs are used to be primary sources of keys and values.
Various implementations for backing data structures are provided:

### PropsConfig

`PropsConfig` wraps a `java.util.Properties` object and makes use of its load and save methods to persist a `PropsConfig`.

	import java.io.File;
	import jd.commons.config.PropsConfig;
	
	File file = ...
	PropsConfig config = new PropsConfig();
	config.read().from(file);
	
	int itemCount = config.get("itemCount").asInt();
	config.set("price").to(4.5);
	
	config.write().to(file);
	
The system properties can easily be accessed using `PropsConfig`:
	
	PropsConfig sysProps = PropsConfig.system();
	
### MapConfig

`MapConfig` turns a `java.util.Map` object into a `Config`:

	import java.util.Map;
	import jd.commons.config.MapConfig;
	
	Map<String,String> map = ...
	MapConfig config = new MapConfig(map);
	int itemCount = config.get("itemCount").asInt();

The environment variables can easily be accessed using `MapConfig`:
	
	MapConfig env = MapConfig.env();
	
### JndiConfig
	
`JndiConfig` allows to access the values of JNDI context:
	
	import javax.naming.Context;
	import jd.commons.config.JndiConfig;
	
	Context context = ...
	JndiConfig config = new JndiConfig(context);
	int itemCount = config.get("itemCount").asInt();
	
	
## Derived Configs 

Existing configs can be transformed to other configs:

### Concatenated Configs

Given multiple configs you can create a new immutable `Config` which returns the 
value from the first config which contains the key. Effectively a config can be backed
by other configs to provide default values:

	Config config1 = ...
	Config config2 = ...
	Config concat = Config.concat(config1, config2);
	
	assert concat.contains("price");
	// is the same as
	assert config1.contains("price") || config2.cotains("price")


### Prefixed Configs

Given a config you can derive a config which knows only about the values
of the original config starting with a given prefix.

	// accessing values by their full key
	Config config = ...
	String v1 = config.get("some.v1").value()
	String v2 = config.get("some.v2").value()
	
	// accessing values by their prefixed key
	Config config = ...
	Config someConfig = config.prefix("some");
	String v1 = someConfig.get("v1").value()
	String v2 = someConfig.get("v2").value()

### Immutable Configs

`Config` not only allows to get values, but also set or remove a value for a key,
or clear all values. Implementations may support or not support modifying a config.
To ensure that a given config is immutable you can use

	Config config = ...
	Config immutableConfig = config.immutable();