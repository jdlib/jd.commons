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
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.ByteSource;
import jd.commons.util.Utils;


/**
 * Models a classpath resource.
 * Provides builder methods for easy creation and loading.
 * @see #of()
 */
public class Resource implements ByteSource
{
	private String name_ = "";
	private ResourceLoader loader_ = ResourceLoader.own();
	private URL url_;
	private boolean urlRetrieved_;


	/**
	 * @return a Resource with an empty name loaded by the {@link ResourceLoader#own() own ResourceLoader}.
	 * Use {@link #path(String)} methods to append to the name.
	 */
	@CheckReturnValue
	public static Resource of()
	{
		return of((String)null);
	}


	/**
	 * @return a Resource identified by the given name,
	 * 		loaded by the {@link ResourceLoader#own() own ResourceLoader}.
	 * @param name the Resource name
	 */
	@CheckReturnValue
	public static Resource of(String name)
	{
		return new Resource(name, null);
	}


	/**
	 * @return a Resource constructed by the given path parts,
	 * 		loaded by the {@link ResourceLoader#own() own ResourceLoader}.
	 * @param parts the parts making up the resource name
	 * @see #path(String...)
	 */
	@CheckReturnValue
	public static Resource of(String... parts)
	{
		return of().path(parts);
	}


	/**
	 * @return a Resource for the class file of the given class.
	 * @param c a class, not null
	 */
	@CheckReturnValue
	public static Resource ofClassFile(Class<?> c)
	{
		Check.notNull(c, "class");
		// two caveats:
		// - use a ResourceLoader based on the class not its ClassLoader
		//   since for classes loaded by the bootstrap ClassLoader
		//   the returned ClassLoader is null
		// - don't use the simple class name and relative loading since
		//   for inner classes the simple clas name does not contain the outer class names
		String resName  = '/' + c.getName().replace('.', '/') + ".class";
		return new Resource(resName, ResourceLoader.of(c));
	}


	private Resource(String name, ResourceLoader loader)
	{
		name_ 	= name != null ? name : "";
		loader_ = loader != null ? loader : ResourceLoader.own();
	}


	/**
	 * Adds the path of the name of this resource, defined by the given context object:
	 * If the context is a Class then then the path is the package of the class,
	 * with dots converted to slashes; else the path of the class of the context object is added.
	 * If this resource has a non empty name a '/' is automatically insert between
	 * the current name and the added path.
	 * @param context a context object
	 * @return a new Resource with the appended path
	 */
	@CheckReturnValue
	public Resource pathTo(Object context)
	{
		Check.notNull(context, "context");
		Class<?> c = context instanceof Class ? (Class<?>)context : context.getClass();
		return path(Utils.packageName(c).replace('.', '/'));
	}


	/**
	 * Adds the given part to the resource name. If there is a preceding part,
	 * the parts will be separated by a slash character.
	 * @param part the part to add, not null
	 * @return a new Resource with the appended path
	 */
	@CheckReturnValue
	public Resource path(String part)
	{
		String newName;
		Check.notNull(part, "part");
		if (name_.isEmpty())
			newName = part;
		else
		{
			boolean slashEnd 	= name_.endsWith("/");
			boolean slashStart  = part.startsWith("/");
			if (!slashEnd && !slashStart)
				newName = name_ + '/' + part;
			else if (slashEnd && slashStart)
				newName = name_ + Utils.cutStart(part, "/");
			else
				newName = name_ + part;
		}
		return new Resource(newName, loader_);
	}


	/**
	 * Adds the given parts to the resource name.
	 * Each part is added with a slash prepended to the part.
	 * @param parts the parts added to the resource name
	 * @return this
	 */
	@CheckReturnValue
	public Resource path(String... parts)
	{
		Resource res = this;
		for (String part : parts)
			res = res.path(part);
		return res;
	}


	/**
	 * Loads the resource by the given Class.
	 * @param c a class, not null
	 * @return the resource
	 * @see ResourceLoader#of(Class)
	 */
	@CheckReturnValue
	public Resource loadBy(Class<?> c)
	{
		return loadBy(ResourceLoader.of(c));
	}


	/**
	 * Loads the resource by the Class defined by the given context.
	 * If the context object is a class, that class is used. Else the class of
	 * the context object is used.
	 * @param object an object, not null
	 * @return the resource
	 * @see #loadBy(Class)
	 */
	@CheckReturnValue
	public Resource loadByClassOf(Object object)
	{
		Check.notNull(object, "object");
		return loadBy(object.getClass());
	}


	/**
	 * Loads the resource by the given ClassLoader.
	 * @param cl a ClassLoader, not null
	 * @return the resource
	 * @see ResourceLoader#of(ClassLoader)
	 */
	@CheckReturnValue
	public Resource loadBy(ClassLoader cl)
	{
		return loadBy(ResourceLoader.of(cl));
	}


	/**
	 * Sets that the built resource is loaded by the ClassLoader of the given context object.
	 * @param context a context, not null, used to obtain a ClassLoader. If the context is
	 * 		a class the ClassLoader of that class is used, else the ClassLoader of the class
	 * 		of the context object is used
	 * @return the resource
	 */
	@CheckReturnValue
	public Resource loadByCLOf(Object context)
	{
		Check.notNull(context, "context");
		Class<?> c = context instanceof Class ? (Class<?>)context : context.getClass();
		return loadBy(c.getClassLoader());
	}


	/**
	 * Sets that the built resource is loaded by the given loader.
	 * @param loader a loader
	 * @return the resource
	 */
	@CheckReturnValue
	public Resource loadBy(ResourceLoader loader)
	{
		Check.notNull(loader, "loader");
		return loader.equals(loader_) ? this : new Resource(name_, loader);
	}


	/**
	 * @return the resource name.
	 */
	public String getName()
	{
		return name_;
	}


	/**
	 * @return if this Resource exists.
	 */
	public boolean exists()
	{
		return getURL() != null;
	}


	/**
	 * Checks that this Resource exists. If not it throws an IOException.
	 * @return this
	 * @throws IOException thrown if the resource does not exist
	 */
	public Resource checkExists() throws IOException
	{
		return checkExists(IOException::new);
	}


	/**
	 * Checks that this Resource exists. If not it throws an Exception
	 * produced by the given function.
	 * @param error creates an Exception for an error message
	 * @param<E> the exception type
	 * @return this
	 * @throws E thrown if the resource does not exist
	 */
	public <E extends Exception> Resource checkExists(Function<String,E> error) throws E
	{
		if (!exists())
			throw Check.notNull(error, "error").apply("resource '" + getName() + "' not found");
		return this;
	}


	/**
	 * @return the resource URL or null if the resource is not found.
	 */
	public URL getURL()
	{
		if (!urlRetrieved_)
		{
			url_ = loader_.getURL(name_);
			urlRetrieved_ = true;
		}
		return url_;
	}


	/**
	 * @return the loader used by this Resource.
	 */
	public ResourceLoader getLoader()
	{
		return loader_;
	}


	/**
	 * @return an InputStream for this Resource.
	 * @throws IOException if an I/O error occurs or if the resource cannot be found
	 * @see ResourceLoader#getInputStream(String)
	 */
	@Override
	public InputStream getInputStream() throws IOException
	{
		return loader_.getInputStream(name_);
	}


	/**
	 * @return an InputStream to this Resource or null if the resource does not exist
	 * @throws IOException if an I/O error occurs
	 */
	public InputStream getInputStreamOrNull() throws IOException
	{
		return loader_.getInputStreamOrNull(name_);
	}


	@Override
	public int hashCode()
	{
		return Objects.hash(name_, loader_);
	}


	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Resource)
		{
			Resource res = (Resource)other;
			return Objects.equals(res.getName(), getName()) && Objects.equals(res.getLoader(), getLoader());
		}
		else
			return false;
	}


	@Override
	public String toString()
	{
		return "Resource[" + name_ + ']';
	}
}