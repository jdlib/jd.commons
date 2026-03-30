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
import java.io.Writer;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.handler.ConsumeCharsHandler;
import jd.commons.util.function.XConsumer;


/**
 * CharWritable represents character content which
 * can be written to a CharTarget.
 */
public interface CharWritable
{
	/**
	 * @return a CharWritable object based on the consumer.
	 * @param consumer a consumer which can write the char content to a Writer.
	 */
	public static CharWritable from(XConsumer<Writer,? extends IOException> consumer)
	{
		Check.notNull(consumer, "consumer");
		return () -> new CharWriteTo<>(new ConsumeCharsHandler(consumer));
	}


	/**
	 * @return a CharWrite object which lets you define to what target to write.
	 */
	@CheckReturnValue
	public CharWriteTo<Void,IOException> write();
}
