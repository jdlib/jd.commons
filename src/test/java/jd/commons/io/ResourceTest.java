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


import static org.assertj.core.api.Assertions.*;
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
		assertThat(url.toString()).endsWith("java/lang/String.class");

		String data = Resource.of("test.txt").loadByClassOf(this).asUtf8().read().all();
		assertEquals("hello", data);
	}


	@Test
	public void testLoadByClassLoader() throws Exception
	{
		Resource res;
		res = Resource.of().pathTo(this).path("test.txt").loadByCLOf(this);
		res.checkExists();

		res = res.loadByCLOf(getClass());
		assertSame(res, res.loadByCLOf(getClass()));
		res.checkExists();
		try (InputStream in = res.getInputStream())
		{
		}
	}


	@Test
	public void testOfClassFile() throws Exception
	{
		URL url = Resource.ofClassFile(String.class).checkExists().getURL();
		assertThat(url.toString()).endsWith("java/lang/String.class");

		// test with inner classes
		Resource.ofClassFile(InnerClass.class).checkExists();
	}


	@Test
	public void testOfPaths() throws Exception
	{
		assertEquals("java/lang", Resource.of().pathTo(String.class).getName());

		String name = Resource.of().path("/", "java", "lang", "String.class").getName();
		assertEquals("/java/lang/String.class", name);

		assertEquals("a/b", Resource.of().path("a/", "/b").getName());
		assertEquals("a/b", Resource.of("a", "b").getName());
	}


	@Test
	public void testProps() throws IOException
	{
		ResourceLoader nop = ResourceLoader.nop();
		Resource res = Resource.of().path("dummy.txt").loadBy(nop);
		assertSame(nop, res.getLoader());
		assertEquals("dummy.txt", res.getName());
		assertEquals("Resource[dummy.txt]", res.toString());
		assertFalse(res.exists());
		assertNull(res.getInputStreamOrNull());
		assertNull(res.getURL());
		assertEquals(res, res);
		assertNotEquals(res, Resource.of().path("dummy.bin").loadBy(nop));
		assertNotEquals(res, Resource.of().path("dummy.txt").loadByClassOf(this));
		assertEquals(Objects.hash(res.getName(), nop), res.hashCode());
		assertNotEquals(res, nop);
		assertThatThrownBy(() -> res.checkExists(IllegalStateException::new))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("resource 'dummy.txt' not found");
	}

	class InnerClass {
	}
}
