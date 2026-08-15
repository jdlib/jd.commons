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


import static deepdive.ExpectThat.*;
import deepdive.actual.Actual;
import deepdive.function.CheckedRunnable;


public class ConfigActual extends Actual<Config, Void, ConfigActual>
{
	public static ConfigActual of(Config config)
	{
		return new ConfigActual(config);
	}


	public ConfigActual(Config config)
	{
		super(config, null);
	}


	public ConfigActual clear()
	{
		value().clear();
		return this;
	}


	public ConfigActual contains(String key, boolean expected)
	{
		expectEqual(expected, value().contains(key));
		return this;
	}


	public ConfigActual get(String key, String expected)
	{
		expectEqual(expected, value().get(key).value());
		return this;
	}


	public ConfigActual immutable(boolean expected)
	{
		expectEqual(expected, value().isImmutable());
		if (expected)
		{
			immutable(() -> value().clear());
			immutable(() -> value().setValue("a", "b"));
		}
		return this;
	}


	private void immutable(CheckedRunnable<?> runnable)
	{
		expectError(runnable)
			.isA(UnsupportedOperationException.class)
			.message("immutable");
	}


	public ConfigActual keys(String... expected)
	{
		expectThat(value().keys().sorted().toArray(String[]::new)).equal(expected);
		return this;
	}


	public ConfigActual remove(String key)
	{
		value().remove(key);
		return this;
	}


	public ConfigActual set(String key, String value)
	{
		value().setValue(key, value);
		return this;
	}


	@Override
	public ConfigActual toString(String expected)
	{
		expectEqual(expected, value().toString());
		return this;
	}
}
