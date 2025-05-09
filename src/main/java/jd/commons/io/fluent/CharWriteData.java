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


import java.io.Writer;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.util.function.XConsumer;
import jd.commons.util.function.XFunction;


// TODO javadoc
/**
 * CharWriteData is a builder class to specify what to write to {@link CharTarget}.
 */
public class CharWriteData<R,E extends Exception>
{
	protected final CharTarget target_;
	protected final ErrorFunction<?,R,E> error_;

	
	public CharWriteData(CharTarget target, ErrorFunction<?,R,E> error)
	{
		target_ = Check.notNull(target, "target");
		error_	= Check.notNull(error, "error");
	}

	
	public R string(CharSequence s) throws E
	{
		return apply(writer -> writer.append(s));
	}


	public R lines(CharSequence... lines) throws E
	{
		Check.notNull(lines, "lines");
		return lines(Arrays.asList(lines));
	}


	public R lines(Iterable<? extends CharSequence> lines) throws E
	{
		Check.notNull(lines, "lines");
		return apply(writer -> {
            for (CharSequence line: lines) 
            {
            	writer.append(line);
                writer.write(System.lineSeparator());
            }
		});
	}


	public R apply(XConsumer<Writer,?> consumer) throws E
	{
		Check.notNull(consumer, "consumer");
		try (Writer writer = target_.getWriter())
		{
			consumer.accept(writer);
			return null;
		}
		catch (Exception e)
		{
			return error_.handleException(e);
		}
	}


	public CharWriteData<Exception,RuntimeException> silent(Consumer<Exception> log)
	{
		return new CharWriteData<>(target_, ErrorFunction.silent(log));
	}


	/**
	 * Returns new CharWriteData which converts thrown exceptions to a new type.
	 * @param factory a factory which receives a thrown exception. It must either
	 * 		throw an own exception of type F or create an exception of type F
	 * 		(which is then thrown). The second case makes it easy to use 
	 * 		method handles to specify a factory (e.g. {@code IllegalStateException::new}).
	 * @param <F> an exception type
	 * @return the CharWriteData
	 */
	@CheckReturnValue
	public <F extends Exception> CharWriteData<Void,F> throwing(XFunction<Exception,F,F> factory)
	{
		return new CharWriteData<>(target_, ErrorFunction.throwing(factory));
	}
	
	
	/**
	 * @return a new CharWriteData which catches all exceptions thrown by this CharWriteData
	 * 		and rethrows it as RuntimeException.
	 */
	@CheckReturnValue
	public CharWriteData<Void,RuntimeException> unchecked()
	{
		return new CharWriteData<>(target_, ErrorFunction.throwUnchecked());
	}
}
