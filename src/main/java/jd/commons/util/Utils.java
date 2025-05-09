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


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jd.commons.check.Check;


/**
 * Provides static utility functions.
 */
public interface Utils
{
	/**
	 * Creates a new array, with the given items prepended.
	 * @param array an array, if null then the new items are returned
	 * @param newItems the new items to prepend, if null then the input array is returned
	 * @param<T> the item type
	 * @return the new array
	 */
	 @SuppressWarnings("unchecked")
	 public static <T> T[] addFirst(T[] array, T... newItems)
	{
		if (isEmpty(newItems))
			return array;
		if (isEmpty(array))
			return newItems;
		T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), array.length + newItems.length);
		System.arraycopy(newItems, 0, result, 0, newItems.length);
		System.arraycopy(array, 0, result, newItems.length, array.length);
		return result;
	}

	
	/**
	 * Creates a new array, with the given items appended to the end.
	 * @param array an array, if null or empty then the new items are returned
	 * @param newItems the new items to append, if null then the input array is returned
	 * @param<T> the item type
	 * @return the new array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] addLast(T[] array, T... newItems)
	{
		if (isEmpty(newItems))
			return array;
		if (isEmpty(array))
			return newItems;
		T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), array.length + newItems.length);
		System.arraycopy(array, 0, result, 0, array.length);
		System.arraycopy(newItems, 0, result, array.length, newItems.length);
		return result;
	}
		
		
	/**
	 * Returns the part of the string after the given character
	 * or the defaultResult if the input does not contain the character.
	 * @param s the input string
	 * @param c a character
	 * @param defaultResult returned if the character is not a found
	 * @return the new string
	 */
	public static String afterOr(String s, char c, String defaultResult)
	{
		int p = s != null ? s.indexOf(c) : -1;
		return p == -1 ? defaultResult : s.substring(p + 1);
	}

	
	/**
	 * Returns the part of the string after the last given character
	 * or the defaultResult if the input does not contain the character.
	 * @param s the input string
	 * @param c a character
	 * @param defaultResult returned if the character is not a found
	 * @return the new string
	 */
	public static String afterLastOr(String s, char c, String defaultResult)
	{
		int p = s != null ? s.lastIndexOf(c) : -1;
		return p == -1 ? defaultResult : s.substring(p + 1);
	}

	
	/**
	 * Returns the part of the string before the given character
	 * or the defaultResult if the input does not contain
	 * the character.
	 * @param s the input string
	 * @param c a character
	 * @param defaultResult returned if the character is not a found
	 * @return the new string
	 */
	public static String beforeOr(String s, char c, String defaultResult)
	{
  		int p = s != null ? s.indexOf(c) : -1;
		return p == -1 ? defaultResult : s.substring(0, p);
	}

	
	/**
	 * Returns the part of the string before the last given character
	 * or the defaultResult if the input does not contain
	 * the character.
	 * @param s the input string
	 * @param c a character
	 * @param defaultResult returned if the character is not a found
	 * @return the new string
	 */
	public static String beforeLastOr(String s, char c, String defaultResult)
	{
  		int p = s != null ? s.lastIndexOf(c) : -1;
		return p == -1 ? defaultResult : s.substring(0, p);
	}


	/**
	 * Returns the given string with the suffix removed from its end. 
	 * If the string is null or does not end with the suffix it is returned unchanged.
	 * @param s a string
	 * @param suffix a suffix
	 * @return the string s with the suffix removed
	 */
	public static String cutEnd(String s, String suffix)
	{
		return (s != null) && s.endsWith(suffix) ? s.substring(0, s.length() - suffix.length()) : s;
	}

	
	/**
	 * Returns the given string with the prefix removed from its start. 
	 * If the string is null or does not start with the prefix it is returned unchanged.
	 * @param s a string
	 * @param prefix a prefix
	 * @return the string s with the prefix removed
	 */
	public static String cutStart(String s, String prefix)
	{
		return s != null && s.startsWith(prefix) ? s.substring(prefix.length()) : s;
	}


	/**
	 * Translates a Enum name into an Enum object.
	 * If the Enum name is null or not defined, then the default value is returned. This helps 
	 * to avoid the IllegalArgumentException thrown by Enum.valueOf
	 * @param enumClass the enum class
	 * @param name the name of an enum entry
	 * @param defaultValue a default value 
	 * @param <T> the enum type
	 * @return the enum
	 */
	public static <T extends Enum<T>> T enumOf(Class<T> enumClass, String name, T defaultValue)
	{
		Check.notNull(enumClass, "enumClass");
		try
		{
			if (name != null)
				return Enum.valueOf(enumClass, name);
		}
		catch(IllegalArgumentException e)
		{
		}
		return defaultValue;
	}
	
	
	/**
	 * Translates a Enum name into an Enum object.
	 * If the Enum name is null or not defined, then null is returned.
	 * @param enumClass the enum class
	 * @param name the name of an enum entry
	 * @param <T> the enum type
	 * @return the enum
	 */
	public static <T extends Enum<T>> T enumOf(Class<T> enumClass, String name)
	{
		return enumOf(enumClass, name, null);
	}


	/**
	 * Returns a string which is the input string with the
	 * suffix appended to the end. If the string is null,
	 * the suffix is returned. If the string already ends with
	 * the suffix it is returned unchanged.
	 * @param s a string, can be null
	 * @param suffix a suffix^, not null
	 * @return the string s with the suffix
	 */
	public static String haveEnd(String s, String suffix)
	{
		Check.notNull(suffix, "suffix");
		if (s == null)
			return suffix;
		else
			return !s.endsWith(suffix) ? s + suffix : s;
	}
	
	
	/**
	 * Returns a string which starts with the given prefix.
	 * If the string is null, the prefix is returned. If the string already starts with
	 * the prefix or is null it is returned unchanged.
	 * @param s a string, can be null
	 * @param prefix a prefix, not null
	 * @return the string s with the prefix
	 */
	public static String haveStart(String s, String prefix)
	{
		Check.notNull(prefix, "prefix");
		if (s == null)
			return prefix;
		else
			return !s.startsWith(prefix) ? prefix + s : s;
	}


	/**
	 * @return the (first) index of the given item in the array or -1 if not found.
	 * @param item the item
	 * @param array an array
	 * @param<T> the item type
	 */
	public static <T> int indexOf(T item, @SuppressWarnings("unchecked") T... array)
	{
		if (array != null)
		{
			for (int i=0; i<array.length; i++)
			{
				if (Objects.equals(array[i], item))
					return i;
			}
		}
		return -1;
	}

	
	/**
	 * @return tests if the object is an instance of the given class.
	 * @param object an object
	 * @param type a class
	 * @param <T> the class type
	 */
	public static <T> boolean isA(Object object, Class<T> type)
	{
		// use type.isInstance(object) and not type.isAssignableFrom(object.getClass())
		// since this does not work for Proxies (e.g. Annotations)
		return object != null && type != null && type.isInstance(object);
	}

	
	/**
	 * @return if the String is null, empty or blank, i.e.
	 * contains only {@link Character#isWhitespace(int) white space} codepoints.
	 * @param s a String
	 * @see String#isBlank()
	 */
	public static boolean isBlank(String s)
	{
		return s == null || s.isBlank();
	}

	
	/**
	 * @return if the CharSequence is null or empty.
	 * @param s a CharSequence
	 */
	public static boolean isEmpty(CharSequence s)
	{
		return s == null || s.length() == 0;
	}

	
	/**
	 * @return if the array is null or empty.
	 * @param array an array
	 */
	public static boolean isEmpty(Object[] array)
	{
		return array == null || array.length == 0;
	}
	

	/**
	 * @return if the collection is null or empty.
	 * @param coll a collection
	 */
	public static boolean isEmpty(Collection<?> coll)
	{
		return coll == null || coll.isEmpty();
	}


	/**
	 * @return if the map is null or empty.
	 * @param map a Map
	 */
	public static boolean isEmpty(Map<?,?> map)
	{
		return map == null || map.isEmpty();
	}




	/**
	 * Creates a new array.
	 * @param <T> the type of the array elements
	 * @param componentType the type of the array elements
	 * @param length the new array length
	 * @return the new array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<T> componentType, int length)
	{
		return (T[])Array.newInstance(componentType, length);
	}


	/**
	 * Creates a new array which has the same component type as the given array.
	 * @param <T> the type of the array elements
	 * @param array another array
	 * @param length the new array length
	 * @return the new array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(T[] array, int length)
	{
		return newArray((Class<T>)array.getClass().getComponentType(), length);
	}


	/**
	 * Creates a new HashSet and adds the values to it.
	 * Of course there is {@link Set#of(Object...)} but that method returns 
	 * an unmodifiable Set which also does not allow nulls.
	 * @param <T> the type of the values
	 * @param values some values
	 * @return the HashSet
	 */
	public static <T> HashSet<T> newHashSet(@SuppressWarnings("unchecked") T... values)
	{
		HashSet<T> set = new HashSet<>(values.length);
		Collections.addAll(set, values);
		return set;
	}

	
	/**
	 * If the String is not null, it it is trimmed. If the trimmed String is 
	 * empty it is set to null.
	 * @param s a String
	 * @return the trimmed String, or null if it was null or the trimmed String is empty
	 */
	public static String norm(String s)
	{
		if (s != null)
		{
			s = s.trim();
			if (s.length() == 0)
				s = null;
		}
		return s; 
	}

	
	/**
	 * @return the String itself if it is not null, else returns the empty string.
	 * @param s a String
	 */
	public static String notNull(String s)
	{
		return s != null ? s : ""; 
	}
	
	
	/**
	 * Returns the package part of the name of a class.
	 * @param type a class
	 * @return the package, or "" if in the default package, or null if the class is null
	 */
	public static String packageName(Class<?> type)
	{
		Check.notNull(type, "type");
		return packageName(type.getName());
	}


	/**
	 * Returns the package part of a class name.
	 * @param className the name of a class
	 * @return the package, or "" if in the default package, or null if the className is null
	 */
	public static String packageName(String className)
	{
		Check.notNull(className, "className");
		return beforeLastOr(className, '.', "");
	}
	
	
	/**
	 * Pads the given string on the end with the specified character until the string reaches the requested length.
	 * If the string is longer than the requested length it is returned unchanged.	
	 * @param s the original string to be padded
	 * @param newlen the requested minimal length of the resulting string
	 * @param fillChar the character to pad the string with
	 * @return the new string
	 */
	public static String padEnd(String s, int newlen, char fillChar)
	{
		return pad(s, newlen, fillChar, false);
	}
		
	
	/**
	 * Pads the given string on the end with a space character until the string reaches the requested length.
	 * If the string is longer than the requested length it is returned unchanged.	
	 * @param s the original string to be padded
	 * @param newlen the requested minimal length of the resulting string
	 * @return the new string
	 */
	public static String padEnd(String s, int newlen)
	{
		return padEnd(s, newlen, ' ');
	}

	
	/**
	 * Converts the number to a string nad pads on the end with a '0' character until the string reaches the requested length.
	 * @param n a number
	 * @param newlen the requested minimal length of the resulting string
	 * @return the new string
	 */
	public static String padEnd(long n, int newlen)
	{
		return padEnd(String.valueOf(n), newlen, '0');
	}


	/**
	 * Pads the given string on the start with a space character until the string reaches the requested length.
	 * If the string is longer than the requested length it is returned unchanged.	
	 * @param s the original string to be padded
	 * @param newlen the requested minimal length of the resulting string
	 * @return the new string
	 */
	public static String padStart(String s, int newlen)
	{
		return padStart(s, newlen, ' ');
	}

	
	/**
	 * Pads the given string on the start with the specified character until the string reaches the desired length.
	 * If the string is longer than the requested length it is returned unchanged.	
	 * @param s the original string to be padded
	 * @param newlen the requested minimal length of the resulting string
	 * @param fillChar the character to pad the string with
	 * @return the new string
	 */
	public static String padStart(String s, int newlen, char fillChar)
	{
		return pad(s, newlen, fillChar, true);
	}


	/**
	 * Converts the number to a string nad pads on the start with a '0' character until the string reaches the requested length.
	 * @param n a number
	 * @param newlen the requested minimal length of the resulting string
	 * @return the new string
	 */
	public static String padStart(long n, int newlen)
	{
		return padStart(String.valueOf(n), newlen, '0');
	}


	/**
	 * Converts the number to a string nad pads on the start with the specified character until the string reaches the requested length.
	 * @param n a number
	 * @param newlen the requested minimal length of the resulting string
	 * @param fillChar the character to pad the string with
	 * @return the new string
	 */
	public static String padStart(long n, int newlen, char fillChar)
	{
		return padStart(String.valueOf(n), newlen, fillChar);
	}

	
	private static String pad(String s, int newlen, char padChar, boolean start)
	{
		int curlen = s.length();
		if (newlen > curlen)
		{
			String r = repeat(padChar, newlen - curlen);
			return start ? r + s : s + r;
		}
		else
			return s;
	}

	
	/**
	 * @return a character replicated certain times.
	 * @param c a character
	 * @param times how often the character is replicated
	 */
	public static String repeat(char c, int times)
	{
		return repeat(c, times, null).toString();
	}


	/**
	 * Replicates a character a certain times and appends to the StringBuilder
	 * @param c a character
	 * @param times how often the character is replicated
	 * @param sb a StringBuilder, if null a new StringBuilder is created
	 * @return the StringBuilder
	 */
	public static StringBuilder repeat(char c, int times, StringBuilder sb)
	{
		if (times < 0)
			times = 0;
		if (sb == null)
			sb = new StringBuilder(times);
		else
			sb.ensureCapacity(sb.length() + times);
	 	for (int i=0; i<times; i++)
			sb.append(c);
		return sb;
	}


	/**
	 * @return a string which starts with an lower case character. If the string is empty
	 * 		or null it is simply returned.
	 * @param s a String
	 */
	public static String startLowerCase(String s)
	{
		return s == null || s.length() == 0 || Character.isLowerCase(s.charAt(0)) ?
			s :
			Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}

	
	/**
	 * @return a string which starts with an upper case character. If the string is empty
	 * 		or null it is simply returned.
	 * @param s a String
	 */
	public static String startUpperCase(String s)
	{
		return s == null || s.length() == 0 || Character.isUpperCase(s.charAt(0)) ?
			s :
			Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
	

	/**
	 * @return the String with whitespace removed from its end. 
	 * @param s a String
	 */
	public static String trimEnd(String s)
	{
		if (s != null)
		{
			int last = s.length() - 1;
			int i = last;
	
			while ((i >= 0) && Character.isWhitespace(s.charAt(i)))
				i--;
	
			if (i < last)
				return s.substring(0, i + 1);
		}
		return s;
	}


	/**
	 * @return the String with whitespace removed from its start. 
	 * @param s a String
	 */
	public static String trimStart(String s)
	{
		if (s != null)
		{
			int len = s.length();
			int i = 0;
	
			while ((i < len) && Character.isWhitespace(s.charAt(i)))
				i++;

			if (i > 0)
				return s.substring(i, len);
		}
		return s;
	}


	/**
	 * @return the provided items as array. 
	 * @param <T> the item type
	 * @param items the items
	 */
	@SafeVarargs
	public static <T> T[] toArray(T... items)
	{
		return items;
	}


	/**
	 * @return the collection as an array or an empty String array if the collection is null.
	 * @param coll a collection
	 */
	public static String[] toArray(Collection<String> coll)
	{
		return toArray(coll, String.class);
	}


	/**
	 * @return the collection as an array or an empty array if the collection is null.
	 * @param coll a collection
	 * @param itemType the type of the items in the collection
	 * @param<T> the item type
	 */
	public static <T> T[] toArray(Collection<? extends T> coll, Class<T> itemType)
	{
		Check.notNull(itemType, "itemType");
		T[] array = newArray(itemType, coll != null ? coll.size() : 0);
		return coll != null ? coll.toArray(array) : array;
	}
}
