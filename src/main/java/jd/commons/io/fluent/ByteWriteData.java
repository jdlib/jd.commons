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


import java.io.OutputStream;
import java.util.function.Consumer;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.util.function.XConsumer;
import jd.commons.util.function.XFunction;


/**
 * ByteWriteData allows to specify what to write to a {@link ByteTarget}.
 * @param<R> the result type of any write operation
 * @param<E> the exception thrown by any write operation
 */
public class ByteWriteData<R,E extends Exception>
{
	protected final ByteTarget target_;
	protected final ErrorFunction<?,R,E> error_;

	
	/**
	 * Creates a new ByteWriteData instance. 
	 * @param target the ByteTarget to write to, not null
	 * @param error handles errors, not null
	 */
	public ByteWriteData(ByteTarget target, ErrorFunction<?,R,E> error)
	{
		target_ = Check.notNull(target, "target");
		error_  = Check.notNull(error, "error");
	}

	
	/**
	 * Writes the bytes of this ByteWriteData to the target. 
	 * @param bytes the bytes, not null
	 * @return t the write result
	 * @throws E thrown if an error occurs
	 */
	public R bytes(byte... bytes) throws E
	{
		Check.notNull(bytes, "bytes");
		return apply(out -> out.write(bytes));
	}
	 

	/**
	 * Writes the bytes of this ByteWriteData to a OutputStream consumer. 
	 * @param consumer a consumer of an OutputStream
	 * @return t the write result
	 * @throws E thrown if an error occurs
	 */
	public R apply(XConsumer<OutputStream,?> consumer) throws E
	{
		Check.notNull(consumer, "consumer");
		try (OutputStream out = target_.getOutputStream())
		{
			consumer.accept(out);
			return null;
		}
		catch (Exception e)
		{
			return error_.handleException(e);
		}
	}

	
	// TODO
	public ByteWriteData<Exception,RuntimeException> silent(Consumer<Exception> log)
	{
		return new ByteWriteData<>(target_, ErrorFunction.silent(log));
	}

	
	/**
	 * @return a new ByteWriteData which catches all exceptions thrown by this ByteWriteData
	 * 		and rethrows it as RuntimeException.
	 * @param<F> an exception type
	 * @param factory a function which takes an exception and returns a new exception 
	 */
	@CheckReturnValue
	public <F extends Exception> ByteWriteData<Void,F> throwing(XFunction<Exception,F,F> factory)
	{
		return new ByteWriteData<>(target_, ErrorFunction.throwing(factory));
	}


	// TODO
	@CheckReturnValue
	public ByteWriteData<R,RuntimeException> unchecked()
	{
		return new ByteWriteData<>(target_, ErrorFunction.throwUnchecked());  
	}
}
