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


import static java.io.OutputStream.*;
import static java.nio.charset.StandardCharsets.*;
import static jd.commons.io.fluent.IO.*;
import static jd.commons.mock.Mock.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import jd.commons.io.FilePath;
import jd.commons.io.fluent.handler.CountBytesHandler;
import jd.commons.io.fluent.handler.IOHandler;
import jd.commons.io.lib.OpenInputStream;
import jd.commons.io.lib.OpenOutputStream;
import jd.commons.mock.MockSocket;
import jd.commons.util.Holder;
import jd.commons.util.UncheckedException;


public class BytesTest
{
	@TempDir
	private static File tempDir;
	private static File tempFile;
	private static final String ABC = "abc";
	private static final byte[] ABC_BYTES = ABC.getBytes(UTF_8);
	private static final String AUML = "\u00E4";
	private static final byte[] AUML_BYTES = AUML.getBytes(UTF_8);
	
	
	@BeforeAll
	public static void beforeAll() throws Exception
	{
		tempFile = new File(tempDir, "test.txt");
		Bytes.from(ABC_BYTES).write().to(tempFile);
		assertEquals(3L, tempFile.length());
	}	
	
	
	@Test
	public void testFactoryFromBlob() throws Exception
	{
		ByteArrayInputStream in = new ByteArrayInputStream(ABC_BYTES);
		Blob blob = mock(Blob.class).when("getBinaryStream").thenReturn(in).create();
		try (InputStream blobIn = Bytes.from(blob).getInputStream())
		{
			assertSame(in, blobIn);
		}

		blob = mock(Blob.class).when("getBinaryStream", 2L, 2L).thenReturn(in).create();
		try (InputStream blobIn = Bytes.from(blob, 2L, 2L).getInputStream())
		{
			assertSame(in, blobIn);
		}
	}
	
	
	@Test
	public void testFactoryFromByteChannel() throws Exception
	{
		ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(ABC_BYTES));
		assertArrayEquals(ABC_BYTES, Bytes.from(channel).read().all());
	}
	
	
	@Test
	public void testFactoryFromFile() throws Exception
	{
		assertTempFileBytes(Bytes.fromFile(tempFile.toString()));
	}

	
	@Test
	public void testFactoryFromFilePath() throws Exception
	{
		ByteSource bs = Bytes.from(FilePath.of(tempFile));
		assertTempFileBytes(bs);
		
		// touch PathByteSource.getInputStream()
		try (InputStream in = bs.getInputStream())
		{
		}

		// touch PathByteSource/CharSource/getReader
		try (Reader reader = bs.asAscii().getReader())
		{
		}

		// this executes the optimized implementation of PathByteSoure/CharSource/CharRead
		assertEquals(ABC, bs.asUtf8().read().all()); 
	}
	
	
	@Test
	public void testFactoryFromInputStream() throws Exception
	{
		try (InputStream in = Bytes.from(new ByteArrayInputStream(ABC_BYTES)).getInputStream())
		{
			assertThat(in).isInstanceOf(OpenInputStream.class);
		}
		
		try (InputStream in = Bytes.from(new ByteArrayInputStream(ABC_BYTES), false).getInputStream())
		{
			assertThat(in).isInstanceOf(ByteArrayInputStream.class);
		}
	}

	
	@Test
	public void testFactoryFromPath() throws Exception
	{
		ByteSource src = Bytes.from(tempFile.toPath());
		assertTempFileBytes(src);
		assertEquals(ABC, src.asUtf8().read().all());
	}

	
	@Test
	public void testFactoryFromSocket() throws Exception
	{
		MockSocket socket = new MockSocket(ABC_BYTES);
		assertArrayEquals(ABC_BYTES, Bytes.from(socket).read().all());
	}
	
	
	@Test
	public void testFactoryFromURI() throws Exception
	{
		assertTempFileBytes(Bytes.from(tempFile.toURI()));
	}
	
	
	@Test
	public void testFactoryFromURL() throws Exception
	{
		URL url = tempFile.toURI().toURL(); 
		assertTempFileBytes(Bytes.from(url));
		assertTempFileBytes(Bytes.from(url.openConnection()));
	}
	
	
	@Test
	public void testFactoryToBlob() throws Exception
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Blob blob = mock(Blob.class).when("setBinaryStream", 1L).thenReturn(out).create();
		try (OutputStream blobOut = Bytes.to(blob).getOutputStream())
		{
			assertSame(out, blobOut);
		}

		blob = mock(Blob.class).when("setBinaryStream", 2L).thenReturn(out).create();
		try (OutputStream blobOut = Bytes.to(blob, 2L).getOutputStream())
		{
			assertSame(out, blobOut);
		}
	}
	
	
	@Test
	public void testFactoryToByteChannel() throws Exception
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Bytes.from(ABC_BYTES).write().to(Channels.newChannel(out));
		assertArrayEquals(ABC_BYTES, out.toByteArray());
	}
	
	
	@Test
	public void testFactoryToFile() throws Exception
	{
		// coverage 
		Bytes.to(tempFile);
		Bytes.toFile(tempFile.getAbsolutePath());
		Bytes.to(FilePath.of(tempFile));
	}

	
	@Test
	public void testFactoryToOutputStream() throws Exception
	{
		try (OutputStream out = Bytes.to(new ByteArrayOutputStream()).getOutputStream())
		{
			assertThat(out).isInstanceOf(OpenOutputStream.class);
		}
		
		try (OutputStream out = Bytes.to(new ByteArrayOutputStream(), false).getOutputStream())
		{
			assertThat(out).isInstanceOf(ByteArrayOutputStream.class);
		}
	}
	

	@Test
	public void testReadResultApplyError() throws Exception
	{
		assertThatThrownBy(() -> Bytes.from(ABC_BYTES).read().apply(in -> { throw new SQLException("x"); }))
			.isInstanceOf(IOException.class)
			.cause().isInstanceOf(SQLException.class);
	}
	
	
	@Test
	public void testReadResultNBytes() throws Exception
	{
		assertTempFileBytes(Bytes.from(tempFile));
		assertTempFileBytes(Bytes.from(ABC_BYTES));

		byte[] read = Bytes.from(ABC_BYTES).read().first(1);
		assertThat(read).hasSize(1).contains((byte)'a');
	}
	
	
	@Test
	public void testSourceAs() throws Exception
	{
		String s = Bytes.from(AUML_BYTES).asUtf8().read().all();
		assertEquals(AUML, s);
	}

	
	@Test
	public void testReadUnchecked() throws Exception
	{
		// implicitly also tests throwing()
		
		// no exception thrown
		assertArrayEquals(ABC_BYTES, Bytes.from(ABC_BYTES).read().unchecked().all());
		assertArrayEquals(new byte[] { ABC_BYTES[0] }, Bytes.from(ABC_BYTES).read().unchecked().first(1));

		// exception thrown
		UnsupportedOperationException uoe = new UnsupportedOperationException("hallo"); 
		ByteSource bs = () -> { throw uoe; };
		assertThatThrownBy(() -> bs.read().unchecked().all()).isSameAs(uoe);
		assertThatThrownBy(() -> bs.read().unchecked().first(1)).isSameAs(uoe);
		assertThatThrownBy(() -> bs.read().unchecked().apply(in -> null)).isSameAs(uoe);
	}
	
	
	@Test
	public void testTargetGetPrintStream() throws Exception
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (PrintStream pout = Bytes.to(out).getPrintStream())
		{
			pout.println("hallo");
		}
	}
	
	
	@Test
	public void testWriteCountBytes() throws Exception
	{
		// .to(OutputStream) and .to(ByteTarget) have different implementations in CountingOutputStream
		assertEquals(2, Bytes.from(AUML_BYTES).write().countBytes().to(nullOutputStream()));
		assertEquals(3, Bytes.from(ABC_BYTES).write().countBytes().to(Bytes.to(nullOutputStream())));
		
		// what if the inner ByteWrite never opens the target, i.e. no CountingOutputStream created by to(ByteTarget)
		CountBytesHandler<?> c = new CountBytesHandler<>(new IOHandler<ByteTarget,OutputStream,Object,Exception>()
		{
			@Override
			public Object runSupplier(ByteTarget target) throws Exception { return null; }

			@Override
			public Object runDirect(OutputStream out) throws Exception { return null; }
		});
		assertEquals(0L, c.runSupplier(Bytes.toNull()));
	}
	
	
	@Test
	public void testWriteInIsOpenedBeforeOut() throws Exception
	{
		Exception e = Bytes.fromError("src").write().silent().to(Bytes.toError("target"));
		assertNotNull(e);
		assertEquals("src", e.getMessage());
	}
	
	
	@Test
	public void testWriteSilent() throws Exception
	{
		// .to(OutputStream) and .to(ByteTarget) have different implementations in ProxyByteWrite
		
		// silent().to(OutputStream) without exception
		assertNull(Bytes.from(ABC_BYTES).write().silent().to(nullOutputStream()));
		
		// silent().to(ByteTarget) without exception
		assertNull(Bytes.from(ABC_BYTES).write().silent().to(Bytes.to(nullOutputStream())));

		// silent().to(OutputStream) with exception
		IOException ioe = new IOException("hallo"); 
		ByteSource bs = Bytes.fromError(ioe);
		assertSame(ioe, bs.write().silent().to(nullOutputStream()));
		
		// silent().to(ByteTarget) with exception
		assertSame(ioe, bs.write().silent().to(Bytes.to(nullOutputStream())));
		
		// silent(Consumer) with exception
		Holder<Exception> holder = new Holder<>();
		assertSame(ioe, bs.write().silent(holder).to(nullOutputStream()));
		assertSame(ioe, holder.get());
	}
	
	
	@Test
	public void testWriteToByteArray() throws Exception
	{
		byte[] written = Bytes.from(AUML_BYTES).write().toByteArray();
		assertArrayEquals(AUML_BYTES, written);
	}
	
	
	@Test
	public void testWriteToSocket() throws Exception
	{
		MockSocket socket = new MockSocket();
		Bytes.from(ABC_BYTES).write().to(Bytes.to(socket));
		assertArrayEquals(ABC_BYTES, socket.out.toByteArray());
	}

	
	@Test
	public void testWriteUnckecked() throws Exception
	{
		// implicitly also tests throwing()
		
		// no exception thrown
		assertNull(Bytes.from(ABC_BYTES).write().unchecked().to(nullOutputStream()));

		// exception thrown
		assertThatThrownBy(() -> Bytes.fromFile("doesnotexist").write().unchecked().toByteArray())
			.isInstanceOf(UncheckedException.class)
			.hasMessage("java.io.FileNotFoundException: doesnotexist (The system cannot find the file specified)")
			.cause()
			.isInstanceOf(IOException.class);
	}

	
	@Test
	public void testWriteWrap() throws Exception
	{
		String s = "test input";
		byte[] b = s.getBytes();
		String expectEncoded = Base64.getEncoder().encodeToString(b);
		
		// Bytes.write.wrap
		byte[] encoded = Bytes.from(b).write().wrap(Base64.getEncoder()::wrap).toByteArray();
		assertEquals(expectEncoded, new String(encoded));

		// ByteSource.wrap
		String decoded = Bytes.from(encoded).wrap(Base64.getDecoder()::wrap).asUtf8().read().all();
		assertEquals(s, decoded);
	}

	
	private void assertTempFileBytes(ByteSource source) throws Exception
	{
		byte[] read = source.read().all();
		assertArrayEquals(ABC_BYTES, read);
	}
}
