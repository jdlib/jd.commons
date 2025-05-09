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


import java.util.function.Function;
import jd.commons.check.Check;


/**
 * A builder class which helps to load classes from class names.
 * @see #forName(String)
 * @param <T> the type of the loaded class
 */
public class ClassLoad<T>
{
	protected final String className_;
	protected Class<T> derivedFrom_;
	protected ClassLoader classLoader_ = ClassLoad.class.getClassLoader();
	

	/**
	 * @return a new ClassLoad object 
	 * @param className the name of the class to be loaded
     */
	public static ClassLoad<?> forName(String className)
	{
		return new ClassLoad<>(className);
	}
	
	
	protected ClassLoad(String className)
	{
		className_ = Check.notEmpty(className, "className");
	}
	

	/**
	 * Specifies a super class of the class to be loaded.
	 * @param derivedFrom a superclass
	 * @return this
	 * @param <D> the super class type
	 */ 
	@SuppressWarnings("unchecked")
	public <D> ClassLoad<D> derivedFrom(Class<D> derivedFrom)
	{
		derivedFrom_ = (Class<T>)derivedFrom;
		return (ClassLoad<D>)this;
	}
	
	
	/**
	 * Specifies the ClassLoader used to load the class.
	 * @param classLoader a ClassLoader, not null 
	 * @return this
	 * @see ClassLoader#loadClass(String)
	 */ 
	public ClassLoad<T> using(ClassLoader classLoader)
	{
		classLoader_ = Check.notNull(classLoader, "classLoader");
		return this;
	}

	
	/**
	 * Loads and returns the specified class.
	 * @return the class
	 * @throws ClassNotFoundException if the class was not found 
	 * @throws IllegalArgumentException if the class is not derived from the given superClass. 
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends T> get() throws ClassNotFoundException, ClassCastException
	{
		Class<?> raw = classLoader_.loadClass(className_);
		return derivedFrom_ != null ?
			Check.derivedFrom(raw, derivedFrom_) :
			(Class<T>)raw;
	}
	
	
	public Class<? extends T> orNull()
	{
		try
		{
			return get();
		}
		catch(Exception e)
		{
			return null;
		}
	}


	/**
	 * Returns the class or throws a customer exception.
	 * @param converter converts an Exception into a target exception.
	 * @return the loaded class
	 * @throws E if the class cannot be loaded
	 * @param <E> the type of the exception thrown
	 */
	public <E extends Throwable> Class<? extends T> orThrow(Function<Exception,E> converter) throws E
	{
		Check.notNull(converter, "converter");
		try
		{
			return get();
		}
		catch(Exception e)
		{
			throw converter.apply(e);
		}
	}
}
