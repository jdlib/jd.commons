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
 * Copyright 2026 the original author or authors.
 */
package jd.commons.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import jd.commons.check.Check;


/**
 * Slice represents a slice expression as defined in
 * <a href="https://www.rfc-editor.org/rfc/rfc9535#name-array-slice-selector">JSON Path</a>.
 * It represents the elements at indices starting at start, incrementing by step,
 * and ending with end (which is itself excluded).
 */
public class Slice
{
	/**
	 * The start value, can be null.
	 */
	public final Integer start;

	/**
	 * The end value, can be null.
	 */
	public final Integer end;

	/**
	 * The step value, can be null.
	 */
	public final Integer step;


	/**
	 * @return a Builder to build a Slice.
	 */
	public static Builder build()
	{
		return new Builder();
	}


	/**
	 * A Slice builder class.
	 */
	public static class Builder
	{
		private Integer start;
		private Integer end;
		private Integer step;


		/**
		 * Sets the start value.
		 * @param value the value
		 * @return this
		 */
		public Builder start(int value)
		{
			this.start = value;
			return this;
		}


		/**
		 * Sets the end value.
		 * @param value the value
		 * @return this
		 */
		public Builder end(int value)
		{
			this.end = value;
			return this;
		}


		/**
		 * Sets the step value, must not be 0.
		 * @param value the value
		 * @return this
		 */
		public Builder step(int value)
		{
			if (value == 0)
				throw new IllegalArgumentException("step must not be 0");
			this.step = value;
			return this;
		}


		public Slice create()
		{
			return new Slice(start, end, step);
		}
	}


	public Slice(Integer start, Integer end, Integer step)
	{
		if ((step != null) && (step.intValue() == 0))
			throw new IllegalArgumentException("step must not be 0");
		this.start = start;
		this.end = end;
		this.step = step;
	}


	public <T> List<T> applyTo(List<T> list)
	{
		Check.notNull(list, "list");
		Indexes indexes = indexes(list.size());
		List<T> result = new ArrayList<>(indexes.length);
		indexes.forEach((src,dest) -> result.add(list.get(src)));
		return result;
	}


	public <T> T[] applyTo(@SuppressWarnings("unchecked") T... array)
	{
		Check.notNull(array, "array");
		Indexes indexes = indexes(array.length);
		T[] result = Utils.newArray(array, indexes.length);
		indexes.forEach((src,dest) -> result[dest] = array[src]);
		return result;
	}


	private int calcStart(int len, int step)
	{
		int start;
	    	if (this.start != null)
	    		start = this.start.intValue();
	    	else
	    		start = step >= 0 ? 0 : len - 1;
	    	// normalize
	    	return start >= 0 ? start : len + start;
	}


	private int calcEnd(int len, int step)
	{
		int end;
	    	if (this.end != null)
	    		end = this.end.intValue();
	    	else
	    		end = step >= 0 ? len : -len - 1;
	    	// normalize
	    	return end >= 0 ? end : len + end;
	}


	public Indexes indexes(int len)
	{
	    if (len <= 0)
		    return Indexes.EMPTY;

	    int step = this.step != null ? this.step.intValue() : 1;
        	int start = calcStart(len, step);
        	int end   = calcEnd(len, step);
        	return indexes(len, start, end, step);
	}


	private static Indexes indexes(int srcLen, int start, int end, int step)
	{
		int lower, upper, destLen;
		if (step >= 0)
		{
			// forward
			lower 	= Math.min(Math.max(start, 0), srcLen);
			upper 	= Math.min(Math.max(end, 0), srcLen);
			destLen = lower < upper ? ceil(upper - lower, step) : 0;
		}
		else
		{
			// backward
			upper	= Math.min(Math.max(start, -1), srcLen - 1);
			lower 	= Math.min(Math.max(end, -1), srcLen - 1);
			destLen = lower < upper ? ceil(upper - lower, -step) : 0;
		}

		return destLen > 0 ? new Indexes(lower, upper, step, destLen) : Indexes.EMPTY;
	}


	private static int ceil(int nominator, int denominator)
	{
		return (int)Math.ceil(nominator / (double)denominator);
	}


	/**
	 * A consumer that will receive the indexes of a slice operation
	 * for a certain list or array.
	 */
	@FunctionalInterface
	public interface SliceConsumer
	{
		/**
		 * Feeds the next index pair into the conumer
		 * @param src the index into the source list or arry
		 * @param dest the index into the destination. The first dest index
		 * 		is 0, and in each invocation the dest index is the previous + 1.
		 */
		void accept(int src, int dest);
	}


	public static class Indexes
	{
		public static final Indexes EMPTY = new Indexes(0, 0, 1, 0);
		public final int lower;
		public final int upper;
		public final int step;
		public final int length;


		private Indexes(int lower, int upper, int step, int length)
		{
			this.lower = lower;
			this.upper = upper;
			this.step = step;
			this.length = length;
		}


		public int length()
		{
			return length;
		}


		public int[] get()
		{
			int result[] = new int[length()];
			forEach((src,dest) -> result[dest] = src);
			return result;
		}


		public void forEach(IntConsumer intConsumer)
		{
			SliceConsumer sc = (src, dest) -> intConsumer.accept(src);
			forEach(sc);
		}


		public void forEach(SliceConsumer consumer)
		{
            if (step >= 0)
                forEachForward(consumer);
            else
            	forEachBackward(consumer);
		}


		private void forEachForward(SliceConsumer consumer)
		{
		    int src = lower;
		    int dest = 0;
		    while (src < upper)
		    {
		    	consumer.accept(src, dest++);
		    	src += step;
		    }
		}


		private void forEachBackward(SliceConsumer consumer)
		{
		    int i = upper;
		    int dest = 0;
		    while (lower < i)
		    {
		    	consumer.accept(i, dest++);
		        i += step;
		    }
		}
	}


	@Override
	public int hashCode()
	{
		return Objects.hash(start, end, step);
	}


	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Slice)
		{
			Slice o = (Slice)other;
			return Objects.equals(start, o.start)
				&& Objects.equals(end, o.end)
				&& Objects.equals(step, o.step);
		}
		return false;
	}


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
	    if (start != null)
	        sb.append(start);
	    sb.append(':');
	    if (end != null)
	        sb.append(end);
	    if (step != null)
	        sb.append(':').append(step);
	    return sb.toString();
	}
}
