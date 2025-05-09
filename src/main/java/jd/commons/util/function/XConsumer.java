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


import java.util.function.Consumer;
import jd.commons.check.Check;
import jd.commons.util.UncheckedException;


/**
 * Represents an operation that accepts a single input argument and returns no
 * result.
 * This interface is similar to {@link Consumer} but can throw checked exceptions.
 * @param <E> the type of the Exception
 */
@FunctionalInterface
public interface XConsumer<T,E extends Exception> 
{
	/**
     * Performs this operation on the given argument.
     * @param value the input argument
     * @throws E if the operation fails
     */
	public void accept(T value) throws E;


    /**
     * Returns a composed {@code XConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation.
     * @param after another XConsumer
     * @return the composed consumer
     */ 
    default XConsumer<T,E> andThen(XConsumer<? super T, E> after) 
    {
        Check.notNull(after, "after");
        return (T t) -> { accept(t); after.accept(t); };
    }


	/**
	 * Turns this XConsumer into an unchecked Consumer.
	 * Any checked exception thrown by the consumer is
	 * converted to a {@link UncheckedException}
	 * @return the unchecked supplier.
	 */
    default Consumer<T> unchecked()
    {
    	return t -> {
    		try
    		{
    			accept(t);
    		}
    		catch (Exception e)
    		{
    			throw UncheckedException.create(e);
    		}
    	};
    }
    

    /**
     * Turns this XConsumer into a XFunction which always returns null.
     * @param <R> the result type of the new XFunction
     * @return the function
     */
    public default <R> XFunction<T,R,E> toXFunction()
    {
		return t -> { accept(t); return null; };
    }
}
