# Utilities

This page lists utility classes provided by package `jd.commons.util`.

## jd.commons.util.Utils

A classic collection of static utility methods:

	import jd.commons.util.Utils;
	
	String s = ... 
	s = Utils.cutStart(s, "/"); // removes a leading "/" from the String
	s = Utils.padEnd(s, 15, '.');
	s = Utils.startUpperCase(s);
	
## jd.commons.util.ClassLoad	

provides easy class loading:

	import jd.commons.util.ClassLoad;
	import org.example.Type;
	
	Class<?> cls = ClassLoad.forName("org.example.StringType").orNull();
	ClassLoader cl = ...;
	Class<? extends Type> cls = ClassLoad
		.forName("org.example.StringType")
		.using(cl)
		.derivedFrom(Type.class)
		.orThrow(IllegalStateException::new);


## jd.commons.util.Slice

a [slice operator](https://www.rfc-editor.org/rfc/rfc9535#name-array-slice-selector) for Java:

	import jd.common.util.Slice;
	
	Slice slice = Slice.build().start(5).end(1).step(-2).create();
	List<String> input = ...
	List<String> sliced = slice.applyTo(input);


## jd.commons.util.Arguments

allows to process command-line arguments passed to the `main` method:

	import jd.common.util.Arguments;
	
	Arguments args = new Arguments();
	boolean showHelp = args.consumeAny("-?", "-h", "--help");
	File inputFile = args.next("inputFile").required().asFile();
	
## jd.commons.util.Unbox

provides safe conversion methods from Numbers, Characters and Boolean objects to primitive values:

	import jd.common.util.Unbox;
	
	Number n = ...
	double d = Unbox.toDouble(n);
	double d = Unbox.toDouble(n, 1.0);
	int i    = Unbox.toInt(n);
	int i    = Unbox.toInt(n, 5);
