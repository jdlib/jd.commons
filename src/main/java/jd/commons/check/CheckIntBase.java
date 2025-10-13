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


abstract class CheckIntBase<T extends CheckIntBase<T>> extends CheckHelper
{
	/**
	 * The int value.
	 */
	protected final int value_;
	
	/**
	 * Describes the value.
	 */
	protected final String what_;
	
	
	protected CheckIntBase(int value, String what)
	{
		value_ = value;
		what_  = what;
	}
	
	
	/**
	 * @return the value of this Check object.
	 */
	public int get()
	{
		return value_;
	}
	
	
	/**
	 * Checks that the size is greater than the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public T greater(int other)
	{
		return compare(other, CheckOp.GREATER);
	}
	
	
	/**
	 * Checks that the size is greater than or equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public T greaterEq(int other)
	{
		return compare(other, CheckOp.GREATER_EQ);
	}
	
	
	/**
	 * Checks that the size is less than the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public T less(int other)
	{
		return compare(other, CheckOp.LESS);
	}
	
	
	/**
	 * Checks that the size is less than or equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public T lessEq(int other)
	{
		return compare(other, CheckOp.LESS_EQ);
	}

	
	/**
	 * Checks that the size is equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public T equal(int other)
	{
		return compare(other, CheckOp.EQ);
	}

	
	/**
	 * Checks that the size is not equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public T notEqual(int other)
	{
		return compare(other, CheckOp.NOT_EQ);
	}

	
	/**
	 * Checks that this value equals true when compared to the other value using the operator.
	 * @param other the other value
	 * @param op the Operator
	 * @return the value under test 
	 * @throws IllegalArgumentException if the comparison fails
	 */
	@SuppressWarnings("unchecked")
	protected T compare(int other, CheckOp op)
	{
		if (!op.compare(value_, other))
			throw failCompareEx(what(), value_, other, op);
		return (T)this;
	}
	
	
	/**
	 * @return the {@link #normWhat(String) normed what description}.
	 */
	protected String what()
	{
		return normWhat(what_);
	}
}
