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
import static jd.commons.io.fluent.IO.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import deepdive.actual.java.util.ListActual;


public class FileTreeTest
{
	private static FilePath root;
	private static FilePath root_atxt;
	private static FilePath root_sub;
	private static FilePath root_sub_btxt;


	@BeforeAll
	public static void beforeAll(@TempDir File temp) throws Exception
	{
		root = FilePath.of(temp);

		root_atxt = root.resolve("a.txt");
		Chars.fromString("abc").write().asUtf8().to(root_atxt);

		root_sub = root.resolve("sub");
		root_sub.createDirectory();

		root_sub_btxt = root_sub.resolve("b.txt");
		Chars.fromString("1234567890").write().asUtf8().to(root_sub_btxt);
	}


	@Test
	public void testGetters() throws Exception
	{
		FileTree tree = FileTree.of(root);
		expectEqual(tree.getRoot(), FileTree.of(root.toNioPath()).getRoot());
		expectEqual(tree.getRoot(), FileTree.of(root.toFile()).getRoot());
		expectSame(root, tree.getRoot());
		expectTrue(tree.getIncludeRoot());
		expectEqual(Integer.MAX_VALUE, tree.getMaxDepth());
		expectNull(tree.getFilter());
		expectEqual(Set.of(), tree.getOptions());
		expectEqual("Tree[" + root + ']', tree.toString());
	}


	@Test
	public void testSetters() throws Exception
	{
		FileTree tree = FileTree.of(root);

		// filter
		tree.addDirFilter((f,a) -> true);
		tree.addFileFilter((f,a) -> false);
		expectNotNull(tree.getFilter());
		tree.clearFilter();

		// maxDepth
		tree.setMaxDepth(15);
		expectEqual(15, tree.getMaxDepth());

		// include root
		tree.setExcludeRoot();
		expectFalse(tree.getIncludeRoot());

		// options
		expectThat(tree.getOptions()).empty();
		tree.setFollowLinks();
		expectThat(tree.getOptions()).elems(FileVisitOption.FOLLOW_LINKS);
	}


	@Test
	public void testClone() throws Exception
	{
		FileTree tree = FileTree.of(root);

		FileTree clone = tree.clone();
		clone.setExcludeRoot();

		expectTrue(tree.getIncludeRoot());
		expectFalse(clone.getIncludeRoot());
	}


	@Test
	public void testStream() throws Exception
	{
		FileTree tree = FileTree.of(root);
		expectThat(tree.toList())
			.contains().exactly(root, root_atxt, root_sub, root_sub_btxt);
		expectThat(tree.clone().setExcludeRoot().toList())
			.contains().exactly(root_atxt, root_sub, root_sub_btxt);
		expectThat(tree.clone().addDirFilter((f,a) -> !f.getName().equals("sub")).toList())
			.contains().exactly(root, root_atxt, root_sub_btxt);
		// coverage for follow-links filter
		expectThat(tree.clone().setFollowLinks().addFileFilter((p,a) -> p.getName().equals("a.txt")).toList())
			.contains().exactly(root, root_atxt, root_sub);
	}


	@Test
	public void testAccept() throws Exception
	{
		expectAccept(FileTree.of(root))
			.contains().exactly(root, root_atxt, root_sub, root_sub_btxt);

		expectAccept(FileTree.of(root).setExcludeRoot())
			.contains().exactly(root_atxt, root_sub, root_sub_btxt);

		expectAccept(FileTree.of(root).addFileFilter((p,a) -> p.getName().equals("a.txt")))
			.contains().exactly(root, root_atxt, root_sub);

		expectAccept(FileTree.of(root).addDirFilter((p,a) -> !p.getName().equals("sub")))
			.contains().exactly(root, root_atxt, root_sub_btxt);
	}


	@Test
	public void testDelete(@TempDir File temp) throws IOException
	{
		FilePath root 		= FilePath.of(temp);
		FilePath a_txt 		= root.resolve("a.txt").createFile();
		FilePath b_txt 		= root.resolve("b.txt").createFile();
		FilePath sub   		= root.resolve("sub").createDirectory();
		FilePath sub_a_txt  = sub.resolve("a.txt").createFile();
		FilePath sub_b_txt  = sub.resolve("b.txt").createFile();
		expectTrue(a_txt.isRegularFile());
		expectTrue(b_txt.isRegularFile());
		expectTrue(sub_a_txt.isRegularFile());
		expectTrue(sub_b_txt.isRegularFile());

		FileTree tree = FileTree.of(root);
		tree.clone().addFileFilter((p,a) -> p.getName().equals("a.txt")).delete();
		expectFalse(a_txt.exists());
		expectTrue(b_txt.exists());
		expectFalse(sub_a_txt.exists());
		expectTrue(sub.exists());
		expectTrue(sub_b_txt.exists());

		tree.delete();
		expectFalse(root.exists());
	}


	@Test
	public void testCopy(@TempDir File temp) throws IOException
	{
		FilePath target = FilePath.of(temp);
		FileTree.of(root).setExcludeRoot().copy().to(target);
		List<String> copies = FileTree.of(temp).setExcludeRoot()
			.stream()
			.map(p -> target.relativize(p).toString().replace('\\', '/'))
			.collect(Collectors.toList());
		expectThat(copies).elems("a.txt", "sub", "sub/b.txt");

		FilePath sub = target.resolve("sub");
		FileTree.of(sub).copy().toSibling("sub2"); // cover FileTreeTarget.toSibling()
		expectTrue(target.resolve("sub").exists());
	}


	@Test
	public void testSkipRootProxy() throws Exception
	{
		FileTree rootTree = FileTree.of(root);
		FileTree.SkipRootProxy srp = rootTree.new SkipRootProxy(new Visited());
		IOException ioe = new IOException();

		expectError(() -> srp.visitFileFailed(root, ioe)).same(ioe);
		expectSame(FileVisitResult.CONTINUE, srp.visitFileFailed(root_atxt, ioe));
		expectSame(FileVisitResult.TERMINATE, srp.visitFile(root, null));
	}


	private ListActual<FilePath,?,?,?> expectAccept(FileTree tree) throws IOException
	{
		Visited visited = new Visited();
		tree.accept(visited);
		return expectThat(visited.list);
	}


	private static class Visited implements FileVisitor<FilePath>
	{
		public final List<FilePath> list = new ArrayList<>();


		@Override
		public FileVisitResult preVisitDirectory(FilePath dir, BasicFileAttributes attrs) throws IOException
		{
			list.add(dir);
			return FileVisitResult.CONTINUE;
		}


		@Override
		public FileVisitResult visitFile(FilePath file, BasicFileAttributes attrs) throws IOException
		{
			list.add(file);
			return FileVisitResult.CONTINUE;
		}


		@Override
		public FileVisitResult visitFileFailed(FilePath file, IOException exc) throws IOException
		{
			return FileVisitResult.CONTINUE;
		}


		@Override
		public FileVisitResult postVisitDirectory(FilePath dir, IOException exc) throws IOException
		{
			return FileVisitResult.CONTINUE;
		}
	}
}
