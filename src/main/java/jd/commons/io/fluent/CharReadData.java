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
package jd.commons.io.fluent;


import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.io.lib.StringWriter2;
import jd.commons.util.function.XFunction;


/**
 * A builder class to specify what to read from a {@link CharSource}.
 * @see CharSource#read()
 * @param<E> the exception thrown by CharReadData methods
 */
public class CharReadData<E extends Exception>
{
	/**
	 * The CharSource. 
	 */
	protected final CharSource source_;

	/**
	 * The ErrorFunction to handle errors. 
	 */
	protected final ErrorFunction<Void,Void,E> error_;
	
	
	/**
	 * Creates a CharReadData which reads from a CharSource.
	 * @param source a CharSource, not null
	 * @param error used to handle thrown exceptions
	 */
	public CharReadData(CharSource source, ErrorFunction<Void,Void,E> error)
	{
		source_ = Check.notNull(source, "source");
		error_	= Check.notNull(error, "error");
	}

	
	/**
	 * @return the character content as String.
	 * @throws E if an error occurs 
	 */ 
	public String all() throws E
	{
		return apply(StringWriter2::readAll);
	}


	/**
	 * Reads the character content and appends to the StringBuilder.
	 * @param sb a StringBuilder, can be null
	 * @return the StringBuilder or a new instance if the given StringBuilder was null
	 * @throws E if an error occurs 
	 */ 
	public StringBuilder all(StringBuilder sb) throws E
	{
		return apply(reader -> StringWriter2.readAll(reader, sb));
	}

	
	/**
	 * Applies the function to a Reader provided by the CharSource of this CharReadData
	 * and returns the result.
	 * @param fn a function
	 * @return the function result
	 * @throws E if an error occurs 
	 */ 
	public <T> T apply(XFunction<Reader,T,?> fn) throws E
	{
		Check.notNull(fn, "fn");
		try (Reader reader = source_.getReader())
		{
			return fn.apply(reader);
		}
		catch (Exception e)
		{
			error_.handleException(e);
			return null; // should not happend
		}
	}


	/**
	 * @return a new CharReadData which catches all exceptions thrown by this CharReadData
	 * 		and rethrows it as RuntimeException.
	 */
	@CheckReturnValue
	public CharReadData<RuntimeException> unchecked()
	{
		return new CharReadData<>(source_, ErrorFunction.throwUnchecked());
	}


	/**
	 * Returns new CharReadData which converts thrown exceptions to a new type.
	 * @param factory a factory which receives a thrown exception. It must either
	 * 		throw an own exception of type F or create an exception of type F
	 * 		(which is then thrown). The second case makes it easy to use 
	 * 		method handles to specify a factory (e.g. {@code IllegalStateException::new}).
	 * @param <F> an exception type
	 * @return the CharReadData
	 */
	@CheckReturnValue
	public <F extends Exception> CharReadData<F> throwing(XFunction<Exception,F,F> factory)
	{	
		return new CharReadData<>(source_, ErrorFunction.throwing(factory));
	}


	/**
	 * @return a Lines builder which lets you define how to read the lines of this CharSource.
	 */
	@CheckReturnValue
	public Lines<E> lines()
	{
		return new Lines<>(this);
	}
	
	
	/**
	 * A builder class which lets you define how to read the lines of this CharSource.
	 * @see CharReadData#lines()
	 */
	public static class Lines<E extends Exception>
	{
		private final CharReadData<E> read_;
		private boolean trim_;
		private boolean removeBlank_;
		

		/**
		 * Creates a new Lines object from the CharRead.
		 * @param read a {@link CharReadData}, not null
		 */
		protected Lines(CharReadData<E> read)
		{
			read_ = Check.notNull(read, "read");
		}
		
		
		/**
		 * Applies a trim to all lines.
		 * @return this
		 */
		public Lines<E> trim()
		{
			trim_ = true;
			return this;
		}

		
		/**
		 * Removes all blank lines, i.e. lines which are all whitespace.
		 * @return this
		 */
		public Lines<E> removeBlank()
		{
			removeBlank_ = true;
			return this;
		}

		
		/**
		 * @return the first line or null if this CharSource is empty
		 * @throws E if an error occurs 
		 */
		public String first() throws E
		{
			return apply(Stream::findFirst).orElse(null);
		}

		
		/**
		 * @return the lines as List
		 * @throws E if an error occurs 
		 */
		public List<String> toList() throws E
		{
			return toList(new ArrayList<>());
		}

		
		/**
		 * Reads the lines and adds them to the provided list.
		 * @param list a List which receives the lines
		 * @return the provided list
		 * @throws E if an error occurs 
		 */
		public List<String> toList(List<String> list) throws E
		{
			Check.notNull(list, "list");
			forEach(list::add);
			return list;
		}
		
		
		/**
		 * @return the lines as array
		 * @throws E if an error occurs 
		 */
		public String[] toArray() throws E
		{
			List<String> list = toList();
			return list.toArray(new String[list.size()]);
		}
		
		
		/**
		 * Invokes the consumer for every line of the CharSource.
		 * @param consumer a consumer
		 * @throws E if an error occurs 
		 */
		public void forEach(Consumer<String> consumer) throws E
		{
			Check.notNull(consumer, "consumer");
			apply(lines -> { lines.forEach(consumer); return null; });
		}

	
		/**
		 * Passes a Stream containing all lines to a function and returns the result.
		 * @param fn a function
		 * @param<T> the function result
		 * @return the function result
		 * @throws E if an error occurs 
		 */
		public <T> T apply(Function<Stream<String>,T> fn) throws E
		{
			Check.notNull(fn, "fn");
			return read_.apply(reader -> {
				BufferedReader br = IOHelper.bufferedReader(reader);
				Stream<String> lines = br.lines();
				if (trim_)
					lines = lines.map(String::trim);
				if (removeBlank_)
					lines = lines.filter(line -> !line.isBlank());
				return fn.apply(lines);
			});
		}
	}
}
