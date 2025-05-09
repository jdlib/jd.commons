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


import java.util.function.BiConsumer;
import jd.commons.check.Check;
import jd.commons.util.UncheckedException;


/**
 * Represents an operation that accepts two input arguments and returns no
 * result.
 * This interface is similar to {@link BiConsumer} but can throw checked exceptions.
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 *
 * @see BiConsumer
 */
@FunctionalInterface
public interface XBiConsumer<T, U, E extends Exception> 
{
    /**
     * Performs this operation on the given arguments.
     * @param t the first input argument
     * @param u the second input argument
     * @throws E if the operation fails
     */
    void accept(T t, U u) throws E;

    
    /**
     * Returns a composed {@code BiConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code BiConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     */
    default XBiConsumer<T, U, E> andThen(XBiConsumer<? super T, ? super U, E> after) 
    {
    	Check.notNull(after, "after");
        return (l, r) -> { 
            accept(l, r);
            after.accept(l, r);
        };
    }


	/**
	 * Turns this XBiConsumer into a unchecked BiConsumer.
	 * Any checked exception thrown is
	 * converted to a {@link UncheckedException}.
	 * @return the unchecked BiConsumer
	 */
    default BiConsumer<T,U> unchecked()
    {
    	return (T t, U u) -> {
    		try
    		{
    			accept(t, u);
    		}
    		catch (Exception e)
    		{
    			throw UncheckedException.create(e);
    		}
    	};
    }
}
