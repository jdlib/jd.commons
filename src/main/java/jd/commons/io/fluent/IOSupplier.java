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


import java.io.IOException;
import jd.commons.check.Check;
import jd.commons.util.function.XSupplier;


class IOSupplier<T> implements XSupplier<T,IOException>
{
	private final XSupplier<T,?> wrapped_;
	
	
	public IOSupplier(XSupplier<T,?> wrapped)
	{
		wrapped_ = Check.notNull(wrapped, "wrapped");
	}
	

	@Override
	public T get() throws IOException
	{
		try
		{
			return wrapped_.get();
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			// compiler complains if this goes into an own catch block
			if (e instanceof IOException)
				throw (IOException)e; 
			throw new IOException(e);
		}
	}
	
	
	@Override
	public String toString()
	{
		return wrapped_.toString();
	}
}
