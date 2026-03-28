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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import jd.commons.io.FilePath.Ancestors;
import jd.commons.io.FilePath.Attributes;
import jd.commons.util.Utils;


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
	public void testAncestors() throws Exception
	{
		final FilePath p0 = tempFile;
		final FilePath p1 = p0.getParent();
		final FilePath proot = p0.getRoot();

		List<FilePath> ancOrSelfList = new ArrayList<>();
		FilePath p = p0;
		while (p != null)
		{
			ancOrSelfList.add(p);
			p = p.getParent();
		}
		List<FilePath> ancOrSelfRevertedList = new ArrayList<>(ancOrSelfList);
		Collections.reverse(ancOrSelfRevertedList);

		List<String> ancOrSelfNameList = ancOrSelfList.stream().map(FilePath::getName).collect(Collectors.toList());
		if (Utils.isBlank(proot.getName()))
			ancOrSelfNameList.remove(ancOrSelfNameList.size() - 1);
		List<String> ancOrSelfNamesRevertedList = new ArrayList<>(ancOrSelfNameList);
		Collections.reverse(ancOrSelfNamesRevertedList);

		//----------------------------------
		// immutability
		Ancestors ancestors = p0.ancestors();

		Ancestors ancestorsOrSelf = ancestors.orSelf();
		assertNotSame(ancestors, ancestorsOrSelf);
		assertSame(ancestorsOrSelf, ancestorsOrSelf.orSelf());

		Ancestors rootToNearest = ancestors.rootToNearest();
		assertNotSame(ancestors, rootToNearest);
		assertSame(rootToNearest, rootToNearest.rootToNearest());

		assertSame(ancestors, ancestors.filter(null));
		Ancestors filtered = ancestors.filter(fp -> true);
		assertNotSame(ancestors, filtered);
		assertNotSame(filtered, filtered.filter(fp -> false));
		assertNotSame(filtered, filtered.filter(null));

		//----------------------------------
		// nearest to root

		// first(), firstOrNull() + filter
		assertEquals(p0, p0.ancestors().orSelf().firstOrNull());
		assertEquals(p1, p0.ancestors().first().orElse(null)); // coverage of first()
		assertNull(p0.getRoot().ancestors().firstOrNull());
		assertEquals(p1, p0.ancestors().orSelf().filter(fp -> fp.equals(p1)).firstOrNull());

		// iterator
		assertEquals(p0, p0.ancestors().orSelf().iterator().next());
		assertEquals(p1, p0.ancestors().iterator().next());
		assertThrows(NoSuchElementException.class, () -> p0.ancestors().filter(filepath -> false)
			.iterator().next());

		// toList
		assertEquals(ancOrSelfList, p0.ancestors().orSelf().toList());
		assertEquals(ancOrSelfList, p0.ancestors().orSelf()
			.filter(fp -> true) // coverage
			.filter(fp -> true) // coverage of filter chaining
			.toList());

		// toNameList
		assertEquals(ancOrSelfNameList, p0.ancestors().orSelf().toNameList());

		//----------------------------------
		// root to nearest

		// firstOrNull()
		assertEquals(proot, p0.ancestors().rootToNearest().firstOrNull());

		// filter
		assertEquals(List.of(p1, p0), p0.ancestors().orSelf().rootToNearest()
			.filter(fp -> fp.equals(p1) || fp.equals(p0))
			.toList());

		// iterator
		assertEquals(proot, p0.ancestors().rootToNearest().iterator().next());

		// toList
		assertEquals(ancOrSelfRevertedList, p0.ancestors().orSelf().rootToNearest().toList());

		// toNameList
		assertEquals(ancOrSelfNamesRevertedList, p0.ancestors().orSelf().rootToNearest().toNameList());

		//----------------------------------
		// size
		assertEquals(ancOrSelfList.size(), p0.ancestors().orSelf().size());
		assertEquals(1, p0.ancestors().filter(fp -> fp.equals(p1)).size());
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
		assertEquals(2, tempDir.children().size());

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

		// children of a regular file
		assertEquals(0, tempFile.children().size());
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
	public void testDelete(@TempDir File tempDir) throws Exception
	{
		FilePath root = FilePath.of(tempDir);
		FilePath file1 = root.resolve("a", "1.txt");
		assertFalse(file1.exists());
		assertFalse(file1.deleteIfExists());
		assertThatThrownBy(() -> file1.delete()).isInstanceOf(NoSuchFileException.class);

		file1.getParent().createDirectories();
		file1.createFile();
		assertTrue(file1.exists());
		FilePath file2 = file1.resolveSibling("2.txt").createFile();
		FilePath file3 = file1.resolveSibling("3.txt").createFile();

		assertTrue(file2.deleteIfExists());
		file3.delete();
		assertFalse(file3.exists());

		assertEquals(3, root.deleteRecursively());
		assertFalse(root.exists());
	}


	@Test
	public void testGetFileName() throws Exception
	{
		assertEquals(tempFile.getName(), tempFile.getFileName().toString());
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
		try (FilePath.Closeable tempDir = tempRoot.createTempDir("test").toCloseable()) {
			assertThat(tempDir.getName()).startsWith("test");
			tempDir.createTempFile("test1", ".tmp"); // will be deleted when tempRoot is closed
			try (FilePath.Closeable tempFile = tempDir.createTempFile("test", ".tmp").toCloseable()) {
				assertThat(tempFile.getName()).startsWith("test").endsWith(".tmp");
			}
		}
	}


	@Test
	public void testTreeList() throws Exception
	{
		List<FilePath> list = FileTree.of(tempDir).setMaxDepth(15).toList();
		assertThat(list).containsExactlyInAnyOrder(tempDir, tempFile, tempLink);
	}
}
