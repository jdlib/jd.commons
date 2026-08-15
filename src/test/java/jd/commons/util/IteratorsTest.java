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
package jd.commons.util;


import static deepdive.ExpectStatic.*;
import static deepdive.ExpectThat.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;


public class IteratorsTest
{
	@Test
	public void testOfArray()
	{
		String[] array = { "a", "b", "c" };
		expectThat(toList(Iterators.of(array))).elems(array);
		expectThat(toList(Iterators.of(array, 1, 2))).elems("b");

		expectError(() -> Iterators.of().next()).isA(NoSuchElementException.class);

		expectFalse(Iterators.of((String[])null).hasNext());
	}


	@Test
	public void testEmpty()
	{
		expectThat(Iterators.empty()).not().hasNext();
	}


	@Test
	public void testImmutable()
	{
		List<String> list = new ArrayList<>();
		list.add("a");
		expectError(() -> Iterators.immutable(list).remove()).isA(UnsupportedOperationException.class);

		Iterator<String> it = Iterators.immutable(list);
		expectTrue(it.hasNext());
		expectEqual("a", it.next());
		expectFalse(it.hasNext());
	}


	@Test
	public void testJoin()
	{
		List<String> list1 = new ArrayList<>();
		Collections.addAll(list1, "a", "b");
		List<String> list2 = new ArrayList<>();
		Collections.addAll(list2, "1", "2");

		Iterator<String> it = Iterators.join(list1.iterator(), list2.iterator());
		expectTrue(it.hasNext());
		expectEqual("a", it.next());
		it.remove();
		expectThat(list1).elems("b");
		expectEqual("b", it.next());
		expectEqual("1", it.next());
		expectEqual("2", it.next());
		expectFalse(it.hasNext());
		expectError(() -> it.next()).isA(NoSuchElementException.class);
		expectError(() -> it.remove()).isA(NoSuchElementException.class);
	}


	@Test
	public void testOptional()
	{
		expectThat(Iterators.optional(null)).not().hasNext();
		expectThat(Iterators.optional("a")).hasNext();
	}


	private static <T> List<T> toList(Iterator<T> it)
	{
		List<T> list = new ArrayList<>();
		while (it.hasNext())
			list.add(it.next());
		return list;
	}


	@Test
	public void testToEnumeration()
	{
		List<String> list = List.of("a");

		Enumeration<String> en = Iterators.toEnumeration(list.iterator());
		expectTrue(en.hasMoreElements());
		expectEqual("a", en.nextElement());
		expectFalse(en.hasMoreElements());

		expectFalse(Iterators.toEnumeration(null).hasMoreElements());
	}
}
