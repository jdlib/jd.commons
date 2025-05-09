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


import static jd.commons.check.CheckHelper.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import jd.commons.io.FilePath;


/**
 * Provides static utility methods to check arguments.
 * Typically you will pass a variable and a description or the name of that variable:
 * <code>
 * String title = ...
 * Check.notNull(title, "title"); 
 * </code> 
 * If the check fails a IllegalArgumentException with a descriptive message is thrown.
 * In the above example, if title is null then a IEA with message "title is null" will be thrown.
 * <p>
 * Some of the check methods will perform a check on the provided argument:
 * <code>
 * List&lt;Item&gt; items = ...
 * Check.notEmpty(items, "items");
 * </code>
 * Other check return a specialized Check object to provide a fluent API to check the variable under test:
 * <code>
 * Check.size(items, "items").greater(5).less(10);
 * </code>
 */
public interface Check
{
	/**
	 * @return a CheckElems object which allows to check the elements of the given array argument.
	 * @param arg the array argument
	 * @param what describes the argument
	 * @param <T> the element type
	 */
	public static <T> CheckElems<T> elems(T[] arg, String what)
	{
		// can't use List.of(arg) since it does not allow null elems
		return new CheckElems<>(Arrays.asList(notNull(arg, what)), what);
	}
	
	
	/**
	 * @return a CheckElems object which allows to check the elements of the given Iterable.
	 * @param arg the iterable
	 * @param what describes the argument
	 * @param <T> the element type
	 */
	public static <T> CheckElems<T> elems(Iterable<T> arg, String what)
	{
		return new CheckElems<>(arg, what);
	}

	
	/**
	 * Checks that two objects are equal.
	 * @param arg1 the first argument
	 * @param arg2 the second argument
	 * @param<T> the argument type
	 * @return the first argument
	 * @exception IllegalArgumentException if the two objects are not equal
	 */
	public static <T> T equal(T arg1, T arg2)
	{
		if (!Objects.equals(arg1, arg2))
			throw new IllegalArgumentException(argString(arg1) + " not equal to " + argString(arg2));
		return arg1;
	}


	/**
	 * @return a CheckFile object which allows to check the given File argument.
	 * @param arg a file 
	 */
	public static CheckFile file(File arg)
	{
		return file(arg, null);
	}
	
	
	/**
	 * @return a CheckFile object which allows to check the given File argument.
	 * @param arg a file
	 * @param what describes the argument
	 */
	public static CheckFile file(File arg, String what)
	{
		return new CheckFile(arg, what);
	}
	
	
	/**
	 * @return a CheckIndex object which allows to check the given index argument.
	 * @param arg a index argument 
	 */
	public static CheckIndex index(int arg)
	{
		return index(arg, null);
	}


	/**
	 * @return a CheckIndex object which allows to check the given index argument.
	 * @param arg a index argument 
	 * @param what describes the index or null
	 */
	public static CheckIndex index(int arg, String what)
	{
		return new CheckIndex(arg, what);
	}

	
	/**
	 * Checks that an object is an instance of a class.
	 * @param arg the argument
	 * @param type the expected class
	 * @param <T> the class type
	 * @return the argument casted to T
	 * @exception IllegalArgumentException if the object is not an instance of the class.
	 */
	public static <T> T isA(Object arg, Class<T> type)
	{
		return isA(arg, type, null);
	}

	
	/**
	 * Checks that an object is an instance of a class.
	 * @param arg the argument
	 * @param type the expected class
	 * @param what describes the argument
	 * @param <T> the class type
	 * @return the argument casted to T
	 * @exception IllegalArgumentException if the object is not an instance of the class.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T isA(Object arg, Class<T> type, String what)
	{
		notNull(arg, what);
		notNull(type, "type");
		if (!type.isAssignableFrom(arg.getClass()))
			throw isAFailure(arg, type, what);
		return (T)arg;
	}


	private static IllegalArgumentException isAFailure(Object arg, Class<?> expected, String what)
	{
		Class<?> actual = arg.getClass();
		StringBuilder msg = new StringBuilder();
		if (what != null)
			msg.append(CheckHelper.normWhat(what)).append(' ');
		msg.append(actual.getName()).append(' ').append(CheckHelper.argString(arg));	
		msg.append(" is not a ").append(expected.getName());
		if (actual.getName().equals(expected.getName()) &&
			actual.getClassLoader() != expected.getClassLoader())
		{
			msg.append(" and was loaded by ").append(actual.getClassLoader()).append(" and not by ").append(expected.getClassLoader());
		}
		return new IllegalArgumentException(msg.toString());
	}


	/**
	 * Checks that a boolean argument is false.
	 * @param arg the argument
	 * @param what describes the argument
	 * @exception IllegalArgumentException if the argument is true.
	 */
	public static void isFalse(boolean arg, String what)
	{
		if (arg)
			throw new IllegalArgumentException(CheckHelper.normWhat(what) + " is true");
	}


	/**
	 * Checks that an argument is null.
	 * @param arg the argument
	 * @param what describes the argument.
	 * @exception IllegalArgumentException if the argument is not null.
	 */
	public static void isNull(Object arg, String what)
	{
		if (arg != null)
			throw new IllegalArgumentException(normWhat(what) + " is " + argString(arg) + " and not null");
	}


	/**
	 * Checks that {@code argType} is derived from the {@code superType} 
	 * i.e. that {@code superType} is {@link Class#isAssignableFrom(Class)} from {@code argType}.
	 * @param argType a argument type, not null
	 * @param superType a potential super class or interface of {@code argType}
	 * @param<T> the type of {@code superType}
	 * @exception IllegalArgumentException if the argument class is not {@link Class#isAssignableFrom(Class) derived} from the super class
	 * @return the argument class casted to the super class
	 * @see Class#isAssignableFrom(Class)
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> derivedFrom(Class<?> argType, Class<T> superType)
	{
		Check.notNull(argType, "arg");
		Check.notNull(superType, "superType");
		if (!superType.isAssignableFrom(argType))
			throw derivedFromError(argType, superType);
		return (Class<T>)argType;
	}


	private static IllegalArgumentException derivedFromError(Class<?> argType, Class<?> superType)
	{
		boolean showLoader = 
			argType.getName().equals(superType.getName()) && 
			argType.getClassLoader() != superType.getClassLoader();
		
		StringBuilder sb = new StringBuilder();
		describeClass(argType, showLoader, sb);
		sb.append(" is not derived from ");
		describeClass(superType, showLoader, sb);
		return new IllegalArgumentException(sb.toString());
	}
	
	
	private static void describeClass(Class<?> c, boolean showLoader, StringBuilder s)
	{
		s.append(c.getName());
		if (showLoader)
			s.append('[').append(c.getClassLoader()).append(']');
	}
	
	
	/**
	 * Checks that a boolean argument is true.
	 * @param arg the argument
	 * @param what describes the argument
	 * @exception IllegalArgumentException if the argument is false.
	 */
	public static void isTrue(boolean arg, String what)
	{
		if (!arg)
			throw new IllegalArgumentException(normWhat(what) + " is false");
	}


	/**
	 * Returns a CheckSize object which allows to check the CharSequence length.
	 * @param arg a CharSequence
	 * @param what describes the argument
	 * @return the CheckSize object
	 */
	public static CheckSize length(CharSequence arg, String what)
	{
		notNull(arg, what);
		return size(arg.length(), what, false);
	}
	
	
	/**
	 * Returns a CheckSize object which allows to check the CharSequence length.
	 * @param arg a CharSequence
	 * @param what describes the argument
	 * @return the CheckSize object
	 */
	public static CheckSize length(File arg, String what)
	{
		notNull(arg, what);
		return size(arg.length(), what, false);
	}

	
	/**
	 * Returns a CheckSize object which allows to check the array length.
	 * @param arg an array
	 * @param what describes the argument
	 * @return the CheckSize object
	 */
	public static CheckSize length(Object[] arg, String what)
	{
		notNull(arg, what);
		return size(arg.length, what, false);
	}

	
	/**
	 * Returns a CheckLong object which allows to check the length value.
	 * @param arg a length value. The value is automatically checked to be &gt;= 0.
	 * @param what describes the argument
	 * @return the CheckLong object
	 */
	public static CheckSize length(long arg, String what)
	{
		CheckSize cs = size(arg, what, false);
		cs.greaterEq(0);
		return cs;
	}

	
	/**
	 * Checks that a CharSequence argument is not blank, i.e. is not null and not empty and not whitespace
	 * only.
	 * @param arg the argument
	 * @param what describes the argument
	 * @param<T> the CharSequence type
	 * @return the argument
	 * @exception IllegalArgumentException if the argument is blank.
	 */
	public static <T extends CharSequence> T notBlank(T arg, String what)
	{
		notEmpty(arg, what);
		if (onlyWhiteSpace(arg))
			throw new IllegalArgumentException(normWhat(what) + " is blank");
		return arg;
	}


	private static boolean onlyWhiteSpace(CharSequence s)
	{
		int len = s.length();
		for (int i=0; i<len; i++)
		{
			if (!Character.isWhitespace(s.charAt(i)))
				return false;
		}
		return true;
	}

	
	/**
	 * Checks that an array argument is not null and not empty, i.e. has length &gt; 0.
	 * @param arg the argument
	 * @param what describes the argument.
	 * @param<T> the array element type
	 * @return the argument
	 * @exception IllegalArgumentException if the argument is null or empty.
	 */
	public static <T> T[] notEmpty(T[] arg, String what)
	{
		notNull(arg, what);
		notEmpty(arg.length, what);
		return arg;
	}


	/**
	 * Checks that an array argument is not null and not empty, i.e. has length &gt; 0.
	 * @param arg the argument
	 * @param what describes the argument.
	 * @return the argument
	 * @exception IllegalArgumentException if the argument is null or empty.
	 */
	public static double[] notEmpty(double[] arg, String what)
	{
		notNull(arg, what);
		notEmpty(arg.length, what);
		return arg;
	}

	
	/**
	 * Checks that an array argument is not null and not empty, i.e. has length &gt; 0.
	 * @param arg the argument
	 * @param what describes the argument.
	 * @return the argument
	 * @exception IllegalArgumentException if the argument is null or empty.
	 */
	public static long[] notEmpty(long[] arg, String what)
	{
		notNull(arg, what);
		notEmpty(arg.length, what);
		return arg;
	}

	
	/**
	 * Checks that an array argument is not null and not empty, i.e. has length &gt; 0.
	 * @param arg the argument
	 * @param what describes the argument.
	 * @return the argument
	 * @exception IllegalArgumentException if the argument is null or empty.
	 */
	public static int[] notEmpty(int[] arg, String what)
	{
		notNull(arg, what);
		notEmpty(arg.length, what);
		return arg;
	}

	
	/**
	 * Checks that an array argument is not null and not empty, i.e. has length &gt; 0.
	 * @param arg the argument
	 * @param what describes the argument.
	 * @return the argument
	 * @exception IllegalArgumentException if the argument is null or empty.
	 */
	public static char[] notEmpty(char[] arg, String what)
	{
		notNull(arg, what);
		notEmpty(arg.length, what);
		return arg;
	}

	
	/**
	 * Checks that an array argument is not null and not empty, i.e. has length &gt; 0.
	 * @param arg the argument
	 * @param what describes the argument.
	 * @return the argument
	 * @exception IllegalArgumentException if the argument is null or empty.
	 */
	public static byte[] notEmpty(byte[] arg, String what)
	{
		notNull(arg, what);
		notEmpty(arg.length, what);
		return arg;
	}

	
	/**
	 * Checks that a CharSequence argument is not null and not empty, i.e. has length &gt; 0.
	 * @param arg the argument
	 * @param what describes the argument
	 * @param<T> the type of the CharSequence
	 * @return the argument
	 * @exception IllegalArgumentException if the argument is null or empty.
	 */
	public static <T extends CharSequence> T notEmpty(T arg, String what)
	{
		notNull(arg, what);
		notEmpty(arg.length(), what);
		return arg;
	}


	/**
	 * Checks that a Collection argument is not null and not empty, i.e. has size &gt; 0.
	 * @param arg the argument
	 * @param what describes the argument
	 * @param<T> the type of the collection
	 * @return the argument
	 * @exception IllegalArgumentException if the argument is null or empty
	 */
	public static <T extends Collection<?>> T notEmpty(T arg, String what)
	{
		notNull(arg, what);
		notEmpty(arg.size(), what);
		return arg;
	}


	/**
	 * Checks that a Map argument is not null and not empty, i.e. has size &gt; 0.
	 * @param arg the argument
	 * @param what describes the argument.
	 * @param <T> the map type
	 * @return the argument
	 * @exception IllegalArgumentException if the argument is null or empty.
	 */
	public static <T extends Map<?, ?>> T notEmpty(T arg, String what)
	{
		notNull(arg, what);
		notEmpty(arg.size(), what);
		return arg;
	}


	/**
	 * Checks that a length or size of an argument is positive.
	 * @param length the length or size argument
	 * @param what describes the argument
	 * @return the length
	 */
	public static int notEmpty(int length, String what)
	{
		if (length <= 0)
			throw new IllegalArgumentException(normWhat(what) + " is empty");
		return length;
	}

	
	/**
	 * Checks that two objects are not equal.
	 * @param arg1 the first argument
	 * @param arg2 the second argument
	 * @param<T> the type of the objects
	 * @return the first argument
	 * @exception IllegalArgumentException if two values are not equal.
	 */
	public static <T> T notEqual(T arg1, T arg2)
	{
		if (Objects.equals(arg1, arg2))
			throw new IllegalArgumentException(argString(arg1) + " equal");
		return arg1;
	}

	
	/**
	 * Checks that an argument object is not null.
	 * @param arg the argument
	 * @param what describes the argument
	 * @param <T> the object type
	 * @return the object
	 * @exception IllegalArgumentException if the object is null.
	 */
	public static <T> T notNull(T arg, String what)
	{
		if (arg == null)
			throw new IllegalArgumentException(normWhat(what) + " is null");
		return arg;
	}


	/**
	 * Checks that two objects are not same.
	 * @param arg1 the first argument
	 * @param arg2 the second argument
	 * @param <T> the type of the first argument
	 * @exception IllegalArgumentException if two values do not refer to the same object
	 * @return the first argument
	 */
	public static <T> T notSame(T arg1, Object arg2)
	{
		if (arg1 == arg2)
			throw new IllegalArgumentException(argString(arg1) + " same as other arg");
		return arg1;
	}

	
	/**
	 * @return a PathCheck object which allows to check the given Path.
	 * @param path a Path 
	 * @param options LinkOptions used in the checks
	 */
	public static CheckPath path(Path path, LinkOption... options)
	{
		return path(path, null, options);
	}
	
	
	/**
	 * @return a PathCheck object which allows to check the given Path argument.
	 * @param path a Path 
	 * @param what describes the argument
	 * @param options LinkOptions used in the checks
	 */
	public static CheckPath path(Path path, String what, LinkOption... options)
	{
		return new CheckPath(path, what, options);
	}

	
	public static CheckPath path(FilePath path, LinkOption... options)
	{
		return path(path, null, options);
	}
	
	
	/**
	 * @return a PathCheck object which allows to check the given FilePath argument.
	 * @param path a Path 
	 * @param what describes the argument
	 * @param options LinkOptions used in the checks
	 */
	public static CheckPath path(FilePath path, String what, LinkOption... options)
	{
		return new CheckPath(Check.notNull(path, "path").toNioPath(), what, options);
	}
	
	
	/**
	 * Checks that two objects are the same.
	 * @param arg1 the first argument
	 * @param arg2 the second argument
	 * @param <T> the type of the first argument
	 * @exception IllegalArgumentException if two values do not refer to the same object
	 * @return the first argument
	 */
	public static <T> T same(T arg1, Object arg2)
	{
		if (arg1 != arg2)
			throw new IllegalArgumentException(argString(arg1) + " not same as " + argString(arg2));
		return arg1;
	}
	
	
	/**
	 * Returns a CheckSize object which allows to check the collection size.
	 * @param arg a Collection
	 * @param what describes the argument
	 * @return the CheckLong object
	 */
	public static CheckSize size(Collection<?> arg, String what)
	{
		notNull(arg, what);
		return size(arg.size(), what, true);
	}
	
	
	/**
	 * Returns a CheckSize object which allows to check the map size.
	 * @param arg a Map
	 * @param what describes the argument
	 * @return the CheckSize object
	 */
	public static CheckLong size(Map<?,?> arg, String what)
	{
		notNull(arg, what);
		return size(arg.size(), what, true);
	}

	
	/**
	 * Returns a CheckSize object which allows to check the Path size.
	 * @param arg a Path
	 * @param what describes the argument
	 * @return the CheckSize object
	 */
	public static CheckSize size(Path arg, String what)
	{
		notNull(arg, what);
		try
		{
			return size(Files.size(arg), what, true);
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException("can't access size of " + arg, e);
		}
	}
	
	
	/**
	 * Returns a CheckSize object which allows to check the Path size.
	 * @param arg a Path
	 * @param what describes the argument
	 * @return the CheckSize object
	 */
	public static CheckSize size(FilePath arg, String what)
	{
		notNull(arg, what);
		return size(arg.toNioPath(), what);
	}

	
	/**
	 * Returns a CheckSize object which allows to check the size value.
	 * @param arg a size value. The value is automatically checked to be &gt;= 0.
	 * @param what describes the argument
	 * @return the CheckSize object
	 */
	public static CheckSize size(long arg, String what)
	{
		CheckSize cs = size(arg, what, true);
		cs.greaterEq(0);
		return cs;
	}

	
	private static CheckSize size(long size, String what, boolean isSize)
	{
		return new CheckSize(size, what, isSize);
	}
	
	
	/**
	 * Returns a CheckDouble object which allows to check the double argument.
	 * This can also be used to check float values.
	 * @param value the value
	 * @param what describes the argument
	 * @return the CheckDouble object
	 */
	public static CheckDouble value(double value, String what)
	{
		return new CheckDouble(value, what);
	}
	
	
	/**
	 * Returns a CheckInt object which allows to check the int argument.
	 * @param value the value
	 * @param what describes the argument
	 * @return the CheckInt object
	 */
	public static CheckInt value(int value, String what)
	{
		return new CheckInt(value, what);
	}

	
	/**
	 * Returns a CheckLong object which allows to check the long argument.
	 * @param value the value
	 * @param what describes the argument
	 * @return the CheckLong object
	 */
	public static CheckLong value(long value, String what)
	{
		return new CheckLong(value, what);
	}
}
