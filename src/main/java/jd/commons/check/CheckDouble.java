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
 * CheckDouble allows to check a double value.
 */
public class CheckDouble extends CheckHelper
{
	private final double value_;
	private final String what_;
	
	
	/**
	 * Creates a new CheckDouble.
	 * @param value the decimal value
	 * @param what describes the value
	 */
	protected CheckDouble(double value, String what)
	{
		value_ = value;
		what_  = what;
	}
	
	
	/**
	 * Checks that this value is greater than the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckDouble greater(double other)
	{
		return compare(other, CheckOp.GREATER);
	}
	
	
	/**
	 * Checks that this value is greater than or equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckDouble greaterEq(double other)
	{
		return compare(other, CheckOp.GREATER_EQ);
	}
	
	
	/**
	 * Checks that this value is less than the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckDouble less(double other)
	{
		return compare(other, CheckOp.LESS);
	}
	
	
	/**
	 * Checks that this value is less than or equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckDouble lessEq(double other)
	{
		return compare(other, CheckOp.LESS_EQ);
	}

	
	/**
	 * Checks that this value is equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckDouble equal(double other)
	{
		return compare(other, CheckOp.EQ);
	}

	
	/**
	 * Checks that this value is finite.
	 * @return this
	 */
	public CheckDouble finite()
	{
		if (!Double.isFinite(value_))
			throw new IllegalArgumentException(normWhat(what_) + " is " + value_);
		return this;
	}

	
	/**
	 * Checks that this value is negative.
	 * @return this
	 */
	public CheckDouble negative()
	{
		return less(0);
	}
	
	
	/**
	 * Checks that this value is not equal to the given value.
	 * @param other a value to compare
	 * @return this
	 */
	public CheckDouble notEqual(double other)
	{
		return compare(other, CheckOp.NOT_EQ);
	}

	
	/**
	 * Checks that this value is not NaN.
	 * @return this
	 */
	public CheckDouble notNaN()
	{
		if (Double.isNaN(value_))
			throw new IllegalArgumentException(normWhat(what_) + " is NaN");
		return this;
	}

	
	/**
	 * Checks that this value is positive.
	 * @return this
	 */
	public CheckDouble positive()
	{
		return greater(0);
	}

	
	private CheckDouble compare(double other, CheckOp op)
	{
		if (!op.compare(value_, other))
			throw failCompareEx(what_, value_, other, op);
		return this;
	}
}
