# Package jd.commons.util.function

contains counterparts to functional interfaces known from `java.util.function`
which are allowed to throw checked exceptions. All of these checked counterparts
have an additional type parameter to specify the type of thrown Exceptions:
	
	import from java.io.*;
	import from java.sql.*;
	import from jd.commons.util.function.*;
	
	XFunction<File,InputStream,IOException> fopen = FileInputStream::new;
	XPredicate<ResultSet,SQLException> hasNext = ResultSet::next;
	XConsumer ...
	XSupplier ... 
	XRunnable ...
	XBiConsumer ...
	XBiFunction ... 
	XBiPredicate ...

Like their unchecked counterparts, you can combine or chain them.

Every checked instance can be also turned into its unchecked counterpart: Any checked exception is turned automatically into a  `jd.commons.util.UncheckedException` having the checked exception as cause:

	XFunction<File,InputStream,IOException> fopenChecked = FileInputStream::new;
	Function<File,InputStream> fopenUnchecked = fopen.unchecked();

