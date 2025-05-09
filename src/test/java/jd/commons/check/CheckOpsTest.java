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
package jd.commons.check;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


// perfect coverage 
public class CheckOpsTest
{
	@Test
	public void testEq()
	{
		assertTrue(CheckOp.EQ.compare(1L, 1L));
		assertFalse(CheckOp.EQ.compare(1L, 2L));
	}


	@Test
	public void testNotEq()
	{
		assertTrue(CheckOp.NOT_EQ.compare(1.0, 2.0));
		assertFalse(CheckOp.NOT_EQ.compare(1.0, 1.0));
	}


	@Test
	public void testGreaterEq()
	{
		assertTrue(CheckOp.GREATER_EQ.compare(1L, 1L));
		assertTrue(CheckOp.GREATER_EQ.compare(2L, 1L));
		assertFalse(CheckOp.GREATER_EQ.compare(1L, 2L));

		assertTrue(CheckOp.GREATER_EQ.compare(1.0, 1.0));
		assertTrue(CheckOp.GREATER_EQ.compare(2.0, 1.0));
		assertFalse(CheckOp.GREATER_EQ.compare(1.0, 2.0));
	}


	@Test
	public void testLess()
	{
		assertTrue(CheckOp.LESS.compare(1.0, 2.0));
		assertFalse(CheckOp.LESS.compare(1.0, 1.0));
	}


	@Test
	public void testLessEq()
	{
		assertTrue(CheckOp.LESS_EQ.compare(1.0, 2.0));
		assertFalse(CheckOp.LESS_EQ.compare(2.0, 1.0));
	}
}
