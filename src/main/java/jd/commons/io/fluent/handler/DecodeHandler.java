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


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import jd.commons.check.Check;
import jd.commons.io.fluent.ByteSource;
import jd.commons.io.fluent.CharSource;


public class DecodeHandler<R,E extends Exception> extends IOHandler<ByteSource,InputStream,R,E>
{
	private final IOHandler<CharSource,Reader,R,E> charHandler_;
	private final Charset charset_;
	
	
	public DecodeHandler(IOHandler<CharSource,Reader,R,E> charHandler, Charset charset)
	{
		charHandler_	= Check.notNull(charHandler, "charHandler");
		charset_ 		= Check.notNull(charset, "charset");
	}


	@Override
	public R runSupplier(ByteSource source) throws E
	{
		return charHandler_.runSupplier(() -> new InputStreamReader(source.getInputStream(), charset_));
	}
	
	
	@Override
	public R runDirect(InputStream in) throws E
	{
		return charHandler_.runDirect(new InputStreamReader(in, charset_));
	}


	/**
	 * Returns the inner IOHandler.
	 */
	@Override
	public IOHandler<?,?,?,?> getInner()
	{
		return charHandler_;
	}
}
