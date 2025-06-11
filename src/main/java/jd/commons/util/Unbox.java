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


/**
 * Provides utility methods to obtain primitive values from their 
 * Object counterparts while avoiding NullPointerExceptions.
 */
public interface Unbox
{
	/**
	 * @return if the Boolean equals Boolean.TRUE.
	 * @param b a Boolean
	 */
	public static boolean isTrue(Boolean b)
	{
		return b != null && b.booleanValue();
	}


	/**
	 * @return if the Boolean equals Boolean.FALSE.
	 * @param b a Boolean
	 */
	public static boolean isFalse(Boolean b)
	{
		return b != null && !b.booleanValue();
	}


	/**
	 * @return the value of the Boolean if it is not null, else the default value
	 * @param b a Boolean
	 * @param defaultValue a default value
	 */
	public static boolean toBoolean(Boolean b, boolean defaultValue)
	{
		return b != null ? b.booleanValue() : defaultValue;
	}


	/**
	 * @return the Number converted to a byte value. If the Number is null then 0 is returned 
	 * @param n a Number, can be null
	 */
	public static byte toByte(Number n)
	{
		return toByte(n, (byte)0);
	}


	/**
	 * @return the Number converted to a byte value. If the Number is null the 
	 * default value is returned
	 * @param n a Number, can be null
	 * @param defaultValue a default value
	 */
	public static byte toByte(Number n, byte defaultValue)
	{
		return n != null ? n.byteValue() : defaultValue;
	}


	/**
	 * @return the Character converted to a char value. If the Character is null then 0 is returned 
	 * @param c a Character, can be null
	 */
	public static char toChar(Character c)
	{
		return toChar(c, (char)0);
	}


	/**
	 * @return the Character converted to a char value. If the Character is null the 
	 * default value is returned
	 * @param c a Character, can be null
	 * @param defaultValue a default value
	 */
	public static char toChar(Character c, char defaultValue)
	{
		return c != null ? c.charValue() : defaultValue;
	}


	/**
	 * @return the Number converted to a double value. If the Number is null then 0.0 is returned 
	 * @param n a Number, can be null
	 */
	public static double toDouble(Number n)
	{
		return toDouble(n, 0.0);
	}


	/**
	 * @return the Number converted to a double value. If the Number is null the 
	 * default value is returned
	 * @param n a Number, can be null
	 * @param defaultValue a default value
	 */
	public static double toDouble(Number n, double defaultValue)
	{
		return n != null ? n.doubleValue() : defaultValue;
	}


	/**
	 * @return the Number converted to a float value. If the Number is null then 0.0f is returned 
	 * @param n a Number, can be null
	 */
	public static float toFloat(Number n)
	{
		return toFloat(n, 0F);
	}


	/**
	 * @return the Number converted to a float value. If the Number is null the 
	 * default value is returned
	 * @param n a Number, can be null
	 * @param defaultValue a default value
	 */
	public static float toFloat(Number n, float defaultValue)
	{
		return n != null ? n.floatValue() : defaultValue;
	}

	
	/**
	 * @return the Number converted to a int value. If the Number is null then 0 is returned 
	 * @param n a Number, can be null
	 */
	public static int toInt(Number n)
	{
		return toInt(n, 0);
	}


	/**
	 * @return the Number converted to a int value. If the Number is null the 
	 * default value is returned
	 * @param n a Number, can be null
	 * @param defaultValue a default value
	 */
	public static int toInt(Number n, int defaultValue)
	{
		return n != null ? n.intValue() : defaultValue;
	}


	/**
	 * @return the Number converted to a long value. If the Number is null then 0L is returned 
	 * @param n a Number, can be null
	 */
	public static long toLong(Number n)
	{
		return toLong(n, 0L);
	}


	/**
	 * @return the Number converted to a long value. If the Number is null the 
	 * default value is returned
	 * @param n a Number, can be null
	 * @param defaultValue a default value
	 */
	public static long toLong(Number n, long defaultValue)
	{
		return n != null ? n.longValue() : defaultValue;
	}

	
	/**
	 * @return the Number converted to a short value. If the Number is null then 0 is returned 
	 * @param n a Number, can be null
	 */
	public static short toShort(Number n)
	{
		return toShort(n, (short)0);
	}


	/**
	 * @return the Number converted to a short value. If the Number is null the 
	 * default value is returned
	 * @param n a Number, can be null
	 * @param defaultValue a default value
	 */
	public static short toShort(Number n, short defaultValue)
	{
		return n != null ? n.shortValue() : defaultValue;
	}


	/**
	 * Adds the given Numbers to a double sum.
	 * @param numbers Any null item is ignored.
	 * @return the sum
	 */
	public static double addDouble(Number... numbers)
	{
		double sum = 0;
		for (Number n : numbers)
			sum += toDouble(n);
		return sum;
	}


	/**
	 * Adds the given Numbers to a float sum.
	 * @param numbers Any null item is ignored.
	 * @return the sum
	 */
	public static float addFloat(Number... numbers)
	{
		float sum = 0;
		for (Number n : numbers)
			sum += toFloat(n);
		return sum;
	}


	/**
	 * Adds the given Numbers to a int sum.
	 * @param numbers Any null item is ignored.
	 * @return the sum
	 */
	public static int addInt(Number... numbers)
	{
		int sum = 0;
		for (Number n : numbers)
			sum += toInt(n);
		return sum;
	}


	/**
	 * Adds the given Numbers to a long sum.
	 * @param numbers Any null item is ignored.
	 * @return the sum
	 */
	public static long addLong(Number... numbers)
	{
		long sum = 0;
		for (Number n : numbers)
			sum += toLong(n);
		return sum;
	}
}
