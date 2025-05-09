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


/**
 * A Check class for long values.
 */
public class CheckLong extends CheckHelper
{
	/**
	 * The long value.
	 */
	protected final long value_;
	
	/**
	 * Describes the value.
	 */
	protected final String what_;
	

	/**
	 * Creates a new CheckLong.
	 * @param value a long value
	 * @param what describes the value
	 */
	protected CheckLong(long value, String what)
	{
		value_ = value;
		what_  = what;
	}
	
	
	/**
	 * Checks that the size is greater than the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckLong greater(long other)
	{
		return compare(other, CheckOp.GREATER);
	}
	
	
	/**
	 * Checks that the size is greater than or equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckLong greaterEq(long other)
	{
		return compare(other, CheckOp.GREATER_EQ);
	}
	
	
	/**
	 * Checks that the size is less than the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckLong less(long other)
	{
		return compare(other, CheckOp.LESS);
	}
	
	
	/**
	 * Checks that the size is less than or equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckLong lessEq(long other)
	{
		return compare(other, CheckOp.LESS_EQ);
	}

	
	/**
	 * Checks that the size is equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckLong equal(long other)
	{
		return compare(other, CheckOp.EQ);
	}

	
	/**
	 * Checks that the size is not equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckLong notEqual(long other)
	{
		return compare(other, CheckOp.NOT_EQ);
	}

	
	/**
	 * Compares this long value to another using the given operator
	 * and throws an exception if the comparison returns falsee
	 * @param other another value
	 * @param op an operator
	 * @return this
	 */
	protected CheckLong compare(long other, CheckOp op)
	{
		if (!op.compare(value_, other))
			throw failCompareEx(what(), value_, other, op);
		return this;
	}
	
	
	/**
	 * @return what normed.
	 */
	protected String what()
	{
		return normWhat(what_);
	}
}
