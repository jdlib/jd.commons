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


import java.util.function.Supplier;
import jd.commons.util.UncheckedException;


/**
 * Represents a supplier of results.
 * This interface is similar to  {@link Supplier} but can throw checked exceptions.
 * @param <T> the type of results supplied by this supplier
 * @param <E> the type of the Exception
 */
@FunctionalInterface
public interface XSupplier<T,E extends Exception> 
{
	/**
	 * @return a result
	 * @throws E thrown if the operation fails
	 */
	T get() throws E;
    
    
	/**
	 * Turns this XSupplier into a unchecked Supplier.
	 * Any checked exception thrown by the supplier is
	 * converted to a {@link UncheckedException}
	 * @return the unchecked supplier.
	 */
    default Supplier<T> unchecked()
    {
    	return () -> {
    		try
    		{
    			return get();
    		}
    		catch (Exception e)
    		{
    			throw UncheckedException.create(e);
    		}
    	};
    }
}
