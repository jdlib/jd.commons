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


/**
 * CheckIndex allows to check a index value.
 * It inherits all comparison checks for int values
 * and additionally allows the index to be checked against
 * objects which have a size or length.
 */
public class CheckIndex extends CheckIntBase<CheckIndex>
{
	/**
	 * Creates a CheckIndex object.
	 * @param index the index value. An exception is thrown if the index is &lt; 0.
	 * @param what describes the index
	 */
	protected CheckIndex(int index, String what)
	{
		super(index, what);
		greaterEq(0);
	}
	
	
	/**
	 * Checks if this index is valid with respect to the length of the 
	 * given CharSequence.
	 * @param s a CharSequence 
	 * @return this
	 */
	public CheckIndex validFor(CharSequence s)
	{
		return validFor(Check.notNull(s, "s").length(), false);
	}
	
	
	/**
	 * Checks if this index is valid with respect to the length of the 
	 * given array.
	 * @param array an array
	 * @param<T> the type of the array elements
	 * @return this
	 */
	public <T> CheckIndex validFor(T[] array)
	{
		return validFor(Check.notNull(array, "array").length, false);
	}

	
	/**
	 * Checks if this index is valid with respect to the size of the 
	 * given collection.
	 * @param collection a Collection
	 * @return this
	 */
	public CheckIndex validFor(Collection<?> collection)
	{
		Check.notNull(collection, "collection");
		return validFor(collection.size(), true);
	}

	
	private CheckIndex validFor(int size, boolean isSize)
	{
		if (value_ >= size)
			throw validForFailed(size, isSize);
		return this;
	}
	
	
	private IllegalArgumentException validForFailed(int size, boolean isSize)
	{
		StringBuilder s = new StringBuilder();
		s.append("index");
		if (what_ != null)
			s.append(' ').append(what_);
		s.append(" is ").append(value_).append(" >= ").append(isSize ? "size" : "length").append(' ').append(size);
		return new IllegalArgumentException(s.toString());
	}
	
	
	@Override
	protected String what()
	{
		return what_ != null ? what_ : "index";
	}
}
