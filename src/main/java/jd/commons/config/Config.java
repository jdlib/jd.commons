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
package jd.commons.config;


import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.util.GetString;
import jd.commons.util.SetString;


/**
 * Config represents a map like structure of String key/value pairs.
 * Additionally Config provides extended validation and type support when setting or getting a value:
 * <ul>
 * <li>{@link #get(String)} returns a {@link GetString} builder which allows to validate and convert the value stored for 
 * 		a key.
 * <li>{@link #set(String)} returns a {@link SetString} builder which allows to pass non-string values and converts them to a String.
 * </ul> 
 * <p>
 * Config implementations exists for several sources (Properties, Maps, JNDI context).<br>
 * Config objects can be {@link #concat(Config...) concatenated} to provide fallback values.<br>
 * Config can be {@link #prefix(String) prefixed}, i.e. a key prefix is automatically applied 
 * 		when accessing a key value.<br>
 * Any config can be turned into a {@link #immutable() immutable} config. 
 */
public abstract class Config
{
	/**
	 * Concatenates Configs. The returned Config object returns the value from the first
	 * config which contains the key.
	 * @param configs some configs
	 * @return the concatenated config, can be null if all provided configs are null
	 */
	public static Config concat(Config... configs)
	{
		if (configs == null)
			return null;
		switch (configs.length)
		{
			case 0: return null;
			case 1: return configs[0];
			case 2:
				Config c0 = configs[0];
				Config c1 = configs[1];
				if (c0 == null)
					return c1;
				else if (c1 == null)
					return c0;
				else
					return new ConcatConfig(c0, c1);
			default:
				Config concat = configs[0];
				for (int i=1; i<configs.length; i++)
					concat = concat(concat, configs[i]);
				return concat;
		}
	}
	
	
	/**
	 * Checks that the key is not empty.
	 * @param key a key
	 * @return the key
	 */
	protected String checkKey(String key)
	{
		return Check.notEmpty(key, "key");
	}
	

	/**
	 * Retrieves the key value and makes available via the returned GetString object.
	 * @param key the key, must not be null or empty
	 * @return a {@link GetString} object to access or convert the key value.  
	 */
	public GetString get(String key)
	{
		String value = getValue(key);
		return GetString.of(value, key);
	}


	/**
	 * @return the value of the key, can be null 
	 * @param key the key, must not be null or empty
	 */
	public String getValue(String key)
	{
		return getInternal(checkKey(key));
	}

	
	/**
	 * @return the value for a key.
	 * @param key a non-null key
	 */
	protected abstract String getInternal(String key);


	/**
	 * @return if the config contains a non-null value for the key.
	 * @param key the key, must not be null or empty
	 */
	public boolean contains(String key)
	{
		return containsInternal(checkKey(key));
	}


	/**
	 * @return if the config contains a non-null value for the key.
	 * @param key a non-null key
	 */
	protected abstract boolean containsInternal(String key);
	

	public SetString<Config> set(String key)
	{
		checkKey(key);
		return new SetString<>()
		{
			@Override
			public Config to(String value)
			{
				return setValue(key, value);
			}
		};
	}


	/**
	 * Sets the value of a key.
	 * @param key the key
	 * @param value the value. If null, the key will be removed.
	 * 		If not null, its toString() method will be called to
	 * 		convert it into a string.
	 * @return this
	 */
	public Config setValue(String key, String value)
	{
		checkKey(key);
		setInternal(key, value);
		return this;
	}
	
	
	/**
	 * Internal method to set the key, implemented by subclasses. 
	 * @param key a key, checked to be not empty
	 * @param value a value, can be null
	 */
	protected abstract void setInternal(String key, String value);


	/**
	 * Removes a key. This a shortcut for {@code setValue(key, null)}
	 * @param key a key, checked to be not empty
	 * @return this
	 */
	public Config remove(String key)
	{
		return setValue(key, null);
	}


	public abstract Config clear();

	
	/**
	 * @return a new config object which wraps this config.
	 * @param prefix a prefix which is automatically preprended
	 * 		to every key:
	 * 		Example:
	 * 		<code>
	 * 		Config s1 = ...;<br>
	 * 		assert s1.hasKey("one.two")<br>
	 * 		Config s2 = new Config2(s1, "one.");<br>
	 * 		assert s1.getKey("one.two") == s2.getKey("two")
	 * 		</code>
	 */
	@CheckReturnValue
	public Config prefix(String prefix)
	{
		return PrefixedConfig.of(this, prefix);
	}


	/**
	 * @return a immutable Config wrapping this Config. 
	 */
	@CheckReturnValue
	public Config immutable()
	{
		return ImmutableConfig.of(this);
	}


	/**
	 * @return if this Config is immutable.
	 */
	public abstract boolean isImmutable();


	protected UnsupportedOperationException createImmutableEx()
	{
		return new UnsupportedOperationException("immutable");
	}

	
	/**
	 * @return a Stream of the keys known to this Config.
	 */
	public abstract Stream<String> keys();
	
	
	/**
	 * @return a String representation of this Config.
	 * @see #describe(StringBuilder)
	 */
	@Override 
	public final String toString()
	{
		StringBuilder s = new StringBuilder("Config").append('[');
		describe(s);
		return s.append(']').toString();
	}


	/**
	 * Adds a description of this Config to the StringBuilder.
	 * @param s a StringBuilder
	 * @see #toString()
	 */
	protected abstract void describe(StringBuilder s);
}
