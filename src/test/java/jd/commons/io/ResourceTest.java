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
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import org.junit.jupiter.api.Test;


public class ResourceTest
{
	@Test
	public void testLoadByClass() throws Exception
	{
		URL url = Resource.of("String.class").loadBy(String.class).checkExists().getURL();
		expectThat(url.toString()).endsWith("java/lang/String.class");

		String data = Resource.of("test.txt").loadByClassOf(this).asUtf8().read().all();
		expectEqual("hello", data);
	}


	@Test
	public void testLoadByClassLoader() throws Exception
	{
		Resource res;
		res = Resource.of().pathTo(this).path("test.txt").loadByCLOf(this);
		res.checkExists();

		res = res.loadByCLOf(getClass());
		expectSame(res, res.loadByCLOf(getClass()));
		res.checkExists();
		try (InputStream in = res.getInputStream())
		{
		}
	}


	@Test
	public void testOfClassFile() throws Exception
	{
		URL url = Resource.ofClassFile(String.class).checkExists().getURL();
		expectThat(url.toString()).endsWith("java/lang/String.class");

		// test with inner classes
		Resource.ofClassFile(InnerClass.class).checkExists();
	}


	@Test
	public void testOfPaths() throws Exception
	{
		expectEqual("java/lang", Resource.of().pathTo(String.class).getName());

		String name = Resource.of().path("/", "java", "lang", "String.class").getName();
		expectEqual("/java/lang/String.class", name);

		expectEqual("a/b", Resource.of().path("a/", "/b").getName());
		expectEqual("a/b", Resource.of("a", "b").getName());
	}


	@Test
	public void testProps() throws IOException
	{
		ResourceLoader nop = ResourceLoader.nop();
		Resource res = Resource.of().path("dummy.txt").loadBy(nop);
		expectSame(nop, res.getLoader());
		expectEqual("dummy.txt", res.getName());
		expectEqual("Resource[dummy.txt]", res.toString());
		expectFalse(res.exists());
		expectNull(res.getInputStreamOrNull());
		expectNull(res.getURL());
		expectEqual(res, res);
		assertNotEquals(res, Resource.of().path("dummy.bin").loadBy(nop));
		assertNotEquals(res, Resource.of().path("dummy.txt").loadByClassOf(this));
		expectEqual(Objects.hash(res.getName(), nop), res.hashCode());
		assertNotEquals(res, nop);
		expectError(() -> res.checkExists(IllegalStateException::new))
			.isA(IllegalStateException.class)
			.message("resource 'dummy.txt' not found");
	}

	class InnerClass {
	}
}
