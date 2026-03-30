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
package jd.commons.util;


import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Holder contains a value.
 * Holder implements Consumer, Supplier and Function in order
 * to be easily used in Streams.
 * @param<V> the value type.
 */
public class Holder<V> implements Consumer<V>, Supplier<V>, Function<V,V>
{
	private V value_;


	/**
	 * Creates a Holder with a null value.
	 */
	public Holder()
	{
		this(null);
	}


	/**
	 * Creates a Holder with an initial value.
	 * @param value the value
	 */
	public Holder(V value)
	{
		value_ = value;
	}


	/**
	 * Sets the value to null.
	 */
	public void clear()
	{
		value_ = null;
	}


	/**
	 * Implements {@link Consumer} and sets the value.
	 * @param value a value
	 */
	@Override
	public void accept(V value)
	{
		set(value);
	}


	/**
	 * Implements {@link Function} and {@link #set(Object) sets} and returns the value
	 * @param value a value
	 * @return the value
	 */
	@Override
	public V apply(V value)
	{
		return set(value);
	}


	/**
	 * Sets the value.
	 * @param value a value
	 * @return the value
	 */
	public V set(V value)
	{
		value_ = value;
		return value;
	}


	/**
	 * Implements Supplier and returns the value.
	 * @return the value
	 */
	@Override public V get()
	{
		return value_;
	}


	/**
	 * @return the value or {@code defaultValue} if the value is null.
	 * @param defaultValue a default value
	 */
	public V getOr(V defaultValue)
	{
		return value_ != null ? value_ : defaultValue;
	}


	/**
	 * @return if the value is not null.
	 */
	public boolean has()
	{
		return value_ != null;
	}


	/**
	 * @return if the value equals the given value.
	 * @param test another value
	 */
	public boolean has(V test)
	{
		return Objects.equals(value_, test);
	}


	/**
	 * @return a String representation.
	 */
	@Override
	public String toString()
	{
		return "Holder" + ':' + value_;
	}
}
