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


import java.io.CharArrayReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import jd.commons.check.Check;
import jd.commons.util.function.XSupplier;


/**
 * CharFrom allows to specify a source for character input.
 * <ul>
 * <li>Directly specify a {@link #from(Reader) Reader}
 * <li>Directly specify a {@link #from(CharSource) CharSource} 
 * <li>Specify IO related objects which
 *     can be turned into a Reader or CharSource. The default implementations
 *     of these methods forward them to {@link #from(Reader)} or {@link #from(CharSource)}
 * </ul>
 * The return value computed from the Reader or CharSource depends on the implementation.
 * @param<T> the result type
 * @param<E> the exception thrown by the CharFrom methods 
 */
public interface CharFrom<T,E extends Exception>
{
	/**
	 * Accepts a CharSource.
	 * @param source a CharSource, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public T from(CharSource source) throws E;

	
	/**
	 * Accepts a Reader.
	 * @param reader a Reader, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public T from(Reader reader) throws E;
	

	/**
	 * Creates a {@link CharArrayReader} and forwards to {@link #from(Reader)}.
	 * @param chars a character array
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T from(char... chars) throws E
	{
		Check.notNull(chars, "chars");
		// don't call "from(new CharArrayReader(s))", but create a CharFrom which
		// can be called repeatedly
		return from(() -> new CharArrayReader(chars));
	}
	
	
	/**
	 * Creates a {@link StringReader} for the string and forwards to {@link #from(Reader)}.
	 * @param s a String, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T fromString(String s) throws E
	{
		Check.notNull(s, "s");
		// don't call "from(new StringReader(s))", but create a CharFrom with
		// can be called repeatedly
		return from(() -> new StringReader(s));
	}

	
	/**
	 * Creates a CharSource to read from a Clob and forwards to {@link CharFrom#from(CharSource)}.
	 * @param clob a Clob, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T from(Clob clob) throws E
	{
		Check.notNull(clob, "clob");
		return fromSupplier(clob::getCharacterStream);
	}
	
	
	/**
	 * Creates a CharSource to read from a Clob and forwards to {@link CharFrom#from(CharSource)}.
	 * @param clob a Clob, not null
	 * @param pos the position at which to start reading. The first position is 1
	 * @param length the length in characters of the partial Clob value to be retrieved.
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T from(Clob clob, long pos, long length) throws E
	{
		Check.notNull(clob, "clob");
		Check.value(pos, "pos").greaterEq(1);
		return fromSupplier(() -> clob.getCharacterStream(pos, length));
	}	
	
	
	/**
	 * Creates a CharSource which throws the exception when a Reader is requested.
	 * @param e either an exception (which is turned into a RuntimeException or IOException),
	 * 		or any other objects which is used as message for a IOException.
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T fromError(Object e) throws E
	{
		return fromSupplier(IOHelper.getThrowsIOorRTException(e));
	}

	
	/**
	 * Creates a CharSource which returns the Reader provided by the supplier.
	 * @param supplier a supplier, not null, allowed to throw any exception
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T fromSupplier(XSupplier<? extends Reader,?> supplier) throws E
	{
		Check.notNull(supplier, "supplier");
		return from(new GenericCharSource<>(supplier));
	}
}
