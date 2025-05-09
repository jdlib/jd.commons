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
 * Provides utility methods to Check classes.
 */
class CheckHelper
{
	/**
	 * @param what a description
	 * @return the description if not null, else the string "arg"
	 */
	protected static String normWhat(String what)
	{
		return what != null ? what : "arg";
	}


	/**
	 * @param arg some argument value
	 * @return a String version of the argument.
	 */
	protected static String argString(Object arg)
	{
		if (arg == null)
			return "null";
		if (arg instanceof String)
			return '"' + arg.toString() + '"';
		else
			return arg.toString();
	}

	
	/**
	 * @return a IllegalArgumentException which describes a failed comparison between number values
	 * @param what describes the actual argument
	 * @param actual the actual value
	 * @param expected the expected value
	 * @param op the comparison operator
	 */
	protected static IllegalArgumentException failCompareEx(String what, Number actual, Number expected, CheckOp op)
	{
		return new IllegalArgumentException(normWhat(what) + " is " + actual + ", expected to be " + op.symbol() + ' ' + expected);
	}
}
