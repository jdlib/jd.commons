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


import static deepdive.ExpectStatic.*;
import static deepdive.ExpectThat.*;
import static java.nio.charset.StandardCharsets.*;
import static jd.commons.io.fluent.IO.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.FileSystemException;
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
		expectEqual(temp, tempDir.toFile());

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
		not().expectSame(ancestors, ancestorsOrSelf);
		expectSame(ancestorsOrSelf, ancestorsOrSelf.orSelf());

		Ancestors rootToNearest = ancestors.rootToNearest();
		not().expectSame(ancestors, rootToNearest);
		expectSame(rootToNearest, rootToNearest.rootToNearest());

		expectSame(ancestors, ancestors.filter(null));
		Ancestors filtered = ancestors.filter(fp -> true);
		not().expectSame(ancestors, filtered);
		not().expectSame(filtered, filtered.filter(fp -> false));
		not().expectSame(filtered, filtered.filter(null));

		//----------------------------------
		// nearest to root

		// first(), firstOrNull() + filter
		expectEqual(p0, p0.ancestors().orSelf().firstOrNull());
		expectEqual(p1, p0.ancestors().first().orElse(null)); // coverage of first()
		expectNull(p0.getRoot().ancestors().firstOrNull());
		expectEqual(p1, p0.ancestors().orSelf().filter(fp -> fp.equals(p1)).firstOrNull());

		// iterator
		expectEqual(p0, p0.ancestors().orSelf().iterator().next());
		expectEqual(p1, p0.ancestors().iterator().next());
		assertThrows(NoSuchElementException.class, () -> p0.ancestors().filter(filepath -> false)
			.iterator().next());

		// toList
		expectEqual(ancOrSelfList, p0.ancestors().orSelf().toList());
		expectEqual(ancOrSelfList, p0.ancestors().orSelf()
			.filter(fp -> true) // coverage
			.filter(fp -> true) // coverage of filter chaining
			.toList());

		// toNameList
		expectEqual(ancOrSelfNameList, p0.ancestors().orSelf().toNameList());

		//----------------------------------
		// root to nearest

		// firstOrNull()
		expectEqual(proot, p0.ancestors().rootToNearest().firstOrNull());

		// filter
		expectEqual(List.of(p1, p0), p0.ancestors().orSelf().rootToNearest()
			.filter(fp -> fp.equals(p1) || fp.equals(p0))
			.toList());

		// iterator
		expectEqual(proot, p0.ancestors().rootToNearest().iterator().next());

		// toList
		expectEqual(ancOrSelfRevertedList, p0.ancestors().orSelf().rootToNearest().toList());

		// toNameList
		expectEqual(ancOrSelfNamesRevertedList, p0.ancestors().orSelf().rootToNearest().toNameList());

		//----------------------------------
		// size
		expectEqual(ancOrSelfList.size(), p0.ancestors().orSelf().size());
		expectEqual(1, p0.ancestors().filter(fp -> fp.equals(p1)).size());
	}


	@Test
	public void testAttributes() throws Exception
	{
		Attributes attrs = tempDir.attrsNoFollowLinks();
		BasicFileAttributes basic = attrs.basic();
		expectTrue(basic.isDirectory());
		expectEqual(basic.lastModifiedTime(), attrs.get("lastModifiedTime"));
		expectThat(attrs.map("fileKey,size"))
			.size(2)
			.value("fileKey", basic.fileKey())
			.value("size", basic.size());
		expectEqual("owner", attrs.ownerView().name());
		attrs.set("lastModifiedTime", basic.lastAccessTime());

		try
		{
			attrs.posix();
		}
		catch (UnsupportedOperationException e)
		{
			// fails on windows
		}
	}


	@Test
	public void testChildren() throws Exception
	{
		// .glob.list
		List<FilePath> files = tempDir.children().glob("t*.txt").toList();
		expectThat(files).elems(tempFile);

		// .count
		expectEqual(2, tempDir.children().size());

		// .filter.list
		files = tempDir.children()
			.filter(null) // coverage of filter with null predicate
			.filter(fp -> true) // test that nexgt filter is merged
			.filter(fp -> fp.getName().endsWith(".link")).toList();
		expectThat(files).elems(tempLink);

		// .forEach, also tests .apply()
		AtomicLong totalSize = new AtomicLong(0);
		tempDir.children().forEach(fp -> totalSize.addAndGet(fp.size()));
		expectEqual(6L, totalSize.get());

		// .forEach throwing an exception
		IOException ioe = new IOException();
		expectError(() -> tempDir.children().forEach(fp -> { throw ioe; })).same(ioe);

		// children of a regular file
		expectEqual(0, tempFile.children().size());
	}


	@Test
	public void testChildrenDelete(@TempDir File tempDir) throws Exception
	{
		FilePath dir = FilePath.of(tempDir);
		dir.resolve("a.txt").write().asUtf8().string("abc");
		expectEqual(1, dir.children().delete());
	}


	@Test
	public void testCopyMove(@TempDir File tempDir) throws Exception
	{
		FilePath root = FilePath.of(tempDir);
		FilePath file = root.resolve("file.txt");
		file.write().asUtf8().string("abc");

		// copy
		FilePath copy = file.copy().toSibling("copy.txt");
		expectEqual(3, copy.size());
		expectEqual(root, copy.getParent());

		// move
		FilePath move = copy.move().toSibling("move.txt");
		expectEqual(3, move.size());
		expectEqual(root, move.getParent());
		expectFalse(copy.exists());

		// delete
		move.delete();
		expectFalse(move.exists());
		expectFalse(move.deleteIfExists());
		expectEqual(0, move.deleteRecursively());
		assertThrows(NoSuchFileException.class, () -> move.delete());
	}


	@Test
	public void testCreate(@TempDir File tempDir) throws Exception
	{
		FilePath root = FilePath.of(tempDir);
		FilePath dir  = root.resolve("a", "b");
		expectFalse(dir.exists());
		expectFalse(dir.getParent().exists());

		// createDirectory, createDirectories
		expectError(() -> dir.createDirectory()).isA(IOException.class);
		dir.createDirectories();
		expectTrue(dir.exists());
		root.resolve("c").createDirectory(); // for coverage

		// createFile
		FilePath file = dir.resolve("file.txt");
		expectFalse(file.exists());
		file.createFile();
		expectTrue(file.exists());
		expectEqual(0, file.size());
	}


	@Test
	public void testDelete(@TempDir File tempDir) throws Exception
	{
		FilePath root = FilePath.of(tempDir);
		FilePath file1 = root.resolve("a", "1.txt");
		expectFalse(file1.exists());
		expectFalse(file1.deleteIfExists());
		expectError(() -> file1.delete()).isA(NoSuchFileException.class);

		file1.getParent().createDirectories();
		file1.createFile();
		expectTrue(file1.exists());
		FilePath file2 = file1.resolveSibling("2.txt").createFile();
		FilePath file3 = file1.resolveSibling("3.txt").createFile();

		expectTrue(file2.deleteIfExists());
		file3.delete();
		expectFalse(file3.exists());

		expectEqual(3, root.deleteRecursively());
		expectFalse(root.exists());
	}


	@Test
	public void testGetFileName() throws Exception
	{
		expectEqual(tempFile.getName(), tempFile.getFileName().toString());
	}


	@Test
	public void testGetType() throws Exception
	{
		expectSame(FilePath.Type.DIRECTORY, tempDir.getType());
		expectSame(FilePath.Type.REGULAR_FILE, tempFile.getType());
	}


	@Test
	public void testNormalize()
	{
		FilePath f = tempDir.resolve("..", tempDir.getName(), tempFile.getName());
		expectThat(f.toString()).contains("..");
		f = f.normalize();
		expectEqual(tempFile, f);
		expectSame(f, f.normalize());
	}


	@Test
	public void testOf() throws Exception
	{
		expectEqual(tempFile, FilePath.of(tempFile.toUri()));
		FilePath.userDir(); // coverage

		FilePath empty = FilePath.of("");
		expectNull(empty.getRoot());
		expectEqual("", empty.getName());
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
		expectEqual(0, tempFile.compareTo(tempFile));
		expectFalse(tempFile.equals(""));
		expectTrue(tempFile.endsWith(FilePath.of(tempFile.getName())));
		expectTrue(tempFile.endsWith(tempFile.getName()));
		expectTrue(tempFile.exists());
		expectTrue(tempFile.existsNoFollowLinks());
		expectNotNull(tempFile.getFileStore());
		expectNotNull(tempFile.getFileSystem());
		expectEqual("test.txt", tempFile.getName());
		expectEqual(tempDir, tempFile.getParent());
		expectNotNull(tempFile.getRoot());
		expectEqual(tempFile.toNioPath().hashCode(), tempFile.hashCode());
		expectTrue(tempFile.isAccessible(AccessMode.READ, AccessMode.WRITE));
		expectTrue(tempDir.isDirectory());
		expectFalse(tempFile.isDirectory());
		expectEqual(Files.isExecutable(tempFile.toNioPath()), tempFile.isExecutable());
		expectFalse(tempDir.isRegularFile());
		expectTrue(tempFile.isRegularFile());
		expectFalse(tempFile.isHidden());
		expectTrue(tempFile.isReadable());
		expectTrue(tempFile.isSameFile(tempLink));
		expectTrue(tempFile.isWritable());
		expectFalse(tempFile.isSymbolicLink());
		expectFalse(tempFile.notExists());
		expectEqual("text/plain", tempFile.probeContentType());
		expectEqual(3L, tempFile.size());
		expectTrue(tempFile.startsWith(tempDir));
		expectEqual(tempFile, tempFile.toRealPath());
		expectTrue(tempFile.startsWith(tempDir.toString()));
		expectTrue(tempFile.toAbsolutePath().isAbsolute());
		expectEqual(tempFile.toNioPath().toString(), tempFile.toString());
	}


	@Test
	public void testReadWrite(@TempDir File tempDir) throws Exception
	{
		FilePath dir = FilePath.of(tempDir);

		// createFile
		FilePath file = dir.resolve("file.txt");
		expectFalse(file.exists());
		file.createFile();
		expectTrue(file.exists());
		expectEqual(0, file.size());

		// write/read string
		file.write().asUtf8().string("abc");
		expectEqual("abc", file.read().asUtf8().all());

		// write/read lines
		List<String> lines = List.of("a", "b", "c");
		file.write().asUtf8().lines(lines);
		expectEqual(lines, file.read().asUtf8().lines().toList());

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
		expectEqual(rel.toString(), tempFile.getName());
	}


	@Test
	public void testResolve()
	{
		String name = tempFile.getName();
		expectEqual(tempFile, tempDir.resolve(FilePath.of(name)));
	}


	@Test
	public void testResolveSibling()
	{
		String name = tempLink.getName();
		expectEqual(tempLink, tempFile.resolveSibling(name));
		expectEqual(tempLink, tempFile.resolveSibling(FilePath.of(name)));
	}


	@Test
	public void testSymbolicLink() throws Exception
	{
		try (FilePath.Closeable symlink = FilePath.tempDir().resolve("symlink").toCloseable())
		{
			try
			{
				symlink.createSymbolicLink().to(tempFile);
				expectEqual(tempFile, symlink.resolveSymbolicLink());
				expectSame(FilePath.Type.SYMBOLIC_LINK, symlink.getType());
			}
			catch (FileSystemException e)
			{
				// will fail on windows since it requires admin rights
			}
		}
	}


	@Test
	public void testTemp() throws Exception
	{
		FilePath tempRoot = FilePath.tempDir();
		try (FilePath.Closeable tempDir = tempRoot.createTempDir("test").toCloseable())
		{
			expectThat(tempDir.getName()).startsWith("test");
			tempDir.createTempFile("test1", ".tmp"); // will be deleted when tempRoot is closed
			try (FilePath.Closeable tempFile = tempDir.createTempFile("test", ".tmp").toCloseable())
			{
				expectThat(tempFile.getName()).startsWith("test").endsWith(".tmp");
			}
		}
	}


	@Test
	public void testTreeList() throws Exception
	{
		List<FilePath> list = FileTree.of(tempDir).setMaxDepth(15).toList();
		expectThat(list).contains().exactly(tempDir, tempFile, tempLink);
	}
}
