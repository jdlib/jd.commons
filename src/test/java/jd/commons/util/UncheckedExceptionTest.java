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
import java.io.IOException;
import org.junit.jupiter.api.Test;


public class UncheckedExceptionTest
{
	@Test
	public void testCreate()
	{
		IllegalArgumentException e1 = new IllegalArgumentException();
		assertSame(e1, UncheckedException.create(e1));

		IOException e2 = new IOException();
		assertThat(UncheckedException.create(e2)).isInstanceOf(UncheckedException.class).hasCause(e2);
	}


	@Test
	public void testRethrow()
	{
		IllegalArgumentException iae = new IllegalArgumentException();
		IOException ioe = new IOException();
		UncheckedException uioe = (UncheckedException)UncheckedException.create(ioe);  
		
		assertThatThrownBy(() -> UncheckedException.rethrow(iae, IOException.class)).isSameAs(iae);
		assertThatThrownBy(() -> UncheckedException.rethrow(ioe, IOException.class)).isSameAs(ioe);
		assertThatThrownBy(() -> UncheckedException.rethrow(uioe,IOException.class)).isSameAs(ioe);
	}
}
