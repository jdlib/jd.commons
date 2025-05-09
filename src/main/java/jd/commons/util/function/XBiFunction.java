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


import java.util.function.BiFunction;
import java.util.function.Function;
import jd.commons.check.Check;
import jd.commons.util.UncheckedException;


/**
 * Represents a function that accepts two arguments and produces a result.
 * This is the two-arity specialization of {@link Function}.
 * Similar to {@link BiFunction} but can throw checked exceptions.
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of the Exception
 * @see BiFunction
 */
@FunctionalInterface
public interface XBiFunction<T, U, R, E extends Exception> 
{
    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     * @throws E if the operation fails
     */
    R apply(T t, U u) throws E;

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     */
    default <V> XBiFunction<T, U, V, E> andThen(XFunction<? super R, ? extends V, E> after) 
    {
        Check.notNull(after, "after");
        return (T t, U u) -> after.apply(apply(t, u));
    }


	/**
	 * Turns this XBiFunction into a unchecked BiFunction.
	 * Any checked exception thrown is
	 * converted to a {@link UncheckedException}.
	 * @return the unchecked BiConsumer
	 */
    default BiFunction<T,U,R> unchecked()
    {
    	return (T t, U u) -> {
    		try
    		{
    			return apply(t, u);
    		}
    		catch (Exception e)
    		{
    			throw UncheckedException.create(e);
    		}
    	};
    }
}
