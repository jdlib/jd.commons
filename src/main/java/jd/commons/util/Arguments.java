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


import static jd.commons.io.fluent.IO.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import jd.commons.check.Check;
import jd.commons.io.fluent.CharSource;


/**
 * Arguments allows easy processing of command line arguments.
 */
public class Arguments
{
	/**
	 * Creates an Arguments object.
	 * @param args the string arguments passed to the main method 
	 */
	public Arguments(String... args)
	{
		if (args != null)
		{
			for (String arg : args)
			{
				if (!Utils.isEmpty(arg))
					args_.add(arg);
			}
		}
    }
    
    
    /**
	 * Creates an Arguments object.
     * @param args a list of arguments
     */
    public Arguments(List<String> args)
    {
		if (args != null)
		{
			for (String arg : args)
			{
				if (!Utils.isEmpty(arg))
					args_.add(arg);
			}
		}
	}
    
    
    /**
     * Loops over all arguments and replaces any argument starting with "@"
     * with the content of the associated file.
     * @return this
     * @throws IOException if an I/O error occurs
     */
    public Arguments resolveIncludes() throws IOException
    {
    	return resolveIncludes("@");
    }
     
     
    /**
     * Loops over all arguments and replaces any argument starting with the given prefix
     * with the content of the associated file.
     * @param prefix a prefix
     * @return this
     * @throws IOException if an I/O error occurs
     */
    public Arguments resolveIncludes(String prefix) throws IOException
    {
    	Check.notNull(prefix, "prefix");
    	return resolveIncludes(prefix, 0, new HashSet<>());
    }
   
    
    private Arguments resolveIncludes(String prefix, int i, Set<File> seen) throws IOException
    {
    	while (i < args_.size())
    	{
    		String arg = args_.get(i);
    		if (!arg.startsWith(prefix))
    			i++;
    		else
    		{
    			File file = new File(arg.substring(prefix.length())).getAbsoluteFile();
    			if (seen.contains(file))
    				throw new IllegalArgumentException("circular inclusion of file " + file);
    			seen.add(file);
    			
    			List<String> includeArgs = readIncludeArgs(Bytes.from(file).asUtf8());
    			args_.remove(i);
   				args_.addAll(i, includeArgs);
    		}
    	}
    	return this;
    }
    
    
    private List<String> readIncludeArgs(CharSource source) throws IOException
    {
    	List<String> result = new ArrayList<>();
    	for (String line : source.read().lines().trim().removeBlank().toList())
    	{
    		if (!line.startsWith("#"))
    		{
    			String[] args = line.split("\\s+");
    			Collections.addAll(result, args);
    		}
    	}
		return result;
    }


	/**
	 * @return if there are more arguments left.
	 */
	public boolean hasMore()
	{
		return index_ < args_.size();
	}
	
	
	/**
	 * @param count the count
	 * @return if there are at least count more arguments.
	 */
	public boolean hasMore(int count)
	{
		Check.value(count, "count").greater(0);
		return index_ + count <= args_.size();
	}
	
	
	/**
	 * @return the index of the current argument.
	 */
	public int index()
	{
		return index_;
	}
        
 
	/**
	 * @return the size of the argument list
	 */
	public int size()
	{
		return args_.size();
	}

	
	/**
	 * @return the current argument or null if there are no more arguments.
	 */
	public String get()
	{
		return hasMore() ? args_.get(index_) : null;
	}


	/**
	 * @return the remaining arguments.
	 * @see #index()
	 */
	public List<String> getRemaining()
	{
		return args_.subList(index_, args_.size());
	}
	
	
	/**
	 * @return all arguments.
	 */
	public List<String> getAll()
	{
		return args_;
	}

	
    /**
	 * @return if the next argument matches the given string. If true, the string is consumed, i.e.
	 * the arguments list advances to the next argument. 
	 * @param s a string 
	 */
	public boolean consume(String s)
	{
		if (hasMore() && get().equals(s))
		{
			index_++;
			return true;
		}
		else
			return false;
	}


    /**
	 * @return if the current argument matches any of the given strings. If yes the argument is consumed
	 * @param any the strings  
	 */
	public boolean consumeAny(String... any)
	{
		for (String s : any)
		{
			if (consume(s))
				return true;
		}
		return false;
	}

	
	/**
	 * @return a GetValue object which allows to access the next argument and convert to other types
	 * @throws IllegalArgumentException if there are no more arguments
	 */
	public GetString next()
	{
		return next(null);
	}


	/**
	 * @return a Next object which allows to access the next argument and convert to other types
	 * @param what describes the argument and is used in error messages. Can be null.
	 * @throws IllegalArgumentException if there are no more arguments
	 */
	public GetString next(String what)
	{
		if (!hasMore())
			throw iae(what, "expected", null);
		return GetString.of(args_.get(index_++), what);
	}

	
	/**
	 * @return if the next argument starts matches the given pattern.
	 * @param pattern a pattern
	 */
	public boolean nextMatches(Pattern pattern)
	{
		Check.notNull(pattern, "pattern");
		return hasMore() && pattern.matcher(get()).matches();
	}

	
	/**
	 * @return if the next argument matches the given test
	 * @param test a test
	 */
	public boolean nextMatches(Predicate<String> test)
	{
		Check.notNull(test, "test");
		return hasMore() && test.test(get());
	}
	
	
	/**
	 * @return if the next argument starts with the given string.
	 * @param s a string
	 */
	public boolean nextStartsWith(String s)
	{
		return hasMore() && get().startsWith(s);
	}

   
	/**
	 * Replaces the current argument with the given replacement string.
	 * @param replacement a string
	 * @return was the current argument replaced?
	 */
	public boolean replace(String replacement)
	{
		Check.notNull(replacement, "replacement");
		if (hasMore())
		{
			args_.set(index_, replacement);
			return true;
		}
		else 
			return false;
	}

	
	/**
	 * @return a IllegalArgumentException created from the parameters.
	 * @param what describes the argument
	 * @param msg an error message
	 * @param cause an error cause
	 */
	protected static IllegalArgumentException iae(String what, String msg, Exception cause)
	{
		StringBuilder s = new StringBuilder();
		if (what != null)
			s.append(what).append(' ');
		s.append("arg ").append(msg);
		return new IllegalArgumentException(s.toString(), cause);
	}
	
	
	private final List<String> args_ = new ArrayList<>();
	private int index_;
}
