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


import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import jd.commons.check.Check;
import jd.commons.io.fluent.ByteTarget;
import jd.commons.io.fluent.CharTarget;


public class EncodeHandler<R,E extends Exception> extends IOHandler<ByteTarget,OutputStream,R,E>
{
	private final IOHandler<CharTarget,Writer,R,E> charHandler_;
	private final Charset charset_;
	
	
	public EncodeHandler(IOHandler<CharTarget,Writer,R,E> charHandler, Charset charset)
	{
		charHandler_ = Check.notNull(charHandler, "charHandler");
		charset_ = Check.notNull(charset, "charset");
	}


	@Override
	public R runSupplier(ByteTarget target) throws E
	{
		return charHandler_.runSupplier(() -> new OutputStreamWriter(target.getOutputStream(), charset_));
	}
	
	
	@Override
	public R runDirect(OutputStream out) throws E
	{
		return charHandler_.runDirect(new OutputStreamWriter(out, charset_));
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
