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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import jd.commons.check.Check;
import jd.commons.io.lib.AppendableWriter;
import jd.commons.util.function.XSupplier;


interface IOHelper
{
	public static BufferedReader bufferedReader(Reader reader)
	{
		return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
	}
	
	
	public static Writer toWriter(Appendable appendable)
	{
		Check.notNull(appendable, "appendable");
		return appendable instanceof Writer ? (Writer)appendable : new AppendableWriter(appendable);
	}
	
	
	public static <T> XSupplier<T,IOException> getThrowsIOorRTException(Object def)
	{
		if (def instanceof RuntimeException)
			return () -> { throw (RuntimeException)def; };
		else if (def instanceof IOException)
			return () -> { throw (IOException)def; };
		else if (def instanceof Exception)
			return () -> { throw new IOException((Exception)def); };
		else
			return () -> { throw new IOException(String.valueOf(def)); };
	}
}
