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
package jd.commons.util.function;


import java.util.function.Function;
import jd.commons.check.Check;
import jd.commons.util.UncheckedException;


/**
 * Represents a function that accepts one argument and produces a result.
 * This interface is similar to {@link Function} but can throw checked exceptions.
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of the Exception
 */
@FunctionalInterface
public interface XFunction<T, R, E extends Exception> 
{
    /**
     * Applies this function to the given argument.
     * @param t the function argument
     * @return the function result
     * @throws E if an error occurs
     */
    R apply(T t) throws E;


    /**
     * Returns a composed function that first applies this
     * function to its input, and then applies the next function to the result.
     * @param <V> the final result type
     * @param next a next function
     * @return the composed function
     */
    default <V> XFunction<T,V,E> andThen(XFunction<? super R, ? extends V, E> next) 
    {
        Check.notNull(next, "next");
        return (T t) -> next.apply(apply(t));
    }

    
    /**
     * @param <T> the input type
     * @return the identity function
     */
    static <T> XFunction<T, T, RuntimeException> identity() 
    {
        return t -> t;
    }
    
    
    /**
     * Returns a XFunction. Convinience method to obtain a XFunction from a lambda
     * or method handle.
     * @param fn a XFunction.
     * @param <T> the input type
     * @param <R> the result type
     * @param <E> the exception type
     * @return the unchecked function
     */
	public static <T,R,E extends Exception> XFunction<T,R,E> of(XFunction<T,R,E> fn)
    {
    	return Check.notNull(fn, "fn");
    }
    
    
	/**
	 * Turn this function into a unchecked function.
	 * Any checked exception thrown by the function is
	 * converted to a {@link UncheckedException}
	 * @return the unchecked function.
	 */
    default Function<T,R> unchecked()
    {
    	return (T t) -> {
    		try
    		{
    			return apply(t);
    		}
    		catch (Exception e)
    		{
    			throw UncheckedException.create(e);
    		}
    	};
    }
}
