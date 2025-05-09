/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2025 the original author or authors.
 */
/**
 * The {@code config} package contains the abstract {@link jd.commons.config.Config} class and
 * various implementations.<p>
 * {@code Config} represents a map-like structure of String keys mapped to String values and
 * is thought as a replacement for {@link java.util.Properties}.<br>
 * It allows:
 * <ul>
 * <li>to easily get a Config value converted to a variety of (primitive or non-primitive) types
 * <li>to easily set a Config value from a variety of (primitive or non-primitive) types
 * <li>to concatenate Configs, to provide default values
 * <li>to have different Config backends, e.g based on {@link java.util.Map}, {@link java.util.Properties}, 
 * 	   {@link javax.naming.Context JNDI context}, environment variables...
 * <li>to easily load or save Configs based on {@link java.util.Properties}
 * </ul>
 * <h2>Accessing Values</h2>
 * Config allows for easy conversion of String values to/from other types:
 * <pre><code>
 * Config config = ...
 *
 * // get values
 * String name = config.get("name").value(); // return string value
 * int itemCount = config.get("itemCount").asInt(); // convert to int
 * File installDir = config.get("installDir").asFile(); // convert to File
 *	
 * // set values
 * config.set("price").to(4.5); // stores "4.5" under key "price"
 * config.set("flag").to(true); // stores "true" under key "flag"
 * </code></pre>
 * <h3>Replace missing values by defaults:</h3>
 * <pre><code>
 * itemCount = config.get("itemCount").asInt(); // fails for missing value
 * itemCount = config.get("itemCount").asInt(0); // returns 0 if value missing
 * </code></pre>
 * <h3>Provoke exceptions for missing values:</h3>
 * <pre><code>
 * installDir = config.get("installDir").asFile(); // returns null for missing value
 * installDir = config.get("installDir").notEmpty().asFile(); // will complain if value missing or empty
 * </code></pre>
 * <h2>Primary Configs</h2>
 * Config itself is an abstract class. Primary configs are used to be primary sources of keys and values.
 * Various implementations for backing data structures are provided:
 * <h3>PropsConfig</h3>
 * {@link jd.commons.config.PropsConfig} wraps a java.util.Properties object and makes use of its 
 * load and save methods to persist a PropsConfig.
 * <pre><code>
 * import java.io.File;
 * import jd.commons.config.PropsConfig;
 * 
 * File file = ...
 * PropsConfig config = new PropsConfig();
 * config.read().from(file);
 * 
 * int itemCount = config.get("itemCount").asInt();
 * config.set("price").to(4.5);
 * 
 * config.write().to(file);
 * </code></pre>
 * The system properties can easily be accessed using PropsConfig:
 * <pre><code>
 * PropsConfig sysProps = PropsConfig.system();
 * </code></pre>
 * <h3>MapConfig</h3>
 * {@link jd.commons.config.MapConfig} turns a java.util.Map object into a Config:
 * <pre><code>
 * import java.util.Map;
 * import jd.commons.config.MapConfig;
 * 
 * Map&lt;String,String&gt; map = ...
 * MapConfig config = new MapConfig(map);
 * int itemCount = config.get("itemCount").asInt();
 * </code></pre>
 * The environment variables can easily be accessed using MapConfig:
 * <pre><code>
 * MapConfig env = MapConfig.env();
 * </code></pre>
 * <h3>JndiConfig</h3>
 * JndiConfig allows to access the values of JNDI context:
 * <pre><code>
 * import javax.naming.Context;
 * import jd.commons.config.JndiConfig;
 * 
 * Context context = ...
 * JndiConfig config = new JndiConfig(context);
 * int itemCount = config.get("itemCount").asInt();
 * </code></pre>
 * 	
 * <h2>Derived Configs</h2> 
 * Existing configs can be transformed to other configs:
 * <h3>Concatenated Configs</h3>
 * Given multiple configs you can create a new immutable `Config` which returns the 
 * value from the first config which contains the key. Effectively a config can be backed
 * by other configs to provide default values:
 * <pre><code>
 * Config config1 = ...
 * Config config2 = ...
 * Config concat = Config.concat(config1, config2);
 * 
 * assert concat.contains("price");
 * // is the same as
 * assert config1.contains("price") || config2.cotains("price")
 * </code></pre>
 *
 * <h3>Prefixed Configs</h3>
 * Given a config you can derive a config which knows only about the values
 * of the original config starting with a given prefix.
 * <pre><code>
 * // accessing values by their full key
 * Config config = ...
 * String v1 = config.get("some.v1").value()
 * String v2 = config.get("some.v2").value()
 * 
 * // accessing values by their prefixed key
 * Config config = ...
 * Config someConfig = config.prefix("some");
 * String v1 = someConfig.get("v1").value()
 * String v2 = someConfig.get("v2").value()
 * </code></pre>
 *
 * <h3>Immutable Configs</h3>
 * Config not only allows to get values, but also set or remove a value for a key,
 * or clear all values. Implementations may support or not support modifying a config.
 * To ensure that a given config is immutable you can use
 * <pre><code>
 * Config config = ...
 * Config immutableConfig = config.immutable(); *   
 * </code></pre>
 */
package jd.commons.config;

