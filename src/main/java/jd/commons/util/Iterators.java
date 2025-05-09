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


import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import jd.commons.check.Check;


/**
 * Provides factory methods for {@link Iterator iterators}.
 */
public interface Iterators
{
	/**
	 * @param <T> the element type
	 * @return an empty Iterator. 
	 */
	public static <T> Iterator<T> empty()
	{
		return List.<T>of().iterator(); 
	}

	
	/**
	 * @param array the array
	 * @param <T> the element type
	 * @return an Iterator for an array. 
	 */
	@SafeVarargs
	public static <T> Iterator<T> of(T... array)
	{
		return of(array, 0, array != null ? array.length : 0);
	}
	

	/**
	 * @return an Iterator for an array slice. 
	 * @param array an array
	 * @param start the start index
	 * @param end the end index.
	 * @param <T> the element type
	 */
	public static <T> Iterator<T> of(T[] array, int start, int end)
	{
		return new ArrayIt<>(array, start, end);
	}

	
	/**
	 * @return an Iterator which returns zero or one values.
	 * @param value the value. If it is not null, the iterator wiull returns
	 * 		this values, else it returns no value. 
	 * @param <T> the element type
	 */
	public static <T> Iterator<T> optional(T value)
	{
		return value != null ? of(value) : empty();
	}

	
	/**
	 * @param iterators a list of iterators
	 * @param <T> the element type
	 * @return an Iterator which joins the given iterators.
	 */
	@SafeVarargs
	public static <T> Iterator<T> join(Iterator<T>... iterators)
	{
		return new JoinIt<>(iterators);
	}

	
	/**
	 * @return an immutable iterator. 
	 * @param it an iterator
	 * @param <T> the element type
	 */
	public static <T> Iterator<T> immutable(Iterator<T> it)
	{
		return new ImmutableIt<>(it);
	}

	
	/**
	 * @param iterable a Iterable
	 * @return an immutable iterator for an iterator of the iterable. 
	 * @param <T> the element type
	 */
	public static <T> Iterator<T> immutable(Iterable<T> iterable)
	{
		return immutable(iterable.iterator());
	}
	
	
	/**
	 * @return an enumeration which wraps an iterator. 
	 * @param it an iterator
	 * @param <T> the element type
	 */
	public static <T> Enumeration<T> toEnumeration(Iterator<T> it)
	{
		final Iterator<T> finalIt = it != null ? it : empty();
		return new Enumeration<>()
		{
			@Override
			public boolean hasMoreElements()
			{
				return finalIt.hasNext();
			}

			@Override
			public T nextElement()
			{
				return finalIt.next();
			}
		};
	}
}


class ArrayIt<T,S extends T> implements Iterator<T>
{
	public ArrayIt(S[] array, int start, int end)
	{
		array_ 	= array;
		index_ 	= start;
		end_ 	= end;
	}


	@Override public boolean hasNext()
	{
		return array_ != null && index_ < end_;
	}


	@Override public T next() throws NoSuchElementException
	{
		if (!hasNext())
			throw new NoSuchElementException();
		return array_[index_++];
	}


	private final S[] array_;
	private final int end_;
	private int index_;
}


class ImmutableIt<T> implements Iterator<T>
{
	private final Iterator<T> it_;

	
	public ImmutableIt(Iterator<T> it)
	{
		it_	= Check.notNull(it, "iterator");
	}
	
	
	@Override public boolean hasNext()
	{
		return it_.hasNext();
	}
	

	@Override public T next()
	{
		return it_.next();
	}
}


class JoinIt<T> implements Iterator<T>
{
	public JoinIt(Iterator<T>[] iterators)
	{
		iterators_	= Check.notNull(iterators, "iterators");
		initNext();
	}
	
	
	private boolean initNext()
	{
		if (next_ < iterators_.length)
		{
			current_ = iterators_[next_++];
			return true;
		}
		else
		{
			current_ = null;
			return false;
		}
	}
	
	
	@Override public boolean hasNext()
	{
		while(current_ != null)
		{
			if (current_.hasNext())
				return true;
			initNext();
		}
		return false;
	}
	

	@Override public T next()
	{
		while(current_ != null)
		{
			if (current_.hasNext())
				return current_.next();
			initNext();
		}
		throw new NoSuchElementException();
	}

	
	@Override public void remove()
	{
		if (current_ != null)
			current_.remove();
		else
			throw new NoSuchElementException();
	}
	
	
	private Iterator<T>[] iterators_;
	private Iterator<T> current_;
	private int next_;
}
