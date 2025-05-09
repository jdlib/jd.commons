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


import static jd.commons.io.fluent.IO.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


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
		assertEquals(tree.getRoot(), FileTree.of(root.toNioPath()).getRoot());
		assertEquals(tree.getRoot(), FileTree.of(root.toFile()).getRoot());
		assertSame(root, tree.getRoot());
		assertTrue(tree.getIncludeRoot());
		assertEquals(Integer.MAX_VALUE, tree.getMaxDepth());
		assertNull(tree.getFilter());
		assertEquals(Set.of(), tree.getOptions());
		assertEquals("Tree[" + root + ']', tree.toString());
	}


	@Test
	public void testSetters() throws Exception
	{
		FileTree tree = FileTree.of(root);
		
		// filter
		tree.addDirFilter((f,a) -> true);
		tree.addFileFilter((f,a) -> false);
		assertNotNull(tree.getFilter());
		tree.clearFilter();
		
		// maxDepth
		tree.setMaxDepth(15);
		assertEquals(15, tree.getMaxDepth());

		// include root
		tree.setExcludeRoot();
		assertFalse(tree.getIncludeRoot());
		
		// options
		assertThat(tree.getOptions()).isEmpty();
		tree.setFollowLinks();
		assertThat(tree.getOptions()).containsExactly(FileVisitOption.FOLLOW_LINKS);
	}


	@Test
	public void testClone() throws Exception
	{
		FileTree tree = FileTree.of(root);
		
		FileTree clone = tree.clone();
		clone.setExcludeRoot();
		
		assertTrue(tree.getIncludeRoot());
		assertFalse(clone.getIncludeRoot());
	}


	@Test
	public void testStream() throws Exception
	{
		FileTree tree = FileTree.of(root);
		assertThat(tree.toList())
			.containsExactlyInAnyOrder(root, root_atxt, root_sub, root_sub_btxt);
		assertThat(tree.clone().setExcludeRoot().toList())
			.containsExactlyInAnyOrder(root_atxt, root_sub, root_sub_btxt);
		assertThat(tree.clone().addDirFilter((f,a) -> !f.getName().equals("sub")).toList())
			.containsExactlyInAnyOrder(root, root_atxt, root_sub_btxt);
		// coverage for follow-links filter
		assertThat(tree.clone().setFollowLinks().addFileFilter((p,a) -> p.getName().equals("a.txt")).toList())
			.containsExactlyInAnyOrder(root, root_atxt, root_sub);
	}


	@Test
	public void testAccept() throws Exception
	{
		assertAccept(FileTree.of(root))
			.containsExactlyInAnyOrder(root, root_atxt, root_sub, root_sub_btxt);

		assertAccept(FileTree.of(root).setExcludeRoot())
			.containsExactlyInAnyOrder(root_atxt, root_sub, root_sub_btxt);
		
		assertAccept(FileTree.of(root).addFileFilter((p,a) -> p.getName().equals("a.txt")))
			.containsExactlyInAnyOrder(root, root_atxt, root_sub);
		
		assertAccept(FileTree.of(root).addDirFilter((p,a) -> !p.getName().equals("sub")))
			.containsExactlyInAnyOrder(root, root_atxt, root_sub_btxt);
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
		assertTrue(a_txt.isRegularFile());
		assertTrue(b_txt.isRegularFile());
		assertTrue(sub_a_txt.isRegularFile());
		assertTrue(sub_b_txt.isRegularFile());
		
		FileTree tree = FileTree.of(root);
		tree.clone().addFileFilter((p,a) -> p.getName().equals("a.txt")).delete();		
		assertFalse(a_txt.exists());
		assertTrue(b_txt.exists());
		assertFalse(sub_a_txt.exists());
		assertTrue(sub.exists());
		assertTrue(sub_b_txt.exists());
		
		tree.delete();
		assertFalse(root.exists());
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
		assertThat(copies).containsExactly("a.txt", "sub", "sub/b.txt");
		
		FilePath sub = target.resolve("sub"); 
		FileTree.of(sub).copy().toSibling("sub2"); // cover FileTreeTarget.toSibling()
		assertTrue(target.resolve("sub").exists());
	}
	
	
	@Test 
	public void testSkipRootProxy() throws Exception
	{
		FileTree rootTree = FileTree.of(root);
		FileTree.SkipRootProxy srp = rootTree.new SkipRootProxy(new Visited());
		IOException ioe = new IOException();
		
		assertThatThrownBy(() -> srp.visitFileFailed(root, ioe)).isSameAs(ioe);
		assertSame(FileVisitResult.CONTINUE, srp.visitFileFailed(root_atxt, ioe));
		assertSame(FileVisitResult.TERMINATE, srp.visitFile(root, null));
	}
	
	
	private ListAssert<FilePath> assertAccept(FileTree tree) throws IOException
	{
		Visited visited = new Visited();
		tree.accept(visited);
		return assertThat(visited.list);
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
