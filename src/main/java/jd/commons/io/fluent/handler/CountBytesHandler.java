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
import jd.commons.check.Check;
import jd.commons.io.fluent.ByteTarget;
import jd.commons.io.lib.CountingOutputStream;
import jd.commons.util.Holder;


public class CountBytesHandler<E extends Exception>
	extends IOHandler<ByteTarget,OutputStream,Long,E>
{
	protected final IOHandler<ByteTarget,OutputStream,?,E> inner_;
	
	
	public CountBytesHandler(IOHandler<ByteTarget,OutputStream,?,E> inner)
	{
		inner_ = Check.notNull(inner, "inner");
	}
	
	
	@Override
	public Long runSupplier(ByteTarget target) throws E
	{
		Holder<CountingOutputStream> holder = new Holder<>();
		inner_.runSupplier(() -> holder.set(new CountingOutputStream(target.getOutputStream())));
		return holder.has() ? holder.get().count() : Long.valueOf(0L);
	}
	

	@Override
	public Long runDirect(OutputStream out) throws E
	{
		CountingOutputStream cout = new CountingOutputStream(out);
		inner_.runDirect(cout);
		return cout.count();
	}
}
