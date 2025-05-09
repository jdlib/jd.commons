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
/**
 * Package jd.commons.util.function contains counterparts to functional 
 * interfaces known from {@code java.util.function}
 * which are allowed to throw checked exceptions. All of these checked counterparts
 * have an additional parameter type to specify the type of thrown Exceptions:
 * <pre><code>
 * import from java.io.*;
 * import from java.sql.*;
 * import from jd.commons.util.function.*;
 * 
 * XFunction&lt;File,InputStream,IOException&gt; fopen = FileInputStream::new;
 * XPredicate&lt;ResultSet,SQLException&gt;hasNext = ResultSet::next;
 * XConsumer ...
 * XSupplier ... 
 * XRunnable ...
 * XBiConsumer ...
 * XBiFunction ... 
 * XBiPredicate ...
 * </code></pre><p>
 * Like their unchecked counterparts, you can combine or chain them.<br>
 * Every checked instance can be also turned into their unchecked counterpart. 
 * Any checked exception is turned automatically into a RuntimeException 
 * {@code jd.commons.util.UncheckedException} having the checked exception as cause:
 * <pre><code>
 * XFunction&lt;File,InputStream,IOException&gt; fopenChecked = FileInputStream::new;
 * Function&lt;File,InputStream&gt; fopenUnchecked = fopen.unchecked();
 * </code></pre>
 */
package jd.commons.util.function;

