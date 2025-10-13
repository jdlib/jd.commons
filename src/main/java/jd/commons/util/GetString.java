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


import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;
import jd.commons.check.Check;
import jd.commons.util.function.XFunction;


/**
 * GetString provides a {@link #value() String value}.
 * Additionally it can
 * <ul>
 * <li>guarantee that the string is {@link #notNull() not null} or {@link #notEmpty() not empty}
 * <li>convert the string to a {@link #asInt() variety}  {@link #asEnumOr(Class, Enum) of} {@link #asURL() types}
 * </ul>  
 */
public interface GetString
{
	public static GetString of(String value)
	{
		return of(value, null);
	}
	
	
	public static GetString of(String value, String what)
	{
		return new Simple(value, what);
	}

	
	public String what();
	
	
	/**
	 * @return the value, can be null.
	 */
	public String value();

	
	/**
	 * @return the value, or {@code other} if the value is null.
	 * @param other another value 
	 */
	public default String valueOr(String other)
	{
		String s = value();
		return s != null ? s : other;
	}

	
	public default boolean isNull()
	{
		return value() == null;
	}
	
	
	public default GetString notNull()
	{
		if (isNull())
			throw Helper.illegalArg(this, "not allowed", null);
		return this;
	}

	
	public default boolean isEmpty()
	{
		return Utils.isEmpty(value());
	}

	
	public default GetString notEmpty()
	{
		if (isEmpty())
			throw Helper.illegalArg(this, "not allowed", null);
		return this;
	}

	
	public GetString replaceNull(String other);

	
	/**
     * @return the value converted to the functions result.
     * @param fn a function
     * @param<T> the result type
     */
    public default <T> T asResult(XFunction<String,T,?> fn)
    {
    	Check.notNull(fn, "fn");
    	try
    	{
    		return fn.apply(value());
    	}
    	catch (Exception e)
    	{
    		throw Helper.illegalArg(this, "can't be converted", e);
    	}
    } 

    
    /**
     * @return the value as boolean, using "true" for the true value and "false" for the false value.
     * @throws IllegalArgumentException if the value can't be converted to a boolean
     */
    public default boolean asBoolean()
    {
        return asBoolean("true", "false");
    } 

    
    public default boolean asBooleanOr(boolean defaultValue)
    {
        return isEmpty() ? defaultValue : asBoolean();
    } 

    
    /**
     * @return the value as boolean.
 	 * @param trueValue the string value that corresponds to true
 	 * @param falseValue the string value that corresponds to false
     * @throws IllegalArgumentException if the value can't be converted to a boolean
     */
    public default boolean asBoolean(String trueValue, String falseValue)
    {
    	String value = notNull().value();
        if (value.equals(trueValue))
        	return true;
        else if (value.equals(falseValue))
        	return false;
        else 
        	throw Helper.convertError(this, "boolean (" + trueValue + '/' + falseValue + ')', null);
    } 
    
    
    /**
     * @return the value string as byte.
     * @throws IllegalArgumentException if the value can't be converted to a byte
     */
    public default byte asByte()
    {
        try
		{
        	return Byte.parseByte(notNull().value());
		}
        catch(NumberFormatException e)
		{
    		throw Helper.convertError(this, "byte", e);
		}
    } 
    
    
    /**
     * @return the value as char.
     * @throws IllegalArgumentException if the value can't be converted to a char
     */
    public default char asChar()
    {
    	String s = notNull().value();
    	if (s.length() != 1) 
    		throw Helper.convertError(this, "char", null);
    	return s.charAt(0);
    } 
	
	
    /**
     * Interprets the value as class name and returns the class
     * @param superClass a super class of the class.
     * @param <T> the superclass type
     * @return the class
     */
    public default <T> Class<? extends T> asClass(Class<T> superClass)
    {
    	try
    	{
        	String s = value();
    		return s != null ? ClassLoad.forName(value()).derivedFrom(superClass).get() : null;
    	}
    	catch(Exception e)
    	{
    		throw Helper.convertError(this, "Class", e);
    	}
    }
    
    
    /**
     * @return the value as double.
     * @throws IllegalArgumentException if the value can't be converted to a double
     */
    public default double asDouble()
    {
        try
		{
        	return Double.parseDouble(notNull().value());
		}
        catch(NumberFormatException e)
		{
    		throw Helper.convertError(this, "double", e);
		}
    } 

    
    /**
     * @return the value as double or the default value if the
     * @param defaultValue the default value  
     * @throws IllegalArgumentException if the value can't be converted to a double
     */
    public default double asDoubleOr(double defaultValue)
    {
    	return isEmpty() ? defaultValue : asDouble();
    }
    
    
    /**
     * @return the value as float.
     * @throws IllegalArgumentException if the value can't be converted to a float
     */
    public default float asFloat()
    {
        try
		{
        	return Float.parseFloat(notNull().value());
		}
        catch(NumberFormatException e)
		{
    		throw Helper.convertError(this, "float", e);
		}
    } 

    
    /**
     * @return the value as Enum.
     * @param enumClass the class of the expected Enum
     * @param<E> the enum type
     * @throws IllegalArgumentException if the value can't be converted to the Enum
     */
    public default <E extends Enum<E>> E asEnum(Class<E> enumClass)
    {
    	return asEnumOr(enumClass, null);
    } 

	
    public default <E extends Enum<E>> E asEnumOr(Class<E> enumClass, E defaultValue)
    {
    	Check.notNull(enumClass, "enumClass");
    	String s = value();
    	return s != null ? Enum.valueOf(enumClass, s) : defaultValue;
    }
    
    
    /**
     * @return the value as File.
     */
    public default File asFile()
    {
    	String s = value();
    	return s != null ? new File(s) : null;
    } 
    
    
	/**
     * @return the value as int.
     * @throws IllegalArgumentException if the value can't be converted to an int
     */
    public default int asInt()
    {
        try
		{
        	return Integer.parseInt(notNull().value());
		}
        catch(NumberFormatException e)
		{
    		throw Helper.convertError(this, "int", e);
		}
    } 
    
    
    public default int asIntOr(int defaultValue)
    {
    	return isEmpty() ? defaultValue : asInt();
    }
    
    
	public default String[] asSplit(String splitPattern)
	{
		return asSplit(Pattern.compile(splitPattern));
	}

	
	public default String[] asSplit(Pattern splitPattern)
	{
		String s = value();
		return s != null ? splitPattern.split(s) : new String[0];
	}
	
	
    /**
     * @return the value as long.
     * @throws IllegalArgumentException if the value can't be converted to a long
     */
    public default long asLong()
    {
        try
		{
        	return Long.parseLong(notNull().value());
		}
        catch(NumberFormatException e)
		{
    		throw Helper.convertError(this, "long", e);
		}
    } 
    
    
    public default long asLongOr(long defaultValue)
    {
    	return isEmpty() ? defaultValue : asLong();
    }
    

    /**
     * @return the value as URL.
     */
    public default URI asURI()
    {
    	String s = value();
		return s != null ? URI.create(s) : null;
    } 

    
    /**
     * @return the value as URL.
     */
    public default URL asURL()
    {
    	String s = value();
    	try
		{
			return s != null ? new URL(s) : null;
		}
		catch (MalformedURLException e)
		{
			throw new IllegalArgumentException(e.getMessage(), e);
		}
    } 

    
    public static class Simple implements GetString
	{
		private String value_;
		private final String what_;
		
		
		public Simple(String value, String what)
		{
			value_ = value;
			what_  = what;
		}
		

		@Override
		public String what()
		{
			return what_;
		}
		

		@Override
		public String value()
		{
			return value_;
		}
		
		
		@Override
		public GetString replaceNull(String other)
		{
			if (value_ == null)
				value_ = other;
			return this;
		}
		
		
		@Override
		public String toString()
		{
			return Helper.toDisplay(value_);
		}
	}
	
	
	public interface Helper
	{
	    /**
	     * @return a new IllegalArgumentException for a conversion error
	     * @param ga a GetString object
	     * @param expectedType the expected type
	     * @param cause the cause
	     */
	    public static IllegalArgumentException convertError(GetString ga, String expectedType, Exception cause)
		{
			return illegalArg(ga, "can't be converted to " + expectedType, cause);
		}

	
		public static IllegalArgumentException illegalArg(GetString ga, String msg, Exception cause)
		{
			StringBuilder sb = new StringBuilder();
			String what = ga.what();
			if (what != null)
				sb.append(what).append(' ');
			toDisplay(ga.value(), sb);
			sb.append(' ').append(msg);
			return new IllegalArgumentException(sb.toString(), cause);
		}

		
		public static String toDisplay(String value)
		{
			return toDisplay(value, new StringBuilder()).toString();
		}
		
		
		public static StringBuilder toDisplay(String value, StringBuilder sb)
		{
			if (value == null)
				sb.append("null");
			else
			{
				sb.append('"');
				int len = Math.min(value.length(), 50); 
				for (int i=0; i<len; i++)
				{
					char c = value.charAt(i);
					switch(c)
					{
						case '\b':	sb.append("\\b"); break;
						case '\f':	sb.append("\\f"); break;
						case '\n':	sb.append("\\n"); break;
						case '\r':	sb.append("\\r"); break;
						case '\t':	sb.append("\\t"); break;
						case '\\':	sb.append("\\\\"); break;
						case '"':	sb.append("\\\""); break;
						default:	sb.append(c); break;
					}
				}
				sb.append('"');
				if (len < value.length())
					sb.append("+<").append(value.length() - len).append(" more>");
			}
			return sb;
		}
	}
}
