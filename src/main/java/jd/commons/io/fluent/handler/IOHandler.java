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
import jd.commons.io.fluent.ByteSource;
import jd.commons.util.Utils;


/**
 * IOHandler can take a IO related class, e.g. an InputStream,
 * either provided directly or supplied indirectly (e.g. as {@link ByteSource},
 * and return a result.
 * @param <AD> the type of the directly provided argument
 * @param <AS> the type of the argument provided by a supplier
 * @param <R> the result type
 * @param <E> the exception type thrown by the IOHandler operations.
 */
public abstract class IOHandler<AS,AD extends Closeable,R,E extends Exception>
{
	public abstract R runSupplier(AS arg) throws E;


	public abstract R runDirect(AD arg) throws E;


	/**
	 * Returns the inner IOHandler. The default implementation returns null.
	 */
	public IOHandler<?,?,?,?> getInner()
	{
		return null;
	}


	@Override
	public final String toString()
	{
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}


	protected String describe()
	{
		Class<?> c = getClass();
		String name = c.getSimpleName();
		if (Utils.isBlank(name))
			name = Utils.afterLastOr(c.getName(), '.', c.getName());
		return Utils.cutEnd(name, "Handler");
	}


	private void toString(StringBuilder sb)
	{
		sb.append(describe());
		IOHandler<?,?,?,?> inner = getInner();
		if (inner != null)
		{
			sb.append("->");
			inner.toString(sb);
		}
	}
}

