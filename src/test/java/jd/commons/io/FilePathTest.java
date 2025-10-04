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
package jd.commons.io;


import static java.nio.charset.StandardCharsets.*;
import static jd.commons.io.fluent.IO.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import jd.commons.io.FilePath.Attributes;


public class FilePathTest
{
	private static FilePath tempDir;
	private static FilePath tempFile;
	private static FilePath tempLink;
	
	
	@BeforeAll
	public static void beforeAll(@TempDir File temp) throws Exception
	{
		tempDir = FilePath.of(temp);
		assertEquals(temp, tempDir.toFile());

		tempFile = tempDir.resolve("test.txt");
		Chars.fromString("abc").write().asUtf8().to(tempFile);
		
		tempLink = tempDir.resolve("test.link");
		tempLink.createLink().to(tempFile);
	}	
	
	
	@Test
	public void testAttributes() throws Exception
	{
		Attributes attrs = tempDir.attrsNoFollowLinks();
		BasicFileAttributes basic = attrs.basic();
		assertTrue(basic.isDirectory());
		assertEquals(basic.lastModifiedTime(), attrs.get("lastModifiedTime"));
		assertThat(attrs.map("fileKey,size"))
			.hasSize(2)
			.containsEntry("fileKey", basic.fileKey())
			.containsEntry("size", basic.size());
		assertEquals("owner", attrs.ownerView().name());
		attrs.set("lastModifiedTime", basic.lastAccessTime());
		
		
		try
		{
			attrs.posix();
		}
		catch (UnsupportedOperationException e)
		{
		}
	}
	
	
	@Test
	public void testChildren() throws Exception
	{
		// .glob.list
		List<FilePath> files = tempDir.children().glob("t*.txt").toList();
		assertThat(files).containsExactly(tempFile);

		// .count
		assertEquals(2, tempDir.children().count());

		// .filter.list
		files = tempDir.children().filter(fp -> fp.getName().endsWith(".link")).toList();
		assertThat(files).containsExactly(tempLink);
		
		// .forEach, also tests .apply()
		AtomicLong totalSize = new AtomicLong(0);
		tempDir.children().forEach(fp -> totalSize.addAndGet(fp.size()));
		assertEquals(6L, totalSize.get());
		
		// .forEach throwing an exception
		IOException ioe = new IOException(); 
		assertThatThrownBy(() -> tempDir.children().forEach(fp -> { throw ioe; })).isSameAs(ioe);
	}
	
	
	@Test
	public void testChildrenDelete(@TempDir File tempDir) throws Exception
	{
		FilePath dir = FilePath.of(tempDir);
		dir.resolve("a.txt").write().asUtf8().string("abc");
		assertEquals(1, dir.children().delete());
	}
	
	
	@Test
	public void testCopyMove(@TempDir File tempDir) throws Exception
	{
		FilePath root = FilePath.of(tempDir);
		FilePath file = root.resolve("file.txt");
		file.write().asUtf8().string("abc");
		
		// copy
		FilePath copy = file.copy().toSibling("copy.txt");
		assertEquals(3, copy.size());
		assertEquals(root, copy.getParent());

		// move
		FilePath move = copy.move().toSibling("move.txt");
		assertEquals(3, move.size());
		assertEquals(root, move.getParent());
		assertFalse(copy.exists());
		
		// delete
		move.delete();
		assertFalse(move.exists());
		assertFalse(move.deleteIfExists());
		assertEquals(0, move.deleteRecursively());
		assertThrows(NoSuchFileException.class, () -> move.delete());
	}
	
	
	@Test
	public void testCreate(@TempDir File tempDir) throws Exception
	{
		FilePath root = FilePath.of(tempDir);
		FilePath dir  = root.resolve("a", "b");
		assertFalse(dir.exists());
		assertFalse(dir.getParent().exists());

		// createDirectory, createDirectories
		assertThatThrownBy(() -> dir.createDirectory()).isInstanceOf(IOException.class);
		dir.createDirectories();
		assertTrue(dir.exists());
		root.resolve("c").createDirectory(); // for coverage
		
		// createFile
		FilePath file = dir.resolve("file.txt");
		assertFalse(file.exists());
		file.createFile();
		assertTrue(file.exists());
		assertEquals(0, file.size());
	}
	
	
	@Test
	public void testGetType() throws Exception
	{
		assertSame(FilePath.Type.DIRECTORY, tempDir.getType());
		assertSame(FilePath.Type.REGULAR_FILE, tempFile.getType());
	}
	
	
	@Test 
	public void testNormalize()
	{
		FilePath f = tempDir.resolve("..", tempDir.getName(), tempFile.getName());
		assertThat(f.toString()).contains("..");
		f = f.normalize();
		assertEquals(tempFile, f);
		assertSame(f, f.normalize());
	}
	
	
	@Test
	public void testOf() throws Exception
	{
		assertEquals(tempFile, FilePath.of(tempFile.toUri()));
		FilePath.userDir(); // coverage
		
		FilePath empty = FilePath.of("");
		assertNull(empty.getRoot());
		assertEquals("", empty.getName());
	}
	
			
	@Test 
	public void testOpen(@TempDir File tempDir) throws Exception
	{
		FilePath file = FilePath.of(tempDir).resolve("a.txt").createFile();
		
		try (InputStream in = file.open().inputStream())
		{
		}
	
		try (OutputStream out = file.open().outputStream())
		{
		}

		try (SeekableByteChannel ch = file.open().channel())
		{
		}
	
		// also covers open.reader
		try (BufferedReader br = file.open().as(UTF_16).bufferedReader())
		{
		}
		
		// also covers open.writer
		try (PrintWriter w = file.open().append().asUtf8().printWriter())
		{
		}
	}
	
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testProps() throws Exception
	{
		assertEquals(0, tempFile.compareTo(tempFile));
		assertFalse(tempFile.equals(""));
		assertTrue(tempFile.endsWith(FilePath.of(tempFile.getName())));
		assertTrue(tempFile.endsWith(tempFile.getName()));
		assertTrue(tempFile.exists());
		assertTrue(tempFile.existsNoFollowLinks());
		assertNotNull(tempFile.getFileStore());
		assertNotNull(tempFile.getFileSystem());
		assertEquals("txt", tempFile.getExtension());
		assertEquals("?", tempDir.getExtensionOr("?"));
		assertEquals("test.txt", tempFile.getName());
		assertEquals(tempDir, tempFile.getParent());
		assertNotNull(tempFile.getRoot());
		assertEquals(tempFile.toNioPath().hashCode(), tempFile.hashCode());
		assertTrue(tempFile.isAccessible(AccessMode.READ, AccessMode.WRITE));
		assertTrue(tempDir.isDirectory());
		assertFalse(tempFile.isDirectory());
		assertEquals(Files.isExecutable(tempFile.toNioPath()), tempFile.isExecutable());
		assertFalse(tempDir.isRegularFile());
		assertTrue(tempFile.isRegularFile());
		assertFalse(tempFile.isHidden());
		assertTrue(tempFile.isReadable());
		assertTrue(tempFile.isSameFile(tempLink));
		assertTrue(tempFile.isWritable());
		assertFalse(tempFile.isSymbolicLink());
		assertFalse(tempFile.notExists());
		assertEquals("text/plain", tempFile.probeContentType());
		assertEquals(3L, tempFile.size());
		assertTrue(tempFile.startsWith(tempDir));
		assertEquals(tempFile, tempFile.toRealPath());
		assertTrue(tempFile.startsWith(tempDir.toString()));
		assertTrue(tempFile.toAbsolutePath().isAbsolute());
		assertEquals(tempFile.toNioPath().toString(), tempFile.toString());
	}
	
	
	@Test
	public void testReadWrite(@TempDir File tempDir) throws Exception
	{
		FilePath dir = FilePath.of(tempDir);
		
		// createFile
		FilePath file = dir.resolve("file.txt");
		assertFalse(file.exists());
		file.createFile();
		assertTrue(file.exists());
		assertEquals(0, file.size());
		
		// write/read string
		file.write().asUtf8().string("abc");
		assertEquals("abc", file.read().asUtf8().all());
		
		// write/read lines
		List<String> lines = List.of("a", "b", "c");
		file.write().asUtf8().lines(lines);
		assertEquals(lines, file.read().asUtf8().lines().toList());

		// write/read bytes
		byte[] bytes = "abc".getBytes();
		file.write().bytes(bytes);
		assertArrayEquals(bytes, file.read().all());
		file.write().append().bytes(bytes);
		assertArrayEquals("abcabc".getBytes(), file.read().all());
	}
	
	
	@Test 
	public void testRelativize()
	{
		FilePath rel = tempDir.relativize(tempFile);
		assertEquals(rel.toString(), tempFile.getName());
	}
	
	
	@Test 
	public void testResolve()
	{
		String name = tempFile.getName();
		assertEquals(tempFile, tempDir.resolve(FilePath.of(name)));
	}
	
	
	@Test 
	public void testResolveSibling()
	{
		String name = tempLink.getName();
		assertEquals(tempLink, tempFile.resolveSibling(name));
		assertEquals(tempLink, tempFile.resolveSibling(FilePath.of(name)));
	}

	
	@Test
	public void testTemp() throws Exception
	{
		FilePath tempRoot = FilePath.tempDir();
		FilePath tempDir = null;
		try {
			tempDir = tempRoot.createTempDir("test");
			assertThat(tempDir.getName()).startsWith("test");
			FilePath tempFile = null;
			try {
				tempFile = tempDir.createTempFile("test", ".tmp");
				assertThat(tempFile.getName()).startsWith("test").endsWith(".tmp");
			}
			finally {
				if (tempFile != null)
					tempFile.deleteIfExists();
			}
		}
		finally {
			if (tempDir != null)
				tempDir.deleteIfExists();
		}
	}

	
	@Test
	public void testTreeList() throws Exception
	{
		List<FilePath> list = FileTree.of(tempDir).setMaxDepth(15).toList();
		assertThat(list).containsExactlyInAnyOrder(tempDir, tempFile, tempLink);
	}
}
