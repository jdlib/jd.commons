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
 * CheckSize allows to check a size or length value.
 */
public class CheckSize extends CheckLong
{
	private final boolean isSize_;
	
	
	protected CheckSize(long value, String what, boolean isSize)
	{
		super(value, what);
		isSize_	= isSize;
	}


	/**
	 * Checks that the provided value is contained in the interval [0,length[,
	 * i.e. is >= 0 and < length.
	 * @param index an index value
	 * @param what describes the index
	 * @return this
	 */
	public CheckSize indexValid(int index, String what)
	{
		if (index < 0 || index > value_ - 1)
			throw failContains(what, index, "<");
		return this;
	}


	/**
	 * Checks that the provided value is contained in the interval [0,length[,
	 * i.e. is >= 0 and < length.
	 * @param index an index value
	 * @return this
	 */
	public CheckSize indexValid(int index)
	{
		return indexValid(index, "index");
	}
	
	
	/**
	 * Checks that the provided value is contained in the interval [0,length],
	 * i.e. is >= 0 and <= length.
	 * @param end an end value 
	 * @param what describes the value
	 * @return this
	 */
	public CheckSize endValid(int end, String what)
	{
		if (end < 0 || end > value_)
			throw failContains(what, end, "<=");
		return this;
	}

	
	/**
	 * Checks that the provided value is contained in the interval [0,length],
	 * i.e. is >= 0 and <= length.
	 * @param end an end value 
	 * @return this
	 */
	public CheckSize endValid(int end)
	{
		return endValid(end, "end");
	}
	
	
	@Override
	protected String what()
	{
		return normWhat(what_) + (isSize_ ? ".size" : ".length");
	}


	protected IllegalArgumentException failContains(String what, long actual, String maxOp)
	{
		return new IllegalArgumentException(normWhat(what) + " is " + actual + ", expected to be >= 0 and " + maxOp + ' ' + value_);
	}
}
