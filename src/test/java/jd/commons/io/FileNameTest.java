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


import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;


public class FileNameTest
{
	@Test
	public void testMultipleExtensions()
	{
		FileName fn = new FileName("archive.tar.gz");
		assertEquals("archive.tar.gz", fn.toString());
		assertEquals("archive.tar.gz", fn.getName());
		assertEquals("archive", fn.getBaseName());
		assertEquals(List.of("tar", "gz"), fn.getExtensions());
		assertEquals("gz", fn.getExtension());
		assertTrue(fn.hasExtension("gz"));
	}


	@Test
	public void testNoExtension()
	{
		FileName fn = new FileName("README");
		assertEquals("README", fn.getBaseName());
		assertEquals(List.of(), fn.getExtensions());
		assertEquals("", fn.getExtension());
	}


	@Test
	public void testLeadingDot()
	{
		FileName fn = new FileName(".bashrc");
		assertEquals("", fn.getBaseName());
		// extensions: none (no further dots)
		assertEquals(List.of("bashrc"), fn.getExtensions());
		assertEquals("bashrc", fn.getExtension());
	}


	@Test
	public void testTrailingDot()
	{
		FileName fn = new FileName("abc.");
		assertEquals("abc", fn.getBaseName());
		assertEquals(List.of(""), fn.getExtensions());
		assertEquals("", fn.getExtension());
	}


	@Test
	public void testEmptyName()
	{
		FileName fn = new FileName("");
		assertEquals("", fn.getName());
		assertEquals("", fn.getBaseName());
		assertEquals(List.of(), fn.getExtensions());
		assertEquals("", fn.getExtension());
	}


	@Test
	public void testConstruction()
	{
		File file = new File("docs/report.pdf");
		FileName fnOfFile = FileName.of(file);
		assertEquals("report.pdf", fnOfFile.getName());

		FileName fnOfPath = FileName.of(Paths.get("/tmp/data.csv"));
		assertEquals("data.csv", fnOfPath.getName());

		FileName fnOfrootPath = FileName.of(Paths.get("/").getRoot());
		assertEquals("", fnOfrootPath.getName());

		FileName fnOfParts = FileName.of("archive", "tar", "gz");
		assertEquals("archive.tar.gz", fnOfParts.getName());
		assertEquals("archive", fnOfParts.getBaseName());
	}


	@Test
	public void testEqualsAndHashCode()
	{
		FileName a = new FileName("foo.txt");
		FileName b = new FileName("foo.txt");
		FileName c = new FileName("bar.txt");

		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, c);
		assertNotEquals(a, null);
		assertNotEquals(a, "foo.txt");
	}
}