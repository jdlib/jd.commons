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
package jd.commons.io;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Function;
import jd.commons.check.Check;
import jd.commons.util.function.XFunction;


/**
 * ResourceLoader can return a URL or InputStream for a name of a classpath resource.
 */
public interface ResourceLoader
{
	/**
	 * @return a ResourceLoader which is based on the {@link Thread#getContextClassLoader() context ClassLoader}.
	 * For each invocation the context ClassLoadder is retrieved again. 
	 * If there is no context ClassLoader the system ClassLoader is used. 
	 */
	public static ResourceLoader context()
	{
		return ResLoaderByContextCL.INSTANCE;
	}

	
	/**
	 * @return a ResourceLoader which is based on the {@link ClassLoader#getSystemClassLoader() system classloader}.
	 */
	public static ResourceLoader system()
	{
		return ResLoaderByCL.SYSTEM;
	}
	
	
	/**
	 * @return a ResourceLoader which is based on the {@link ClassLoader#getSystemClassLoader() platform classloader}.
	 */
	public static ResourceLoader platform()
	{
		return ResLoaderByCL.PLATFORM;
	}

	
	/**
	 * @return a ResourceLoader which is based on the ClassLoader of this class.
	 */
	public static ResourceLoader own()
	{
		return ResLoaderByCL.OWN;
	}

	
	/**
	 * @param type a Class
	 * @return a ResourceLoader for the class
	 */
	public static ResourceLoader of(Class<?> type)
	{
		return new ResLoaderByClass(type);
	}
	
	
	/**
	 * @param classLoader a ClassLoader
	 * @return a ResourceLoader for the ClassLoader
	 */
	public static ResourceLoader of(ClassLoader classLoader)
	{
		return new ResLoaderByCL(classLoader);
	}

	
	// think ServletContext
	/**
	 * @return a ResourceLoader which is based the given functions to
	 * 		return an InputStream and URL for a resource.
	 * @param name a name for the ResourceLoader (used in its toString() method)
	 * @param inputStream a function which returns an InputStream for a resource name or null if the resource does not exist
	 * @param url a function which returns an InputStream for a resource name or null if the resource does not exist 
	 */
	public static ResourceLoader of(String name,
		XFunction<String,InputStream,IOException> inputStream,
		Function<String,URL> url)
	{
		return new GenericResLoader(name, inputStream, url);
	}

	
	/**
	 * @return a ResourceLoader which always returns null/fails.
	 */
	public static ResourceLoader nop()
	{
		return GenericResLoader.NOP;
	}

	
	/**
	 * Returns an InputStream to the resource or null if it not found.
	 * @param name the resource name
	 * @return an InputStream or null
	 * @throws IOException if an I/O error occurs
	 */
	public InputStream getInputStreamOrNull(String name) throws IOException;

	
	/**
	 * @return an InputStream to the resource.
	 * @param name the resource name
	 * @throws IOException if an I/O error occurs or if the resource cannot be found
	 */
	public default InputStream getInputStream(String name) throws IOException
	{
		InputStream in = getInputStreamOrNull(name);
		if (in == null)
			throw new IOException("resource '" + name + "' not found");
		return in;
	}

	
	/**
	 * Returns an URL to the resource or null if it not found.
	 * @param name the resource name
	 * @return an URL or null
	 */
	public URL getURL(String name);
}


class ResLoaderByClass implements ResourceLoader
{
	private final Class<?> class_;
	
	
	public ResLoaderByClass(Class<?> c)
	{
		class_ = Check.notNull(c, "class");
	}
	
	
	@Override
	public InputStream getInputStreamOrNull(String name) throws IOException
	{
		return class_.getResourceAsStream(name);
	}
	

	@Override
	public URL getURL(String name)
	{
		return class_.getResource(name);
	}
	
	
	@Override
	public int hashCode()
	{
		return class_.hashCode();
	}
	
	
	@Override
	public boolean equals(Object other)
	{
		return other instanceof ResLoaderByClass && ((ResLoaderByClass)other).class_ == class_;
	}
	
	
	@Override
	public String toString()
	{
		return "Loader[" + class_.getName() + ']';
	}
}


class ResLoaderByContextCL implements ResourceLoader
{
	public static final ResLoaderByContextCL INSTANCE = new ResLoaderByContextCL();
	
		
	private ResLoaderByContextCL()
	{
	}
	
	
	private ClassLoader classLoader()
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return loader != null ? loader : ClassLoader.getSystemClassLoader();
	}

	
	@Override
	public InputStream getInputStreamOrNull(String name) throws IOException
	{
		return classLoader().getResourceAsStream(name);
	}
	

	@Override
	public URL getURL(String name)
	{
		return classLoader().getResource(name);
	}
	
	
	@Override
	public String toString()
	{
		return "Loader[" + "<context>" + ']';
	}
}


class ResLoaderByCL implements ResourceLoader
{
	public static final ResLoaderByCL SYSTEM   	= new ResLoaderByCL(ClassLoader.getSystemClassLoader(), "<system>");
	public static final ResLoaderByCL PLATFORM 	= new ResLoaderByCL(ClassLoader.getPlatformClassLoader(), "<platform>");
	public static final ResLoaderByCL OWN 		= new ResLoaderByCL(ResourceLoader.class.getClassLoader());
	

	private final ClassLoader classLoader_;
	private final Object name_;
	
	
	public ResLoaderByCL(ClassLoader classLoader)
	{
		this(Check.notNull(classLoader, "classLoader"), null);
	}
	
	
	private ResLoaderByCL(ClassLoader classLoader, Object name)
	{
		classLoader_ = Check.notNull(classLoader, "classLoader");
		name_ 		 = name != null ? name : classLoader;
	}

	
	@Override
	public InputStream getInputStreamOrNull(String name) throws IOException
	{
		return classLoader_.getResourceAsStream(name);
	}
	

	@Override
	public URL getURL(String name)
	{
		return classLoader_.getResource(name);
	}
	
	
	@Override
	public int hashCode()
	{
		return classLoader_.hashCode();
	}
	
	
	@Override
	public boolean equals(Object other)
	{
		return other instanceof ResLoaderByCL && ((ResLoaderByCL)other).classLoader_ == classLoader_;
	}
	
	
	@Override
	public String toString()
	{
		return "Loader[" + name_ + ']';
	}
}


class GenericResLoader implements ResourceLoader
{
	public static final GenericResLoader NOP = new GenericResLoader("nop", s -> null, s -> null);
	
	
	private final String name_;
	private final XFunction<String,InputStream,IOException> inputStream_;
	private final Function<String,URL> url_;
	
	
	public GenericResLoader(String name, XFunction<String,InputStream,IOException> inputStream,
		Function<String,URL> url)
	{
		name_ = Check.notNull(name, "name");
		inputStream_ = Check.notNull(inputStream, "inputStream");
		url_ = Check.notNull(url, "url");
	}
	
	
	@Override
	public InputStream getInputStreamOrNull(String name) throws IOException
	{
		return inputStream_.apply(name);
	}
	

	@Override
	public URL getURL(String name)
	{
		return url_.apply(name);
	}
	
	
	@Override
	public int hashCode()
	{
		return name_.hashCode();
	}
	
	
	@Override
	public boolean equals(Object other)
	{
		return other instanceof GenericResLoader && ((GenericResLoader)other).name_.equals(name_);
	}
	
	
	@Override
	public String toString()
	{
		return "Loader['" + name_ + "']";
	}
}