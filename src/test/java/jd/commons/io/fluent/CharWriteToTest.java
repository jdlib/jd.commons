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


import static java.io.Writer.*;
import static jd.commons.io.fluent.IO.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import jd.commons.io.lib.OpenWriter;
import jd.commons.util.Holder;
import jd.commons.util.UncheckedException;


public class CharWriteToTest
{
	private static final String AUML = "\u00E4";
	@Test 
	public void testCountChars() throws Exception
	{
		CharSource cs = Chars.fromString(AUML);
		assertEquals(1, cs.write().countChars().asUtf8().toNull());
		assertEquals(1, cs.write().countChars().toNull()); // coverage for Writer
		assertEquals(1, cs.write().countChars().to(Chars.toNull())); // coverage for CharTarget
		assertEquals(2, cs.write().asUtf8().countBytes().toNull());
		assertEquals(2, cs.write().countChars().asUtf8().countBytes().toNull());
	}


	@Test 
	public void testSilent() throws Exception
	{
		assertEquals("abc", Chars.fromString("abc").write().silent().toStr());
		IOException e = new IOException("hello");
		Holder<Exception> log = new Holder<>();  
		assertSame(e, Chars.fromError(e).write().silent(log).to(Chars.toNull()));
		assertSame(e, log.get());
	}

	
	@Test
	public void testThrowing() throws Exception
	{
		IOException e = new IOException("a");
		assertThatThrownBy(()-> Chars.fromError(e).write().throwing(IllegalStateException::new).toNull())
			.isInstanceOf(IllegalStateException.class)
			.cause().isSameAs(e);
	}
	
	
	@Test 
	public void testToAppendable() throws Exception
	{
		StringBuilder sb = new StringBuilder();
		Chars.fromString("a").write().to(sb);
		assertEquals("a", sb.toString());
	}

	
	@Test 
	public void testToError() throws Exception
	{
		IOException e = new IOException("x");
		Exception f = Chars.fromString("a").write().silent().toError(e);
		assertSame(e, f);
	}
	

	@SuppressWarnings("deprecation")
	@Test 
	public void testToStr() throws Exception
	{
		CharWriteTo<Void,IOException> cw = Chars.fromLines("a").write();
		String expected = "a" + System.lineSeparator(); 
		assertEquals(expected, cw.toStr());
		assertNotEquals(expected, cw.toString());
	}

	
	@Test
	public void testUnckecked() throws Exception
	{
		// no exception thrown
		assertNull(Chars.fromString("abc").write().unchecked().to(nullWriter()));

		// exception thrown
		IOException ioe = new IOException("hallo");
		assertThatThrownBy(() -> Chars.fromError(ioe).write().unchecked().to(nullWriter()))
			.isInstanceOf(UncheckedException.class)
			.hasMessage("java.io.IOException: hallo")
			.cause()
			.isSameAs(ioe);
	}

	
	@Test
	public void testWrap() throws Exception
	{
		assertNull(Chars.fromString("a").write().wrap(OpenWriter::new).toNull());
		assertNull(Chars.fromString("a").write().wrap(OpenWriter::new).to(Chars.toNull()));
	}
}
