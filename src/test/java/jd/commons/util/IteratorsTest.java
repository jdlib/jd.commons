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


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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
		assertThat(toList(Iterators.of(array))).containsExactly(array);
		assertThat(toList(Iterators.of(array, 1, 2))).containsExactly("b");
		
		assertThatThrownBy(() -> Iterators.of().next()).isInstanceOf(NoSuchElementException.class);
		
		assertFalse(Iterators.of((String[])null).hasNext());
	}
	
	
	@Test
	public void testEmpty()
	{
		assertThat(Iterators.empty()).isExhausted();
	}
	

	@Test
	public void testImmutable()
	{
		List<String> list = new ArrayList<>();
		list.add("a");
		assertThat(Iterators.immutable(list)).isUnmodifiable();
		
		Iterator<String> it = Iterators.immutable(list);
		assertTrue(it.hasNext());
		assertEquals("a", it.next());
		assertFalse(it.hasNext());
	}
	
	
	@Test
	public void testJoin()
	{
		List<String> list1 = new ArrayList<>();
		Collections.addAll(list1, "a", "b");
		List<String> list2 = new ArrayList<>();
		Collections.addAll(list2, "1", "2");
		
		Iterator<String> it = Iterators.join(list1.iterator(), list2.iterator());
		assertTrue(it.hasNext());
		assertEquals("a", it.next());
		it.remove();
		assertThat(list1).containsExactly("b");
		assertEquals("b", it.next());
		assertEquals("1", it.next());
		assertEquals("2", it.next());
		assertFalse(it.hasNext());
		assertThatThrownBy(() -> it.next()).isInstanceOf(NoSuchElementException.class);
		assertThatThrownBy(() -> it.remove()).isInstanceOf(NoSuchElementException.class);
	}
	
	
	@Test
	public void testOptional()
	{
		assertThat(Iterators.optional(null)).isExhausted();
		assertThat(Iterators.optional("a")).hasNext();
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
		assertTrue(en.hasMoreElements());
		assertEquals("a", en.nextElement());
		assertFalse(en.hasMoreElements());

		assertFalse(Iterators.toEnumeration(null).hasMoreElements());
	}
}
