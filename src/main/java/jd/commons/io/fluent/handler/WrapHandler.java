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
package jd.commons.io.fluent.handler;


import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.function.BiFunction;
import java.util.function.Function;
import jd.commons.check.Check;
import jd.commons.io.fluent.ByteSource;
import jd.commons.io.fluent.ByteTarget;
import jd.commons.io.fluent.CharSource;
import jd.commons.io.fluent.CharTarget;
import jd.commons.io.fluent.IO;


public class WrapHandler<AS,AD extends Closeable,R,E extends Exception>
	extends IOHandler<AS,AD,R,E>
{
	public static <R,E extends Exception> WrapHandler<ByteSource,InputStream,R,E>
		forByteSource(IOHandler<ByteSource, InputStream,R,E> inner, Function<InputStream,? extends InputStream> wrapper)
	{
		// explicit since javac has problems
		BiFunction<ByteSource,Function<InputStream,? extends InputStream>,ByteSource> adapter = ByteSource::wrap; 
		return new WrapHandler<>(inner, wrapper, adapter);
	}

	
	public static <R,E extends Exception> WrapHandler<CharSource,Reader,R,E>
		forCharSource(IOHandler<CharSource,Reader,R,E> inner, Function<Reader,? extends Reader> wrapper)
	{
		// explicit since javac has problems
		BiFunction<CharSource,Function<Reader,? extends Reader>,CharSource> adapter = CharSource::wrap; 
		return new WrapHandler<>(inner, wrapper, adapter);
	}


	public static <R,E extends Exception> WrapHandler<ByteTarget,OutputStream,R,E>
		forByteTarget(IOHandler<ByteTarget, OutputStream,R,E> inner, Function<OutputStream,? extends OutputStream> wrapper)
	{
		// explicit since javac has problems
		BiFunction<ByteTarget,Function<OutputStream,? extends OutputStream>,ByteTarget> adapter = ByteTarget::wrap; 
		return new TargetWrapHandler<>(inner, wrapper, adapter, IO.Bytes::to);
	}

	
	public static <R,E extends Exception> WrapHandler<CharTarget,Writer,R,E>
		forCharTarget(IOHandler<CharTarget,Writer,R,E> inner, Function<Writer,? extends Writer> wrapper)
	{
		// explicit since javac has problems
		BiFunction<CharTarget,Function<Writer,? extends Writer>,CharTarget> adapter = CharTarget::wrap; 
		return new TargetWrapHandler<>(inner, wrapper, adapter, IO.Chars::to);
	}

	
	protected final IOHandler<AS,AD,R,E> inner_;
	protected final Function<AD,? extends AD> wrapper_;
	protected final BiFunction<AS,Function<AD,? extends AD>,AS> adapter_;
	
	
	public WrapHandler(IOHandler<AS,AD,R,E> inner, Function<AD,? extends AD> wrapper,
		BiFunction<AS,Function<AD,? extends AD>,AS> adapter)
	{
		inner_   = Check.notNull(inner, "inner");
		wrapper_ = Check.notNull(wrapper, "wrapper");
		adapter_ = Check.notNull(adapter, "adapter");
	}
	
	
	@Override
	public R runSupplier(AS arg) throws E
	{
		return inner_.runSupplier(adapter_.apply(arg, wrapper_));
	}
	

	@Override
	public R runDirect(AD arg) throws E
	{
		// this should never be called
		return inner_.runDirect(wrapper_.apply(arg));
	}


	/**
	 * Returns the inner IOHandler.
	 */
	@Override
	public IOHandler<?,?,?,?> getInner()
	{
		return inner_;
	}
}


class TargetWrapHandler<AS,AD extends Closeable,R,E extends Exception> 
	extends WrapHandler<AS,AD,R,E>
{
	private final Function<AD,AS> factory_;
	
	
	public TargetWrapHandler(IOHandler<AS,AD,R,E> inner,
		Function<AD,? extends AD> wrapper,
		BiFunction<AS,Function<AD,? extends AD>,AS> adapter,
		Function<AD,AS> factory)
	{
		super(inner, wrapper, adapter);
		factory_ = factory;
	}
	

	@Override
	public R runDirect(AD arg) throws E
	{
		// we could just do 
		//     inner_.runDirect(wrapper_.apply(arg));
		// but in case the wrapping OutputStream/Writer caches output
		// which it only flushes completely when closed
		// the flush applied by the terminal handler
		// would not be sufficient, there we change to ByteTarget/CharTarget
		// which are closed, and at the same time
		// the OutputStream/Write will be kept open...
		return runSupplier(factory_.apply(arg));
	}
}
