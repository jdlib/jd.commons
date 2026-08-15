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
		expectEqual("archive.tar.gz", fn.toString());
		expectEqual("archive.tar.gz", fn.getName());
		expectEqual("archive", fn.getBaseName());
		expectEqual(List.of("tar", "gz"), fn.getExtensions());
		expectEqual("gz", fn.getExtension());
		expectTrue(fn.hasExtension("gz"));
	}


	@Test
	public void testNoExtension()
	{
		FileName fn = new FileName("README");
		expectEqual("README", fn.getBaseName());
		expectEqual(List.of(), fn.getExtensions());
		expectEqual("", fn.getExtension());
	}


	@Test
	public void testLeadingDot()
	{
		FileName fn = new FileName(".bashrc");
		expectEqual("", fn.getBaseName());
		// extensions: none (no further dots)
		expectEqual(List.of("bashrc"), fn.getExtensions());
		expectEqual("bashrc", fn.getExtension());
	}


	@Test
	public void testTrailingDot()
	{
		FileName fn = new FileName("abc.");
		expectEqual("abc", fn.getBaseName());
		expectEqual(List.of(""), fn.getExtensions());
		expectEqual("", fn.getExtension());
	}


	@Test
	public void testEmptyName()
	{
		FileName fn = new FileName("");
		expectEqual("", fn.getName());
		expectEqual("", fn.getBaseName());
		expectEqual(List.of(), fn.getExtensions());
		expectEqual("", fn.getExtension());
	}


	@Test
	public void testConstruction()
	{
		File file = new File("docs/report.pdf");
		FileName fnOfFile = FileName.of(file);
		expectEqual("report.pdf", fnOfFile.getName());

		FileName fnOfPath = FileName.of(Paths.get("/tmp/data.csv"));
		expectEqual("data.csv", fnOfPath.getName());

		FileName fnOfrootPath = FileName.of(Paths.get("/").getRoot());
		expectEqual("", fnOfrootPath.getName());

		FileName fnOfParts = FileName.of("archive", "tar", "gz");
		expectEqual("archive.tar.gz", fnOfParts.getName());
		expectEqual("archive", fnOfParts.getBaseName());
	}


	@Test
	public void testEqualsAndHashCode()
	{
		FileName a = new FileName("foo.txt");
		FileName b = new FileName("foo.txt");
		FileName c = new FileName("bar.txt");

		expectEqual(a, b);
		expectEqual(a.hashCode(), b.hashCode());
		not().expectEqual(a, c);
		not().expectEqual(a, null);
		not().expectEqual(a, "foo.txt");
	}
}