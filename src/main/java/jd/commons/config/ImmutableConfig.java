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


import jd.commons.check.Check;


/**
 * A Config implementation which cannot be modified.
 */
public class ImmutableConfig extends ProxyConfig
{
	public static final ImmutableConfig EMPTY = new ImmutableConfig(new MapConfig());  
		
		
	public static Config of(Config config)
	{
		Check.notNull(config, "wrapped");
		return !config.isImmutable() ? new ImmutableConfig(config) : config;
	}

	
	public ImmutableConfig(Config wrapped)
	{
		super(wrapped);
	}
	
	
	@Override
	public boolean isImmutable()
	{
		return true;
	}

	
	@Override
	protected void setInternal(String key, String value)
	{
		throw createImmutableEx();
	}
	
	
	@Override
	public Config clear()
	{
		throw createImmutableEx();
	}

	
	@Override
	protected void describeSelf(StringBuilder s)
	{
		s.append("readonly");
	}
}
