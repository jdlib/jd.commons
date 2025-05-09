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


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.CheckReturnValue;


/**
 * {@code AsCharset} can take a {@link Charset} and return a result based on that charset.
 * @param<T> the result type
 */
public interface AsCharset<T>
{
	/**
	 * @return calls {@link #as(Charset)} with {@link StandardCharsets#UTF_8}.
	 */
	@CheckReturnValue
	public default T asUtf8()
	{
		return as(StandardCharsets.UTF_8);
	}
	
	
	/**
	 * @return calls {@link #as(Charset)} with {@link StandardCharsets#ISO_8859_1}.
	 */
	@CheckReturnValue
	public default T asLatin1()
	{
		return as(StandardCharsets.ISO_8859_1);
	}
	
	
	/**
	 * @return calls {@link #as(Charset)} with {@link StandardCharsets#US_ASCII}.
	 */
	@CheckReturnValue
	public default T asAscii()
	{
		return as(StandardCharsets.US_ASCII);
	}

	
	/**
	 * @return takes a Charset and returns a result based on that charset.
	 * @param charset a charset, not null
	 */
	@CheckReturnValue
	public T as(Charset charset);
}
