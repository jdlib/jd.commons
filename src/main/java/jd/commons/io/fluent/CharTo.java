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


import java.io.Writer;
import java.sql.Clob;
import jd.commons.check.Check;
import jd.commons.util.function.XSupplier;


/**
 * CharTo allows to specify a target for character output.
 * <ul>
 * <li>Directly specify a {@link #to(Writer) Writer}
 * <li>Directly specify a {@link #to(CharTarget) CharTarget} 
 * <li>Specify IO related objects which
 *     can be turned into a Writer or CharTarget. The default implementations
 *     of these methods forward them to {@link #to(Writer)} or {@link #to(CharTarget)}
 * </ul>
 * The return value computed from the Writer or CharTarget depends on the implementation.
 * @param<T> the result type
 * @param<E> the exception thrown by the CharTo methods 
 */
public interface CharTo<T,E extends Exception>
{
	/**
	 * Accepts a CharTarget.
	 * @param target the target, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public T to(CharTarget target) throws E;
	
	
	/**
	 * Accepts a Writer.
	 * The Writer will not be closed by subsequent IO operations.
	 * @param writer the Writer, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public T to(Writer writer) throws E;
	
	
	/**
	 * Accepts a Appendable.
	 * @param appendable a Appendable, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(Appendable appendable) throws E
	{
		return to(IOHelper.toWriter(appendable));
	}
	
	
	/**
	 * Accepts a Clob.
	 * @param clob a Clob, not null
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(Clob clob) throws E
	{
		return to(clob, 1);
	}
	
	
	/**
	 * Accepts a Clob.
	 * @param clob a Clob, not null
	 * @param pos the position at which to start writing. The first position is 1
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T to(Clob clob, long pos) throws E
	{
		Check.notNull(clob, "clob");
		Check.value(pos, "pos").greaterEq(1);
		return toSupplier(() -> clob.setCharacterStream(pos));
	}	
	
	
	/**
	 * Creates a CharTarget which throws the exception when a Writer is requested.
	 * @param e either an exception (which is turned into a RuntimeException or IOException),
	 * 		or any other objects which is used as message for a IOException.
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T toError(Object e) throws E
	{
		return toSupplier(IOHelper.getThrowsIOorRTException(e));
	}

	
	/**
	 * Forwards {@link Writer#nullWriter()} to {@link #to(Writer)}.
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T toNull() throws E
	{
		return to(Writer.nullWriter());
	}

	
	/**
	 * Accepts a supplier whose writer will be used in write operations.
	 * @param supplier a supplier, not null, allowed to throw any exception
	 * @return the computed result
	 * @throws E if an error occurs
	 */
	public default T toSupplier(XSupplier<? extends Writer,?> supplier) throws E
	{
		Check.notNull(supplier, "supplier");
		return to(new GenericCharTarget<>(supplier));
	}
}
