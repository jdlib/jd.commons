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


import static deepdive.ExpectStatic.*;
import static deepdive.ExpectThat.*;
import static jd.commons.io.fluent.IO.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import jd.commons.io.FilePath;
import jd.commons.io.fluent.PathCharSource.PathCharReadData;
import jd.commons.io.fluent.handler.ErrorFunction;


/**
 * Tests PathCharSource and PathByteSource
 */
public class PathSourceTest
{
	private static FilePath dir;
	private static FilePath fpExists;
	private static FilePath fpNotExists;
	private static final byte[] abcBytes = "abc".getBytes();


	@BeforeAll
	public static void beforeAll(@TempDir File temp) throws Exception
	{
		dir = FilePath.of(temp);
		fpExists = dir.resolve("a.txt");
		fpExists.write().bytes(abcBytes);
		fpNotExists = dir.resolve("b.txt");
	}


	// make sure that PathCharReadData is used
	@Test
	public void testCharReadAs() throws Exception
	{
		// call Files.readString(path_, charset_)
		PathCharReadData<IOException> crd = expectInstance(PathCharReadData.class, fpExists.read().asUtf8());
		expectEqual("abc", crd.all());
		expectError(() -> fpNotExists.read().asUtf8().all()).isA(NoSuchFileException.class);

		// coverage: specifying options leads to fallback to default read
		expectEqual("abc", Bytes.from(fpExists, StandardOpenOption.READ).asUtf8().read().all());

		// coverage
		PathCharSource source = expectInstance(PathCharSource.class, Bytes.from(fpNotExists).asUtf8());
		expectNull(source.new PathCharReadData<>(ErrorFunction.swallow()).all());
	}


	// make sure that PathByteReadData is used
	@Test
	public void testByteReadAs() throws Exception
	{
		// call Files.readString(path_, charset_)
		assertArrayEquals(abcBytes, fpExists.read().all());
		expectError(() -> fpNotExists.read().asUtf8().all()).isA(NoSuchFileException.class);

		// coverage: specifying options leads to fallback to default read
		assertArrayEquals(abcBytes, Bytes.from(fpExists, StandardOpenOption.READ).read().all());

		// coverage
		PathByteSource source = expectInstance(PathByteSource.class, Bytes.from(fpNotExists));
		expectNull(source.new PathByteReadData<>(ErrorFunction.swallow()).all());
	}
}
