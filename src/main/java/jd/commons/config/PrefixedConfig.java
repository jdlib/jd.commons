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
import jd.commons.check.Check;


class PrefixedConfig extends ProxyConfig
{
	private final String prefix_;

	
	public static PrefixedConfig of(Config wrapped, String prefix)
	{
		Check.notNull(wrapped, "wrapped");
		Check.notEmpty(prefix, "prefix");
		if (wrapped instanceof PrefixedConfig)
		{
			PrefixedConfig ps = (PrefixedConfig)wrapped;
			wrapped = ps.wrapped_;
			prefix  = prefix + ps.prefix_;
		}
		return new PrefixedConfig(wrapped, prefix);
	}


	private PrefixedConfig(Config wrapped, String prefix)
	{
		super(wrapped);
		prefix_ = prefix;
	}

	
	@Override protected String getInternal(String key)
	{
		return wrapped_.getInternal(fullKey(key));
	}


	@Override protected void setInternal(String key, String value)
	{
		wrapped_.setValue(fullKey(key), value);
	}


	private String fullKey(String key)
	{
		return prefix_ + key;
	}


	@Override 
	public Stream<String> keys()
	{
		int len = prefix_.length();
		return wrapped_.keys().filter(s -> s.startsWith(prefix_)).map(s -> s.substring(len)).distinct();
	}


	@Override
	protected void describeSelf(StringBuilder s)
	{
		s.append('"').append(prefix_).append('"');
	}
}