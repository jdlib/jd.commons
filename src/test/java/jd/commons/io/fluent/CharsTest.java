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


import static java.util.stream.Collectors.*;
import static jd.commons.io.fluent.IO.*;
import static jd.commons.mock.Mock.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import jd.commons.io.lib.OpenReader;
import jd.commons.io.lib.OpenWriter;
import jd.commons.io.lib.StringWriter2;


public class CharsTest
{
	@Test
	public void testFactoryFromChars() throws Exception
	{
		assertEquals("abc",Chars.from('a', 'b', 'c').read().all());
	}
	
	
	@Test
	public void testFactoryFromClob() throws Exception
	{
		Reader reader = new StringReader("");
		Clob clob = mock(Clob.class).when("getCharacterStream").thenReturn(reader).create();
		try (Reader blobReader = Chars.from(clob).getReader())
		{
			assertSame(reader, blobReader);
		}

		clob = mock(Clob.class).when("getCharacterStream", 2L, 3L).thenReturn(reader).create();
		try (Reader blobReader = Chars.from(clob, 2L, 3L).getReader())
		{
			assertSame(reader, blobReader);
		}
	}
	
	
	@Test
	public void testFactoryFromLines() throws Exception
	{
		CharWritable cc  = Chars.fromLines(List.of("a", "b"));
		String expected = 'a' + System.lineSeparator() + 'b' + System.lineSeparator(); 
		
		// write.to(OutputStream)
		byte[] actual = cc.write().asUtf8().toByteArray();
		byte[] exp    = expected.getBytes(StandardCharsets.UTF_8);
		assertArrayEquals(exp, actual);

		// write.to(Target)
		StringWriter2 sw = new StringWriter2();
		cc.write().to(Chars.to(sw));
		assertEquals(expected, sw.toString());
	}
	
	
	@Test
	public void testFactoryFromReader() throws Exception
	{
		try (Reader reader = Chars.from(new StringReader("abc")).getReader())
		{
			assertThat(reader).isInstanceOf(OpenReader.class);
		}
		
		try (Reader reader = Chars.from(new StringReader("abc"), false).getReader())
		{
			assertThat(reader).isInstanceOf(StringReader.class);
		}
	}

	
	@Test
	public void testFromString() throws Exception
	{
		assertEquals("abc", Chars.fromString("abc").read().all());
	}
	
	
	@Test
	public void testFactoryToClob() throws Exception
	{
		Writer writer = new StringWriter2();
		Clob clob = mock(Clob.class).when("setCharacterStream", 1L).thenReturn(writer).create();
		try (Writer blobWriter = Chars.to(clob).getWriter())
		{
			assertSame(writer, blobWriter);
		}

		clob = mock(Clob.class).when("setCharacterStream", 2L).thenReturn(writer).create();
		try (Writer blobWriter = Chars.to(clob, 2L).getWriter())
		{
			assertSame(writer, blobWriter);
		}
	}
	
	
	@Test
	public void testFactoryToWriter() throws Exception
	{
		StringWriter s = new StringWriter();
		Chars.fromString("abc").write().to(Chars.to(s));
		assertEquals("abc", s.toString());

		try (Writer writer = Chars.to(new StringWriter()).getWriter())
		{
			assertThat(writer).isInstanceOf(OpenWriter.class);
		}
		
		try (Writer writer = Chars.to(new StringWriter(), false).getWriter())
		{
			assertThat(writer).isInstanceOf(StringWriter.class);
		}
	}
	
	
	@Test
	public void testReadResultApply() throws Exception
	{
		SQLException e = new SQLException(); 
		assertThatThrownBy(() -> Chars.fromString("abc").read().apply(r -> { throw e; }))
			.isInstanceOf(IOException.class)
			.cause().isSameAs(e);
	}
	
	
	@Test
	public void testReadResultLines() throws Exception
	{
		CharSource src = Chars.fromString("a\nb");
		
		assertThat(src.read().lines().toList()).containsExactly("a", "b");
		assertThat(src.read().lines().toArray()).containsExactly("a", "b");
		assertEquals("ab", src.read().lines().apply(st -> st.collect(joining())));
		assertEquals("a", src.read().lines().first());

		src = Chars.fromString("a\n\nb");
		assertThat(src.read().lines().toList()).containsExactly("a", "", "b");
		assertThat(src.read().lines().removeBlank().toList()).containsExactly("a", "b");
	}
	
	
	@Test
	public void testReadResultUnchecked() throws Exception
	{
		// implicitly also tests throwing()
		
		// no exception thrown
		assertEquals("abc", Chars.fromString("abc").read().unchecked().all());
		assertEquals("x", Chars.fromString("abc").read().unchecked().apply(in -> "x"));

		// exception thrown
		UnsupportedOperationException uoe = new UnsupportedOperationException("hallo"); 
		CharSource cs = () -> { throw uoe; };
		assertThatThrownBy(() -> cs.read().unchecked().all()).isSameAs(uoe);
		assertThatThrownBy(() -> cs.read().unchecked().apply(in -> null)).isSameAs(uoe);
	}


	@Test
	public void testSourceBufferedReader() throws Exception
	{
		CharSource src = Chars.from(new StringReader("a"), false);
		BufferedReader br = src.getBufferedReader();
		assertSame(br, Chars.from(br, false).getBufferedReader());
	}

	
	@Test
	public void testSourceWrap() throws Exception
	{
		try (Reader reader = Chars.fromString("a").wrap(BufferedReader::new).getReader())
		{
			assertInstanceOf(BufferedReader.class, reader);
		}
	}


	@Test
	public void testTargetPrintWriter() throws Exception
	{
		StringWriter s = new StringWriter();
		try (PrintWriter w = Chars.to(s).getPrintWriter())
		{
			w.println("hallo");
		}
		assertEquals("hallo" + System.lineSeparator(), s.toString());
	}


	@Test
	public void testTargetWrap() throws Exception
	{
		try (Writer writer = Chars.toNull().wrap(OpenWriter::new).getWriter())
		{
			assertInstanceOf(OpenWriter.class, writer);
		}
	}
	
	
	@Test
	public void testWriteAs()
	{
		// coverage for EncodeHandler.toString
		assertEquals("Encode->TransferChars", Chars.fromString("abc").write().asUtf8().handler_.toString());
	}
}
