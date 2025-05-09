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


import java.util.function.BiPredicate;
import jd.commons.check.Check;
import jd.commons.util.UncheckedException;


/**
 * Represents a predicate (boolean-valued function) of two arguments.
 * This interface is similar to {@link BiPredicate} but can throw checked exceptions.
 * @param <T> the type of the first argument to the BiPredicate
 * @param <U> the type of the seconds argument to the BiPredicate
 * @param <E> the type of the Exception
 */
@FunctionalInterface
public interface XBiPredicate<T, U, E extends Exception>
{
    /**
     * Evaluates this predicate on the given arguments.
     * @param t the first input argument
     * @param u the second input argument
     * @return do the input arguments match the predicate?
	 * @throws E thrown if the operation fails
     */
    boolean test(T t, U u) throws E;

    
    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * AND of this predicate and another. 
     * @param other another predicate
     * @return a composed predicate 
     */
    default XBiPredicate<T,U,E> and(XBiPredicate<? super T,? super U,E> other)
    {
        Check.notNull(other, "other");
        return (T t, U u) -> test(t, u) && other.test(t, u);
    }
    

    /**
     * @return a predicate that represents the logical negation of this
     * predicate.
     */
    default XBiPredicate<T,U,E> negate() 
    {
        return (T t, U u) -> !test(t, u);
    }
    

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * OR of this predicate and another.
     * @param other another predicate
     * @return the composed predicate
     */
    default XBiPredicate<T,U,E> or(XBiPredicate<? super T,? super U,E> other) 
    {
        Check.notNull(other, "other");
        return (T t, U u) -> test(t, u) || other.test(t, u);
    }


	/**
	 * Turn this XBiPredicate into a unchecked BiPredicate.
	 * Any checked exception thrown by the function is
	 * converted to a {@link UncheckedException}.
	 * @return the unchecked BiPredicate
	 */
    default BiPredicate<T,U> unchecked()
    {
    	return (T t, U u) -> {
    		try
    		{
    			return test(t, u);
    		}
    		catch (Exception e)
    		{
    			throw UncheckedException.create(e);
    		}
    	};
    }
}
