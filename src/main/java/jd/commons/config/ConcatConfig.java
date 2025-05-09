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


class ConcatConfig extends Config
{
	private final Config[] configs_;

	
	public ConcatConfig(Config... configs)
	{
		Check.elems(configs, "configs").notEmpty().noneNull();
		configs_ = configs;
	}

	
	@Override
	protected boolean containsInternal(String key)
	{
		for (Config c : configs_)
		{
			if (c.containsInternal(key))
				return true;
		}
		return false;
	}


	@Override protected String getInternal(String key)
	{
		for (Config c : configs_)
		{
			String s = c.getInternal(key);
			if (s != null)
				return s;
		}
		return null;
	}


	@Override
	public boolean isImmutable()
	{
		return true;
	}

	
	@Override protected void setInternal(String key, String value)
	{
		throw createImmutableEx();
	}


	@Override
	public Config clear()
	{
		throw createImmutableEx();
	}

	
	@Override 
	public Stream<String> keys()
	{
		Stream<String> keys = configs_[0].keys();
		for (int i=1; i<configs_.length; i++)
			keys = Stream.concat(keys, configs_[i].keys());
		return keys.distinct();
	}


	@Override
	protected void describe(StringBuilder s)
	{
		for (int i=0; i<configs_.length; i++)
		{
			if (i > 0)
				s.append(" | ");
			configs_[i].describe(s);
		}
	}
}
