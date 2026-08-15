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
import org.junit.jupiter.api.Test;


public class HolderTest
{
	@Test
	public void test()
	{
		Holder<String> holder;

		holder = new Holder<>("a");
		expectTrue(holder.has("a"));
		expectFalse(holder.has(null));
		expectEqual("Holder:a", holder.toString());
		expectEqual("a", holder.get());
		expectEqual("a", holder.getOr("b"));
		expectEqual("b", holder.apply("b"));

		holder.clear();
		expectNull(holder.get());
		expectNull(holder.getOr(null));
	}
}
