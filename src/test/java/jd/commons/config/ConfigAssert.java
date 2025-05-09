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


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;


public class ConfigAssert
{
	private final Config config_;
	
	
	public static ConfigAssert of(Config config)
	{
		return new ConfigAssert(config);
	}
	  
	
	public ConfigAssert(Config config)
	{
		config_ = config;
	}


	public ConfigAssert clear()
	{
		config_.clear();
		return this;
	}

	
	public ConfigAssert contains(String key, boolean expected)
	{
		assertEquals(expected, config_.contains(key));
		return this;
	}


	public ConfigAssert get(String key, String expected)
	{
		assertEquals(expected, config_.get(key).value());
		return this;
	}


	public ConfigAssert immutable(boolean expected)
	{
		assertEquals(expected, config_.isImmutable());
		if (expected)
		{
			immutable(() -> config_.clear());
			immutable(() -> config_.setValue("a", "b"));
		}
		return this;
	}
	

	private void immutable(ThrowingCallable shouldRaiseThrowable)
	{
		assertThatThrownBy(shouldRaiseThrowable)
			.isInstanceOf(UnsupportedOperationException.class)
			.hasMessage("immutable");
	}
	


	public ConfigAssert keys(String... expected)
	{
		assertArrayEquals(expected, config_.keys().sorted().toArray());
		return this;
	}


	public ConfigAssert remove(String key)
	{
		config_.remove(key);
		return this;
	}

	
	public ConfigAssert set(String key, String value)
	{
		config_.setValue(key, value);
		return this;
	}

	
	public ConfigAssert toString(String expected)
	{
		assertEquals(expected, config_.toString());
		return this;
	}
}
