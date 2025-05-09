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


import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import jd.commons.check.Check;


/**
 * A Config implementation based on a Map.
 */
public class MapConfig extends Config
{
	private final Map<String,String> map_;
	private final boolean immutable_;
	
	
	public static MapConfig env()
	{
		return new MapConfig(System.getenv(), true);
	}

	
	public MapConfig()
	{
		this(new HashMap<>(), false);
	}


	/**
	 * Creates an immutable Config object based on the given map.
	 * @param map a map, not null
	 */
	public MapConfig(Map<String,String> map)
	{
		this(map, true);
	}

	
	/**
	 * Creates an Config object based on the given map.
	 * @param map a map, not null
	 * @param immutable if the map is immutable
	 */
	public MapConfig(Map<String,String> map, boolean immutable)
	{
		map_ = Check.notNull(map, "map");
		immutable_ = immutable;
	}
	
	
	public Map<String,String> getMap()
	{
		return map_;
	}


	@Override
	protected boolean containsInternal(String key)
	{
		return map_.containsKey(key);
	}


	@Override
	protected String getInternal(String key)
	{
		return map_.get(key);
	}


	@Override
	protected void setInternal(String key, String value)
	{
		if (immutable_)
			throw createImmutableEx();
		map_.put(key, value);
	}
	
	
	@Override
	public Config clear()
	{
		if (immutable_)
			throw createImmutableEx();
		map_.clear();
		return this;
	}


	@Override
	public Stream<String> keys()
	{
		return map_.keySet().stream();
	}


	@Override
	public boolean isImmutable()
	{
		return immutable_;
	}


	@Override
	protected void describe(StringBuilder s)
	{
		s.append("map");
	}
}
