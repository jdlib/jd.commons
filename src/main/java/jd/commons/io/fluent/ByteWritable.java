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
import javax.annotation.CheckReturnValue;


/**
 * ByteWritable represents binary content which
 * can be written to a binary target.
 */
public interface ByteWritable
{
	/**
	 * @return a ByteWriteTo object to specify a target to which
	 * 		the content of this ByteWritable should be written.
	 * 		This initial ByteWriteTo does not return a result
	 * 		and throws IOExceptions
	 */
	@CheckReturnValue
	public ByteWriteTo<Void,IOException> write();
}
