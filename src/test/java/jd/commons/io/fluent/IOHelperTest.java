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
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import jd.commons.io.lib.AppendableWriter;
import deepdive.actual.java.lang.ThrowableActual;


public class IOHelperTest
{
	@Test
	public void testToWriter()
	{
		Appendable a1 = Writer.nullWriter();
		expectSame(a1, IOHelper.toWriter(a1));

		Appendable a2 = new StringBuilder();
		expectInstance(AppendableWriter.class, IOHelper.toWriter(a2));
	}


	@Test
	public void testGetThrowsIOorRTException()
	{
		IllegalStateException e1 = new IllegalStateException();
		expectGetThrowsIOorRTException(e1).same(e1);

		SQLException e2 = new SQLException();
		expectGetThrowsIOorRTException(e2).isA(IOException.class).cause().same(e2);
	}


	private ThrowableActual<?,?,?> expectGetThrowsIOorRTException(Object e)
	{
		return expectError(() -> IOHelper.getThrowsIOorRTException(e).get());
	}
}
