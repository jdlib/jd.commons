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
package jd.commons.check;


import java.util.Collection;
import java.util.Objects;


/**
 * Allows to check the values of an Iterable.
 * @param <T> the type of the elements
 */
public class CheckElems<T> extends CheckHelper
{
	private final Iterable<T> iterable_;
	private final String what_;
	
	
	/**
	 * Creates a new CheckElems.
	 * @param it the iterator
	 * @param what describes the value
	 */
	protected CheckElems(Iterable<T> it, String what)
	{
		iterable_	= Check.notNull(it, what);
		what_ 		= what;
	}
	

	/**
	 * Checks that the elements are not empty.
	 * @return this
	 */
	public CheckElems<T> notEmpty()
	{
		if (!iterable_.iterator().hasNext())
			throw new IllegalArgumentException(normWhat(what_) + " is empty");
		return this;
	}

	
	/**
	 * Checks that none of the elements is null.
	 * @return this
	 */
	public CheckElems<T> noneNull()
	{
		return notContains(null);
	}


	/**
	 * Checks that the elements do not contain the given value.
	 * @param value a value
	 * @return this
	 */
	public CheckElems<T> notContains(T value)
	{
		if (doesContain(value))
			throw new IllegalArgumentException(normWhat(what_) + " contains " + argString(value));
		return this;
	}


	/**
	 * Checks that the elements do contain the given value.
	 * @param value a value
	 * @return this
	 */
	public CheckElems<T> contains(T value)
	{
		if (!doesContain(value))
			throw new IllegalArgumentException(normWhat(what_) + " not contains " + argString(value));
		return this;
	}
	
	
	private boolean doesContain(T value)
	{
		// JDK immutable collections throw a NPE for null values
		// therefore if value_ is null, fall back to iteration	
		if ((value != null) && (iterable_ instanceof Collection))
			return ((Collection<T>)iterable_).contains(value);

		for (Object elem : iterable_)
		{
			if (Objects.equals(elem, value))
				return true;
		}
		return false;
	}
}


