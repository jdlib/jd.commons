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
import java.io.OutputStream;
import jd.commons.util.function.XSupplier;


class GenericByteTarget<I extends OutputStream> extends IOSupplier<I> implements ByteTarget
{
	public GenericByteTarget(XSupplier<I,?> wrapped)
	{
		super(wrapped);
	}
	

	@Override
	public OutputStream getOutputStream() throws IOException
	{
		return get();
	}
}