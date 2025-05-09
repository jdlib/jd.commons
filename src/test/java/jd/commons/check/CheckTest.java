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


import static jd.commons.io.fluent.IO.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;
import jd.commons.io.FilePath;
import jd.commons.io.Resource;


public class CheckTest
{
	private static final String[] NAMES = new String[] { "a", "b" };
	@TempDir
	private static File DIR;
	private static File SOME_FILE;
	private static File SOME_LINK;
	private static File INVALID_FILE;

	
	@BeforeAll
	public static void beforeAll() throws Exception
	{
		SOME_FILE = new File(DIR, "abc.txt");
		SOME_LINK = new File(DIR, "abc.link");
		Chars.fromString("abc").write().asUtf8().to(SOME_FILE);
		INVALID_FILE = new File(DIR, "xyz123");
		FilePath.of(SOME_LINK).createLink().to(SOME_FILE);
	}
	
	@Test
	public void testElemsArray()
	{
		Check.elems(NAMES, "names").noneNull().notContains("c").contains("b").notEmpty();
		Check.elems(Set.of(NAMES), "names").noneNull().notContains("c").contains("b");
		
		failCheck(() -> Check.elems(new String[] { null }, null).noneNull(), "arg contains null");
		failCheck(() -> Check.elems(NAMES, "names").notContains("a"), "names contains \"a\"");
		failCheck(() -> Check.elems(NAMES, "names").contains("x"), "names not contains \"x\"");
		failCheck(() -> Check.elems(List.of(), "names").notEmpty(), "names is empty");
	}

	
	@Test
	public void testElemsCollection()
	{
		Check.elems(List.of(NAMES), "names").noneNull();
		failCheck(() -> Check.elems(Arrays.asList((String)null), "names").noneNull(), "names contains null");
	}

	
	@Test
	public void testEqual()
	{
		Check.equal("a", "a");
		failCheck(() -> Check.equal(1, 2), "1 not equal to 2");
	}
	
	
	@Test
	public void testFile()
	{
		Check.file(DIR).isDir().exists(true);
		Check.file(SOME_FILE).isFile().exists(true).length().equal(SOME_FILE.length());
		Check.file(INVALID_FILE).exists(false);
		failCheck(() -> Check.file(DIR).isFile(), DIR.getAbsolutePath() + " is not a file");
		failCheck(() -> Check.file(DIR).exists(false), DIR.getAbsolutePath() + " exists");
		failCheck(() -> Check.file(INVALID_FILE).exists(true), INVALID_FILE.getAbsolutePath() + " does not exist");
		failCheck(() -> Check.file(SOME_FILE, "f").isDir(), "f " + SOME_FILE.getAbsolutePath() + " is not a directory");
		failCheck(() -> Check.file(SOME_FILE).exists(false), SOME_FILE.getAbsolutePath() + " exists");
	}
	
	
	@Test
	public void testIndex()
	{
		Check.index(0).validFor(NAMES);
		Check.index(1).validFor("abc");
		Check.index(1).validFor(List.of("a", "b"));
		failCheck(() -> Check.index(-1), "index is -1, expected to be >= 0");
		failCheck(() -> Check.index(2, "i").lessEq(1), "i is 2, expected to be <= 1");
		failCheck(() -> Check.index(4, "i").validFor(NAMES), "index i is 4 >= length 2");
		failCheck(() -> Check.index(4, "i").validFor(List.of(NAMES)), "index i is 4 >= size 2");
	}

	
	@Test
	public void testIsA() throws Exception
	{
		Check.isA("a", CharSequence.class);
		Check.isA("a", CharSequence.class, "name");
		failCheck(() -> Check.isA("a", Integer.class), "java.lang.String \"a\" is not a java.lang.Integer");
		failCheck(() -> Check.isA("a", Integer.class, "name"), "name java.lang.String \"a\" is not a java.lang.Integer");
		
		Class<?> c1 = CustomClassLoader.load();
		Class<?> c2 = CustomClassLoader.load();
		Object i1   = c1.getConstructor().newInstance();
		failCheck(() -> Check.isA(i1, c2), c1.getName() + ' ' + i1 + " is not a " + c2.getName() + " and was loaded by " + c1.getClassLoader() + " and not by " + c2.getClassLoader());
	}
	
	
	@Test
	public void testIsFalse()
	{
		Check.isFalse(false, "flag");
		failCheck(() -> Check.isFalse(true, "flag"), "flag is true"); 
	}

		
	@Test
	public void testIsNull()
	{
		Check.isNull(null, "name");
		failCheck(() -> Check.isNull("a", "name"), "name is \"a\" and not null");
	}
	
	
	@Test
	public void testDerivedFrom() throws Exception
	{
		Class<?> arg = String.class;
		Class<CharSequence> result = Check.derivedFrom(arg, CharSequence.class);
		assertSame(arg, result);
		failCheck(() -> Check.derivedFrom(String.class, Number.class), "java.lang.String is not derived from java.lang.Number");
		
		Class<?> c1 = CustomClassLoader.load();
		Class<?> c2 = CustomClassLoader.load();
		failCheck(() -> Check.derivedFrom(c1, c2), c1.getName() + '[' + c1.getClassLoader() + "] is not derived from " + c2.getName() + '[' + c2.getClassLoader() + ']');
	}
	
	
	@Test
	public void testIsTrue()
	{
		Check.isTrue(true, "flag");
		failCheck(() -> Check.isTrue(false, "flag"), "flag is false"); 
	}


	@Test
	public void testLengthArray()
	{
		Check.length(NAMES, "names").greater(0).lessEq(10);
		failCheck(() -> Check.length(NAMES, "names").greaterEq(6), "names.length is 2, expected to be >= 6"); 
	}


	@Test
	public void testLengthCharSequence()
	{
		Check.length("abc", "name")
			.greater(2)
			.greaterEq(3)
			.less(4)
			.lessEq(3)
			.equal(3)
			.notEqual(5);
		failCheck(() -> Check.length("abc", "name").greater(5), "name.length is 3, expected to be > 5"); 
	}

	
	@Test
	public void testLengthValue()
	{
		Check.length(1, null).greater(0);
		
		CheckSize cs = Check.length(5, "len");
		cs.indexValid(4).endValid(5);
		
		assertThatThrownBy(() -> cs.indexValid(5)).hasMessage("index is 5, expected to be >= 0 and < 5");
		assertThatThrownBy(() -> cs.endValid(6)).hasMessage("end is 6, expected to be >= 0 and <= 5");
	}
	
	
	@Test
	public void testLengthFile()
	{
		Check.length(SOME_FILE, null).equal(SOME_FILE.length());
		failCheck(() -> Check.length(SOME_FILE, "someFile").less(0), "someFile.length is " + SOME_FILE.length() + ", expected to be < 0");
	}


	@Test
	public void testNotBlankCharSequence()
	{
		Check.notBlank("abc", "name");
		failCheck(() -> Check.notBlank(null, "name"), "name is null"); 
		failCheck(() -> Check.notBlank("", "name"), "name is empty"); 
		failCheck(() -> Check.notBlank(" ", "name"), "name is blank"); 
	}
	

	@Test
	public void testNotEmptyArray()
	{
		Check.notEmpty(new byte[] { 1 }, null);
		Check.notEmpty(new char[] { '1' }, null);
		Check.notEmpty(new double[] { 1.0 }, null);
		Check.notEmpty(new int[] { 1 }, null);
		Check.notEmpty(new long[] { 1L }, null);
		assertSame(NAMES, Check.notEmpty(NAMES, "names"));
		failCheck(() -> Check.notEmpty((Object[])null, "names"), "names is null"); 
		failCheck(() -> Check.notEmpty(new Object[0], null), "arg is empty"); 
	}
	
	
	@Test
	public void testNotEmptyCharSequence()
	{
		assertSame("a", Check.notEmpty("a", "type"));
		failCheck(() -> Check.notEmpty((CharSequence)null, null), "arg is null"); 
		failCheck(() -> Check.notEmpty("", "type"), "type is empty"); 
	}


	@Test
	public void testNotEmptyCollection()
	{
		List<String> list = List.of("a");
		assertSame(list, Check.notEmpty(list, "types"));
		failCheck(() -> Check.notEmpty((List<?>)null, null), "arg is null"); 
		failCheck(() -> Check.notEmpty(List.of(), "types"), "types is empty"); 
	}


	@Test
	public void testNotEmptyMap()
	{
		Map<String,String> map = Map.of("a", "1");
		assertSame(map, Check.notEmpty(map, "types"));
		failCheck(() -> Check.notEmpty((Map<?,?>)null, null), "arg is null"); 
		failCheck(() -> Check.notEmpty(Map.of(), "map"), "map is empty"); 
	}

	
	@Test
	public void testNotEqual()
	{
		assertSame("a", Check.notEqual("a", "b"));
		failCheck(() -> Check.notEqual("a", "a"), "\"a\" equal"); 
	}

	
	@Test
	public void testNotNull()
	{
		assertSame("a", Check.notNull("a", "name"));
		failCheck(() -> Check.notNull(null, "name"), "name is null"); 
	}

	
	@Test
	public void testNotSame()
	{
		assertSame("a", Check.notSame("a", "b"));
		failCheck(() -> Check.notSame("a", "a"), "\"a\" same as other arg"); 
	}
	
	
	@Test
	public void testPath() throws IOException
	{
		Path curDir = DIR.toPath();
		Path someFile = SOME_FILE.toPath();
		Path invalidFile = INVALID_FILE.toPath();
		Check.path(curDir, LinkOption.NOFOLLOW_LINKS).isDir().exists(true);
		Check.path(someFile).isFile().exists(true);
		failCheck(() -> Check.path(curDir, "arg").exists(false), "arg " + curDir.toAbsolutePath() + " exists");
		failCheck(() -> Check.path(curDir).isFile(), curDir.toAbsolutePath() + " is not a file");
		failCheck(() -> Check.path(curDir).isSymbolicLink(), curDir.toAbsolutePath() + " is not a symbolic link");
		failCheck(() -> Check.path(someFile).isDir(), someFile.toAbsolutePath() + " is not a directory");
		failCheck(() -> Check.path(invalidFile).exists(true), invalidFile.toAbsolutePath() + " does not exist");
		
		Check.path(FilePath.of(curDir)).isDir().size().equal(0);
		Check.size(FilePath.of(curDir), "curDir").equal(0);
		assertThatThrownBy(() -> Check.size(FilePath.of("doesnotexist"), "curDir").equal(0))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("can't access size of doesnotexist")
			.cause().isInstanceOf(IOException.class);
	}

	
	@Test
	public void testSame()
	{
		assertSame("a", Check.same("a", "a"));
		failCheck(() -> Check.same("a", null), "\"a\" not same as null"); 
	}


	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testSize()
	{
		Check.size(Map.of(), null).equals(0);
		failCheck(() -> Check.size(List.of(), null).greater(6), "arg.size is 0, expected to be > 6"); 
	}

	
	@Test
	public void testSizeValue()
	{
		Check.size(1, null).greater(0);
	}

	
	@Test
	public void testValueDecimal()
	{
		Check.value(1.3f, null)
			.greater(1).lessEq(1.3).positive().notNaN().finite();
		Check.value(-1.3, null).greaterEq(-1.3).less(0).notEqual(2).equal(-1.3).negative().notNaN().finite();
		failCheck(() -> Check.value(2.5, "price").greater(3), "price is 2.5, expected to be > 3.0");
		failCheck(() -> Check.value(Float.NaN, "price").notNaN(), "price is NaN");
		failCheck(() -> Check.value(Double.NEGATIVE_INFINITY, "price").finite(), "price is -Infinity");
		failCheck(() -> Check.value(1.0, null).equal(4.0), "arg is 1.0, expected to be == 4.0");
	}

	
	@Test
	public void testValueInt()
	{
		Check.value(1, null).greater(0).lessEq(1000);
		Check.value((short)5, null).greaterEq(0).less(10).notEqual(6).equal(5);
		failCheck(() -> Check.value(4, "count").lessEq(3), "count is 4, expected to be <= 3");
	}

	
	@Test
	public void testValueLong()
	{
		Check.value(1L, null).greater(0).lessEq(1000L).equal(1L).notEqual(2);
		failCheck(() -> Check.value(4L, "count").lessEq(3L), "count is 4, expected to be <= 3");
		failCheck(() -> Check.value(4L, "count").notEqual(4L), "count is 4, expected to be != 4");
	}

	
	private static void failCheck(Executable executable, String msg) 
	{
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
		assertEquals(msg, e.getMessage());
	}


	public static class Custom
	{
	}


	static class CustomClassLoader extends ClassLoader 
	{
		public static Class<?> load() throws ClassNotFoundException
		{
			String name = CheckTest.class.getName() + "$Custom";
			return new CustomClassLoader().loadClass(name, true);
		}
		
		
		@Override protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException 
		{
			if (!name.endsWith("$Custom"))
				return super.loadClass(name, resolve);
			String resource = name.replace('.', '/') + ".class";
			URL url = getParent().getResource(resource);
			byte[] data = Bytes.from(url).read().unchecked().all();
            return defineClass(name, data, 0, data.length);
		}
		
		
	    @Override
	    protected Class<?> findClass(String name) throws ClassNotFoundException 
	    {
	        byte[] classData = loadClassData(name);
	        return defineClass(name, classData, 0, classData.length);
	    }
	    
	
	    private byte[] loadClassData(String name) 
	    {
        	String s = '/' + name.replace('.', '/');
        	return Resource.of().path(s).loadByCLOf(getClass()).read().unchecked().all();
	    }
	}
}
