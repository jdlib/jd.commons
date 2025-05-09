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
package jd.commons.util.function;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import jd.commons.util.UncheckedException;


public class XSupplierTest
{
	@Test
	public void testUnchecked() throws Exception
	{
		XSupplier<String,IOException> p1 = () -> "a";
		XSupplier<String,IOException> p2 = () -> { throw new IOException(); };
		
		assertEquals("a", p1.unchecked().get());
		assertThatThrownBy(() -> p2.unchecked().get())
			.isInstanceOf(UncheckedException.class);
	}
}
