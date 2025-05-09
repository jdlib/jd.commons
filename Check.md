# jd.commons.check.Check

The Check class allows you to easily validate method arguments for most common use cases.

Let's dive into an example. Given a method 

	public void calculate(String id, List<String> names, double percent)
	
we want to ensure that

* id is not null
* names is not empty and does not contain null elements
* percent is >= 0 and <= 100.0

Here is how the Check class can help you

	import jd.commons.check.Check;
	
	public void calculate(String id, List<String> names, double percent) {
		Check.notNull(id, "id");
		Check.elems(names, "names").notEmpty().noneNull();
		Check.value(percent, "percent").greaterEq(0.0).lessEq(100.0);
		...  
	}
	
 On validation failure a `IllegalArgumentException` would be thrown with error messages like:
 
	"id is null"
	"names contain null"
	"percent is 110.0, expected to be <= 100.0"
	
Typically you only pass a variable value to a Check method plus the variable name which is enough to construct a meaningful error message if a validation fails.

Most methods usually return the value under test to allow for easy further processing:

	this.id = Check.notNull(id, "id");

## Provided Validations
The following list shows the methods provided by `Check` for *simple* argument checking:

* `Check.notNull(Object, String what)`
* `Check.notEmpty(CharSequence|Array|Collection|Map, String what)`
* `Check.notBlank(CharSequence, String what)`
* `Check.notEquals(Object, Object)`
* `Check.notSame(Object, Object)`
* `Check.equal(Object, Object)`
* `Check.same(Object, Object)`
* `Check.isA(Object, Class, String what)`
* `Check.isFalse(boolean, String what)`, `Check.isTrue(boolean, String what)`
* `Check.isNull(Object, String what)`
* `Check.derivedFrom(Class, Class)`

Additionally `Check` provides methods which return a specialized `Check` object for the value under test, providing a fluent API for a series of checks:

* `Check.elems(Array|Iterable)`,
* `Check.file(File)`
* `Check.index(int)`
* `Check.length(long|CharSequence|Array|File|List)`
* `Check.path(Path|FilePath)`
* `Check.size(long|Collection|Map)`
* `Check.value(double|int|long)`

Examples:
	
	import java.io.File;
	import java.util.List;
	
	File input = ...
	List<String> names = ...
	double price = ...
		
	Check.file(input, "input").isFile().length().greater(0);
	Check.size(name, "names").greater(5).lessEq(10);
	Check.value(price, "price").greater(4.5);
	
## Why use Check of jd.commons?

...because `Check`
* allows for dense and readable code,
* produces high quality error messages,
* provides a lot of validations out of the box,
* has low runtime overhead.

Other popular validation classes are `java.util.Objects` from the JDK, `com.google.common.base.Preconditions` from Guava, `org.apache.commons.lang3.Validate` from Apache Commons.

Given our example this is how these alternatives would solve the challenge:

A pure Java solution:

	import java.util.Objects;

	Objects.requireNonNull(id, "id");
	Objects.requireNonNull(names, "names");
	f (names.isEmpty()))
		throw new IllegalArgumentException("names is empty");
	if (names.stream().anyMatch(Objects::isNull))
		throw new IllegalArgumentException("names contains null elements");
	if (percent < 0.0 || percent > 100.0)
		throw new IllegalArgumentException("percent must be >= 0.0 and <= 100.0, is " + percent);

A Guava solution:

	import com.google.common.base.Preconditions;

	Preconditions.checkNotNull(id, "id cannot be null");
	Preconditions.checkArgument(names != null && !names.isEmpty(), "names cannot be null or empty");
	Preconditions.checkArgument(names.stream().noneMatch(java.util.Objects::isNull), "names cannot contain null elements");
	Preconditions.checkArgument(percent >= 0.0 && percent <= 100.0, "percent must be between 0.0 and 100.0");

An Apache Commons solution:

	import org.apache.commons.lang3.Validate;

    Validate.notNull(id, "id cannot be null");
    Validate.notEmpty(names, "names cannot be null or empty");
    Validate.noNullElements(names, "names cannot contain null elements");
    Validate.inclusiveBetween(0.0, 100.0, percent, "percent must be between 0.0 and 100.0");