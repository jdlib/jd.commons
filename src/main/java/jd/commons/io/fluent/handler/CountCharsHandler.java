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


import java.io.Writer;
import jd.commons.check.Check;
import jd.commons.io.fluent.CharTarget;
import jd.commons.io.lib.CountingWriter;
import jd.commons.util.Holder;


public class CountCharsHandler<E extends Exception>
	extends IOHandler<CharTarget,Writer,Long,E>
{
	protected final IOHandler<CharTarget,Writer,?,E> inner_;
	
	
	public CountCharsHandler(IOHandler<CharTarget,Writer,?,E> inner)
	{
		inner_ = Check.notNull(inner, "inner");
	}
	
	
	@Override
	public Long runSupplier(CharTarget target) throws E
	{
		Holder<CountingWriter> holder = new Holder<>();
		inner_.runSupplier(() -> holder.set(new CountingWriter(target.getWriter())));
		return holder.has() ? holder.get().count() : Long.valueOf(0L);
	}
	

	@Override
	public Long runDirect(Writer out) throws E
	{
		CountingWriter cout = new CountingWriter(out);
		inner_.runDirect(cout);
		return Long.valueOf(cout.count());
	}
}
