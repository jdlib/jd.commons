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


import static jd.commons.io.fluent.IO.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.Writer;
import org.junit.jupiter.api.Test;
import jd.commons.io.fluent.ByteWriteTo;
import jd.commons.io.fluent.CharTarget;
import jd.commons.io.fluent.IO;
import jd.commons.io.lib.CountingOutputStream;


public class HandlerTest
{
	@Test
	public void testCountChars() throws Exception
	{	
		// CountingWriter created and used
		CountCharsHandler<?> ch = new CountCharsHandler<>(new TransferCharsHandler<>(Chars.fromString("abc")));
		assertEquals(3L, ch.runSupplier(Chars.toNull()));

		// CountingWriter not created and used, falls back to 0
		ch = new CountCharsHandler<>(new NopHandler<CharTarget,Writer>());
		assertEquals(0L, ch.runSupplier(Chars.toNull()));
	}

	
	@SuppressWarnings("deprecation")
	@Test
	public void testErrorSilent()
	{	
		// coverage for toString and IOHandler.getInner
		assertEquals("Silent->TransferChars", IO.Chars.fromString("a").write().silent().toString());
	}


	@Test
	public void testWrap() throws Exception
	{	
		ByteWriteTo<?,?> wto = Bytes.from("abc".getBytes()).write().wrap(CountingOutputStream::new);
		assertEquals("TargetWrap->TransferBytes", wto.toString());
	}
}