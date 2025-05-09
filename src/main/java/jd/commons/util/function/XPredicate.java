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


import java.util.function.Predicate;
import jd.commons.check.Check;
import jd.commons.util.UncheckedException;


/**
 * Represents a predicate (boolean-valued function) of one argument.
 * This interface is similar to {@link Predicate} but can throw checked exceptions.
 * @param <T> the type of the input to the predicate
 * @param <E> the type of the Exception
 */
@FunctionalInterface
public interface XPredicate<T,E extends Exception> 
{
    /**
     * Evaluates this predicate on the given argument.
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
	 * @throws E thrown if the operation fails
     */
    boolean test(T t) throws E;

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * AND of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code false}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ANDed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * AND of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default XPredicate<T,E> and(XPredicate<? super T,E> other) 
    {
    	Check.notNull(other, "other");
        return t -> test(t) && other.test(t);
    }

    /**
     * Returns a predicate that represents the logical negation of this
     * predicate.
     *
     * @return a predicate that represents the logical negation of this
     * predicate
     */
    default XPredicate<T,E> negate() 
    {
        return t -> !test(t);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * OR of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code true}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ORed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * OR of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default XPredicate<T,E> or(XPredicate<? super T,E> other) 
    {
    	Check.notNull(other, "other");
        return t -> test(t) || other.test(t);
    }


	/**
	 * Turns this XPredicate into a unchecked Predicate.
	 * Any checked exception thrown by the XPredicate is
	 * converted to a {@link UncheckedException}
	 * @return the unchecked predicate.
	 */
    default Predicate<T> unchecked()
    {
    	return t -> {
    		try
    		{
    			return test(t); 
    		}
    		catch (Exception e)
    		{
    			throw UncheckedException.create(e);
    		}
    	};
    }
}
