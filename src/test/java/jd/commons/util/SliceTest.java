package jd.commons.util;


import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import jd.commons.util.Slice.Indexes;


public class SliceTest
{
	@Test
	public void testStart()
	{
		Slice start0 = Slice.build().create();
		assertIndexes(start0.indexes(3), 0, 1, 2);

		Slice start1 = Slice.build().start(1).create();
		assertIndexes(start1.indexes(3), 1, 2);

		Slice startNeg = Slice.build().start(-3).create();
		assertIndexes(startNeg.indexes(5), 2, 3, 4);
	}


	@Test
	public void testEnd()
	{
		Slice end4 = Slice.build().end(4).create();
		assertIndexes(end4.indexes(3), 0, 1, 2);
		assertIndexes(end4.indexes(5), 0, 1, 2, 3);
	}


	@Test
	public void testStep()
	{
		Slice plus2 = Slice.build().step(2).create();
		assertIndexes(plus2.indexes(5), 0, 2, 4);

		Slice revert = Slice.build().step(-1).create();
		assertIndexes(revert.indexes(5), 4, 3, 2, 1, 0);

		Slice minus3 = Slice.build().step(-3).create();
		assertIndexes(minus3.indexes(10), 9, 6, 3, 0);
	}


	@Test
	public void testStepReject()
	{
		assertThrows(IllegalArgumentException.class, () -> Slice.build().step(0));
		assertThrows(IllegalArgumentException.class, () -> new Slice(null, null, 0));
	}


	@Test
	public void testApplyToList()
	{
		Slice slice = Slice.build().start(1).create();
		assertEquals(List.of("b", "c"), slice.applyTo(List.of("a", "b", "c")));
	}


	@Test
	public void testApplyToArray()
	{
		Slice slice = Slice.build().end(2).create();
		assertArrayEquals(new String[] { "a", "b" }, slice.applyTo("a", "b", "c"));
	}


	@Test
	public void testIndexesEmpty()
	{
		// invalid len
		assertIndexesEmpty(Slice.build().end(2).create(), -1);

		// step > 0: start >= end
		assertIndexesEmpty(Slice.build().start(3).end(3).create(), 12);

		// step < 0: start <= end
		assertIndexesEmpty(Slice.build().start(3).end(3).step(-1).create(), 12);
	}


	@Test
	public void testForEachInt()
	{
		Slice.Indexes indexes = Slice.build().create().indexes(5);
		StringBuilder s = new StringBuilder();
		indexes.forEach(n -> s.append(n));
		assertEquals("01234", s.toString());
	}


	@Test
	public void testToString()
	{
		assertToString(":", Slice.build());
		assertToString("1:", Slice.build().start(1));
		assertToString("1:2", Slice.build().start(1).end(2));
		assertToString("1:5:2", Slice.build().start(1).end(5).step(2));
	}


	@Test
	public void testEqualsHashCode()
	{
		Slice start0 = Slice.build().start(0).create();
		Slice end1 = Slice.build().end(1).create();
		Slice step2 = Slice.build().step(2).create();
		Slice start0End1 = Slice.build().start(0).end(1).create();
		Slice start0Step2 = Slice.build().start(0).step(2).create();

		assertEquals(start0, start0);
		assertEquals(end1, end1);
		assertEquals(step2, step2);

		assertNotEquals(0, start0.hashCode());

		assertNotEquals(start0, Long.valueOf(1));
		assertNotEquals(start0, end1);
		assertNotEquals(start0, step2);
		assertNotEquals(start0, start0End1);
		assertNotEquals(start0, start0Step2);
	}


	private static void assertIndexes(Slice.Indexes indexes, int... expected)
	{
		assertArrayEquals(expected, indexes.get());
	}


	private static void assertToString(String expected, Slice.Builder builder)
	{
		assertEquals(expected, builder.create().toString());
	}


	private static void assertIndexesEmpty(Slice slice, int len)
	{
		assertSame(Indexes.EMPTY, slice.indexes(len));
	}
}
