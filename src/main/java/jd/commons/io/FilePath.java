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
package jd.commons.io;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.CheckReturnValue;
import jd.commons.check.Check;
import jd.commons.io.fluent.AsCharset;
import jd.commons.io.fluent.ByteReadData;
import jd.commons.io.fluent.ByteSource;
import jd.commons.io.fluent.ByteTarget;
import jd.commons.io.fluent.ByteWriteData;
import jd.commons.io.fluent.CharReadData;
import jd.commons.io.fluent.CharWriteData;
import jd.commons.io.fluent.IO;
import jd.commons.io.fluent.handler.ErrorFunction;
import jd.commons.util.UncheckedException;
import jd.commons.util.function.XConsumer;
import jd.commons.util.function.XFunction;


/**
 * Represents a file in a file system as a system dependent path.
 * <p>
 * Technically {@code FilePath} wraps a {@link java.nio.file.Path} and provides
 * a user friendly interface for {@code Path} operations, especially
 * it exposes static Path methods in {@code java.nio.file.Files} as instance methods.
 * <p>
 * Java 1.0 introduced the {@code File} class to model files in
 * a local file system.<br>
 * In order to generalize to other file systems (e.g. archives, JRT) and add missing features
 * like file attributes, Java 1.7 introduced {@code Path} as a replacement for {@code File}.
 * {@code Path} designers decided to provide lot of functionality as static
 * methods in the new {@code java.nio.file.Files} class which makes using the {@code Path}
 * hard and non-intuitive.
 * <p>
 * {@code FilePath} tries to provide the best of both worlds:
 * <ul>
 * <li>ease of use of the {@code java.io.File} class
 * <li>features of {@code java.nio.file.Path} class provided as instance methods
 * <li>using builders and fluent APIs to keep a easy to use interface
 * </ul>
 */
public class FilePath implements Comparable<FilePath>
{
	/**
	 * Categorizes a FilePath.
	 */
	public enum Type
	{
		DIRECTORY,
		REGULAR_FILE,
		SYMBOLIC_LINK,
		OTHER
	}


	/**
	 * The path wrapped by this FilePath.
	 */
	protected final Path path_;


	/**
	 * @return a FilePath object for the given Path.
	 * @param path a Path, not null
	 */
	public static FilePath of(Path path)
	{
		return new FilePath(path);
	}


	/**
	 * @return a FilePath object for the given File.
	 * @param file a File, not null
	 * @see File#toPath()
	 */
	public static FilePath of(File file)
	{
		return of(Check.notNull(file, "file").toPath());
	}


	/**
	 * @return a FilePath object for the given URI.
	 * @param uri a URI, not null
	 * @see Path#of(URI)
	 */
	public static FilePath of(URI uri)
	{
		return of(Path.of(Check.notNull(uri, "uri")));
	}


	/**
	 * @return a {@code FilePath} by converting a path string, or a sequence of
	 * strings that when joined form a path string.
	 * @param first the path string or initial part of the path string
	 * @param more additional strings to be joined to form the path string
	 * @see Path#of(String, String...)
	 */
	public static FilePath of(String first, String... more)
	{
		return of(Path.of(first, more));
	}


	/**
	 * @return the FilePath for the current user directory.
	 */
	public static FilePath userDir()
	{
		return of(System.getProperty("user.dir"));
	}


	/**
	 * @return the FilePath for the system temp directory.
	 */
	public static FilePath tempDir()
	{
		return of(System.getProperty("java.io.tmpdir"));
	}


	private static FilePath ofNullable(Path path)
	{
		return path != null ? new FilePath(path) : null;
	}


	/**
	 * Creates a FilePath object for the given path.
	 * @param path a Path, not null
	 */
	public FilePath(Path path)
	{
		path_ = Check.notNull(path, "path");
	}


	/**
	 * @return a builder object which allows to access the attributes of this path.
	 * @param options indicating how symbolic links are handled.
	 * 		By default, if you don't provide any options then symbolic links are followed
	 * 		and the attributes of the final target of the link are returned.
	 * 		If the option {@link LinkOption#NOFOLLOW_LINKS NOFOLLOW_LINKS} is present
	 * 		then symbolic links are not followed.
	 * @see #attrsNoFollowLinks()
	 */
	@CheckReturnValue
	public Attributes attributes(LinkOption... options)
	{
		return new Attributes(options);
	}


	/**
	 * @return a builder object which allows to access the attributes of this path.
	 * Symbolic links are not followed.
	 * @see #attributes(LinkOption...)
	 */
	@CheckReturnValue
	public Attributes attrsNoFollowLinks()
	{
		return attributes(LinkOption.NOFOLLOW_LINKS);
	}


	/**
	 * Provides access to attributes of this FilePath.
	 * @see FilePath#attributes(LinkOption...)
	 * @see FilePath#attrsNoFollowLinks()
	 */
	public class Attributes
	{
		private final LinkOption[] options_;


		/**
		 * Creates a new Attributes object.
		 * @param options the link options
		 */
		protected Attributes(LinkOption[] options)
		{
			options_ = options;
		}


		/**
		 * @return the BasicFileAttributes of the FilePath.
		 * @throws IOException if an I/O error occurs
		 */
		public BasicFileAttributes basic() throws IOException
		{
			return type(BasicFileAttributes.class);
		}


		/**
		 * @return the value of an attribute.
		 * @param attribute the attribute name.
		 * 		Consult {@link Files#getAttribute(Path, String, LinkOption...)}
		 * 		to learn about the possible values for this parameter.
		 * @throws IOException if an I/O error occurs
		 */
		public Object get(String attribute) throws IOException
		{
			return Files.getAttribute(path_, attribute, options_);
		}


		/**
		 * @return a map of attribute names and its values.
		 * @param attributes the attributes to read.
		 * 		Please consult {@link Files#readAttributes(Path, String, LinkOption...)}
		 * 		to learn about the possible values for this parameter.
		 * @throws IOException if an I/O error occurs
		 * @see Files#readAttributes(Path, String, LinkOption...)
		 */
		public Map<String,Object> map(String attributes) throws IOException
		{
			return Files.readAttributes(path_, attributes, options_);
		}


		/**
		 * @return the {@link FileOwnerAttributeView} of the Path.
		 */
		public FileOwnerAttributeView ownerView()
		{
			return view(FileOwnerAttributeView.class);
		}


		/**
		 * @return the {@link PosixFileAttributes} of this path.
		 * @throws IOException if an I/O error occurs
		 */
		public PosixFileAttributes posix() throws IOException
		{
			return type(PosixFileAttributes.class);
		}


		/**
		 * Sets the value of an attribute.
		 * @param attribute the attribute
		 * @param value the value
		 * @return this
		 * @throws IOException if an I/O error occurs
		 * @see Files#setAttribute(Path, String, Object, LinkOption...)
		 */
		public Attributes set(String attribute, Object value) throws IOException
		{
			Files.setAttribute(path_, attribute, value, options_);
			return this;
		}


		/**
		 * @return an object providing specific attributes of this path.
		 * @param attrClass a class derived from BasicFileAttributes
		 * @param<A> the type of attrClass
		 * @throws IOException if an I/O error occurs
		 * @see Files#readAttributes(Path, String, LinkOption...)
		 */
		public <A extends BasicFileAttributes> A type(Class<A> attrClass) throws IOException
		{
			return Files.readAttributes(path_, attrClass, options_);
		}


		/**
		 * @return an object providing a specific attributes view of this path.
		 * @param viewClass a class derived from FileAttributeView
		 * @param<V> the type of viewClass
		 * @see Files#getFileAttributeView(Path, Class, LinkOption...)
		 */
		public <V extends FileAttributeView> V view(Class<V> viewClass)
		{
			return Files.getFileAttributeView(path_, viewClass, options_);
		}
	}


	/**
	 * @return a Ancestors object which provides access to the ancestors of this path.
	 */
	@CheckReturnValue
	public Ancestors ancestors()
	{
		return new Ancestors();
	}


	/**
	 * Represents the ancestors of this path.
	 */
	public class Ancestors implements Iterable<FilePath>
	{
		private Predicate<FilePath> filter_;
		private boolean includeSelf_;


		protected Ancestors()
		{
		}


		/**
		 * Instructs this builder to also include the start path itself.
		 * @return this
		 */
		public Ancestors orSelf()
		{
			includeSelf_ = true;
			return this;
		}


		/**
		 * Restricts to ancestors which match the filter.
		 * @param filter the filter. Overrides any previous filter.
		 * @return this
		 */
		public Ancestors filter(Predicate<FilePath> filter)
		{
			filter_ = filter;
			return this;
		}


		/**
		 * @return an Optional containing the first matching ancestor or null.
		 */
		public Optional<FilePath> first()
		{
			return Optional.ofNullable(firstOrNull());
		}


		/**
		 * @return the first matching ancestor or null.
		 */
		public FilePath firstOrNull()
		{
			FilePath p = includeSelf_ ? FilePath.this : getParent();
			while (p != null)
			{
				if (filter_ == null || filter_.test(p))
					return p;
				p = p.getParent();
			}
			return null;
		}


		/**
		 * @return an iterator over the matched ancestors.
		 */
		@Override public Iterator<FilePath> iterator()
		{
			return new AncestorsIterator(filter_, includeSelf_ ? FilePath.this : getParent());
		}


		/**
		 * @return the matched ancestors as List.
		 */
		public List<FilePath> toList()
		{
			List<FilePath> list = new ArrayList<>();
			forEach(list::add);
			return list;
		}
	}


	private static class AncestorsIterator implements Iterator<FilePath>
	{
		private FilePath next_;
		private final Predicate<FilePath> filter_;


		public AncestorsIterator(Predicate<FilePath> filter, FilePath start)
		{
			filter_ = filter; // must be set before calling findNext
			setNext(start);
		}


		private void setNext(FilePath candidate)
		{
			while (candidate != null && !match(candidate))
				candidate = candidate.getParent();
			next_ = candidate;
		}


		private boolean match(FilePath path)
		{
			return filter_ == null || filter_.test(path);
		}


		@Override public boolean hasNext()
		{
			return next_ != null;
		}


		@Override public FilePath next()
		{
			if (next_ == null)
				throw new NoSuchElementException();
			FilePath current = next_;
			setNext(next_.getParent());
			return current;
		}
	}



	/**
	 * @return a Children object which provides access to the children of this path.
	 */
	@CheckReturnValue
	public Children children()
	{
		return new Children(null, null);
	}


	/**
	 * Represents the children of this path.
	 */
	public class Children
	{
		private String glob_;
		private Predicate<FilePath> filter_;


		private Children(String glob, Predicate<FilePath> filter)
		{
			glob_   = glob;
			filter_ = filter;
		}


		/**
		 * Returns a new children object restricted to children which match the glob pattern.
		 * To learn more about glob patterns consult {@link Files#newDirectoryStream(Path, String)}.
		 * @param pattern the glob pattern
		 * @return the new Children object
		 */
		public Children glob(String pattern)
		{
			return new Children(pattern, filter_);
		}


		/**
		 * Returns a new children object restricted to children which match the filter.
		 * @param filter the filter
		 * @return the new Children object
		 */
		public Children filter(Predicate<FilePath> filter)
		{
			if (filter_ != null)
				filter = filter_.and(filter);
			return new Children(glob_, filter);
		}


		/**
		 * @return if the children list is empty.
		 * @throws IOException if an I/O error occurs
		 */
		public boolean isEmpty() throws IOException
		{
			return apply(Stream::findAny).isEmpty();
		}


		/**
		 * @return the number of children.
		 * @throws IOException if an I/O error occurs
		 */
		public long count() throws IOException
		{
			return apply(Stream::count);
		}


		/**
		 * Deletes the children. This operation applies {@link FilePath#deleteIfExists()}
		 * to all children matching the glob pattern and filter of this Children instance.
		 * This is not a recursive delete and can fail, i.e
		 * if a child is a non-empty directory. For recursive deletes use {@link FileTree#delete()}
		 * @return the number of deleted childrens
		 * @throws IOException if an I/O error occurs
		 * @see FileTree#delete
		 */
		public int delete() throws IOException
		{
			int[] count = new int[1];
			forEach(fp -> {
				if (fp.deleteIfExists())
					count[0]++;
			});
			return count[0];
		}


		/**
		 * @return the children as List.
		 * @throws IOException if an I/O error occurs
		 */
		public List<FilePath> toList() throws IOException
		{
			return apply(s -> s.collect(Collectors.toList()));
		}


		/**
		 * Invokes the consumer for every child path.
		 * @param consumer a Consumer
		 * @throws IOException if an I/O error occurs
		 */
		public void forEach(XConsumer<FilePath,IOException> consumer) throws IOException
		{
			Check.notNull(consumer, "consumer");
			try
			{
				Consumer<FilePath> unchecked = consumer.unchecked();
				apply(stream -> { stream.forEach(unchecked); return null; });
			}
			catch (Exception e)
			{
				UncheckedException.rethrow(e, IOException.class);
			}
		}


		/**
		 * Passes a Stream of child paths to a Function and returns the result.
		 * @param fn a function
		 * @return the result
		 * @throws IOException if an I/O error occurs
		 * @param<T> the result type
		 */
		public <T> T apply(XFunction<Stream<FilePath>,T,IOException> fn) throws IOException
		{
			Check.notNull(fn, "fn");
			try (DirectoryStream<Path> dirs = open())
			{
				Stream<FilePath> stream = StreamSupport.stream(dirs.spliterator(), false).map(FilePath::of);
				if (filter_ != null)
					stream = stream.filter(filter_);
				return fn.apply(stream);
			}
		}


		private DirectoryStream<Path> open() throws IOException
		{
			return glob_ != null ?
				Files.newDirectoryStream(path_, glob_) :
					Files.newDirectoryStream(path_);
		}
	}


	/**
	 * Initiates a copy operation of this FilePath.
	 * @param options specifying how the copy should be done
	 * @return a builder to choose the target of where to copy this FilePath.
	 * @see Files#copy(Path, Path, CopyOption...)
	 */
	@CheckReturnValue
	public FileTarget copy(CopyOption... options)
	{
		return new FilePathTarget()
		{
			@Override
			protected void execute(FilePath target) throws IOException
			{
				Files.copy(path_, target.path_, options);
			}
		};
	}


	/**
	 * Creates a new and empty file, failing if the file already exists.
	 * @param attrs an optional list of file attributes to set atomically when
	 * 		creating the file
	 * @return this
	 * @throws IOException if an I/O error occurs or file already exists
	 * @see Files#createFile(Path, FileAttribute...)
	 */
	public FilePath createFile(FileAttribute<?>... attrs) throws IOException
	{
		Files.createFile(path_, attrs);
		return this;
	}


	/**
	 * Creates a new directory.
	 * @param attrs an optional list of file attributes to set atomically when
	 * 		creating the directory
	 * @return this
	 * @throws IOException if an I/O error occurs or the parent directory does not exist
	 * @see Files#createDirectory(Path, FileAttribute...)
	 */
	public FilePath createDirectory(FileAttribute<?>... attrs) throws IOException
	{
		Files.createDirectory(path_, attrs);
		return this;
	}


	/**
	 * Creates a directory by creating all nonexistent parent directories first.
	 * @param attrs an optional list of file attributes to set atomically when
	 * 		creating the directory
	 * @return this
	 * @throws IOException if an I/O error occurs or the path does not exist
	 * @see Files#createDirectories(Path, FileAttribute...)
	 */
	public FilePath createDirectories(FileAttribute<?>... attrs) throws IOException
	{
		Files.createDirectories(path_, attrs);
		return this;
	}


	/**
	 * @param attrs attributes for the symbolic link
	 * @return a Target builder to specify the target of the symbolik link
	 */
	@CheckReturnValue
	public FileTarget createSymbolicLink(FileAttribute<?>... attrs)
	{
		return new FilePathTarget()
		{
			@Override
			protected void execute(FilePath target) throws IOException
			{
				Files.createSymbolicLink(path_, target.path_, attrs);
			}
		};
	}


	/**
	 * @return a FileTarget to specifiy the link target.
	 */
	@CheckReturnValue
	public FileTarget createLink()
	{
		return new FilePathTarget()
		{
			@Override
			protected void execute(FilePath target) throws IOException
			{
				Files.createLink(path_, target.path_);
			}
		};
	}


	/**
	 * Creates a new directory in this directory, using the given prefix to generate its name.
	 * @param prefix the prefix string to be used in generating the directory's name; may be {@code null}
	 * @param attrs an optional list of file attributes to set atomically when creating the directory
	 * @throws IOException if an I/O error occurs
	 */
	public FilePath createTempDir(String prefix, FileAttribute<?>... attrs) throws IOException
	{
		return FilePath.of(Files.createTempDirectory(path_, prefix, attrs));
	}


	/**
	 * Creates a new empty file in this directory, using the given prefix and suffix to generate its name.
	 * @param prefix the prefix string to be used in generating the directory's name; may be {@code null}
	 * @param suffix the suffix string to be used in generating the file's name; may be {@code null}, in which case "{@code .tmp}" is used
	 * @param attrs an optional list of file attributes to set atomically when creating the directory
	 * @throws IOException if an I/O error occurs
	 */
	public FilePath createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) throws IOException
	{
		return FilePath.of(Files.createTempFile(path_, prefix, suffix, attrs));
	}


	/**
	 * Deletes this path.
	 * @throws IOException if an I/O error occurs or the path does not exist
	 */
	public void delete() throws IOException
	{
		Files.delete(path_);
	}


	/**
	 * Deletes this path if it exists.
	 * @return did the file exist and was deleted?
	 * @throws IOException if an I/O error occurs
	 * @see Files#deleteIfExists(Path)
	 * @see FileTree#delete()
	 */
	public boolean deleteIfExists() throws IOException
	{
		return Files.deleteIfExists(path_);
	}


	/**
	 * Recursively walks this file tree and deletes the files included in this tree.
	 * @return the number of deleted files and directories
	 * @throws IOException if an I/O error occurs
	 */
	public int deleteRecursively() throws IOException
	{
		return FileTree.of(this).delete();
	}


	/**
	 * @return if this path ends with the given path
	 * @param other a path
	 * @see Path#endsWith(Path)
	 */
	public boolean endsWith(FilePath other)
	{
		Check.notNull(other, "other");
		return path_.endsWith(other.path_);
	}


	/**
	 * @return if this path ends with the given path
	 * @param other a path
	 * @see Path#endsWith(String)
	 */
	public boolean endsWith(String other)
	{
		return path_.endsWith(other);
	}


	/**
	 * @return if the path exists.
	 * @param options indicating how symbolic links are handled
	 * @see Files#exists(Path, LinkOption...)
	 */
	public boolean exists(LinkOption... options)
	{
		return Files.exists(path_, options);
	}


	/**
	 * @return if the path exists, if symbolic links are not followed:
	 * @see Files#exists(Path, LinkOption...)
	 */
	public boolean existsNoFollowLinks()
	{
		return exists(LinkOption.NOFOLLOW_LINKS);
	}


	/**
	 * @return the extension of the file name or "" if the file name does not have a extension#.
	 */
	public String getExtension()
	{
		return getExtensionOr("");
	}


	/**
	 * @param defaultValue a default value
	 * @return the extension of the file name or the default value if the extensiopn is empty.
	 */
	public String getExtensionOr(String defaultValue)
	{
		String name = getName();
		int p = name.lastIndexOf('.');
		return p >= 0 ? name.substring(p + 1) : defaultValue;
	}


	/**
	 * @return the FileStore of this path.
	 * @throws IOException if an I/O error occurs
	 * @see Files#getFileStore(Path)
	 */
	public FileStore getFileStore() throws IOException
	{
		return Files.getFileStore(path_);
	}


	/**
	 * @return the file system that created this object.
	 * @see Path#getFileSystem()
	 */
	public FileSystem getFileSystem()
	{
		return path_.getFileSystem();
	}


	/**
	 * Returns the name of the file or directory denoted by this path as a
	 * {@code Path} object. The file name is the <em>farthest</em> element from
	 * the root in the directory hierarchy.
	 * @return  a path representing the name of the file or directory, or
	 *          empty string if this path has zero elements
	 * @see Path#getFileName()
	 */
	public String getName()
	{
		Path name = path_.getFileName();
		return name != null ? name.toString() : "";
	}


	/**
	 * @return a path representing the path's parent or null if this path does not have a parent.
	 * 		Different invocations of this method will return different objects, all representing the same parent path.
	 * @see Path#getParent()
	 */
	public FilePath getParent()
	{
		return ofNullable(path_.getParent());
	}


	/**
	 * @return  a path representing the root component of this path,
	 *          or {@code null} if this path does not have a root component.
	 * @see Path#getRoot()
	 */
	public FilePath getRoot()
	{
		return ofNullable(path_.getRoot());
	}


	/**
	 * @return the type of this path.
	 * @param options indicating how symbolic links are handled
	 * @see Files#readAttributes(Path, Class, LinkOption...)
	 */
	public Type getType(LinkOption... options)
	{
		try
		{
			BasicFileAttributes attrs = Files.readAttributes(path_, BasicFileAttributes.class, options);
			if (attrs.isDirectory())
				return Type.DIRECTORY;
			else if (attrs.isRegularFile())
				return Type.REGULAR_FILE;
			else if (attrs.isSymbolicLink())
				return Type.SYMBOLIC_LINK;
		}
		catch (IOException e)
		{
		}
		return Type.OTHER;
	}


	/**
	 * @return if this path has all the given access modes.
	 * @param modes the access modes
	 * @see FileSystemProvider#checkAccess(Path, AccessMode...)
	 */
	public boolean isAccessible(AccessMode... modes)
	{
		try
		{
			path_.getFileSystem().provider().checkAccess(path_, modes);
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}


	/**
	 * @return whether or not this path is absolute.
	 * @see Path#isAbsolute
	 */
	public boolean isAbsolute()
	{
		return path_.isAbsolute();
	}


	/**
	 * @return if this path is a directory.
	 * @param options LinkOptions
	 * @see Files#isDirectory(Path, LinkOption...)
	 */
	public boolean isDirectory(LinkOption... options)
	{
		return Files.isDirectory(path_, options);
	}


	/**
	 * @return if this path is executable.
	 * @see Files#isExecutable(Path)
	 * @see #isAccessible(AccessMode...)
	 */
	public boolean isExecutable()
	{
		return Files.isExecutable(path_);
	}


	/**
	 * @return if this file is hidden.
	 * @see Files#isHidden(Path)
	 * @throws IOException if an I/O error occurs
	 */
	public boolean isHidden() throws IOException
	{
		return Files.isHidden(path_);
	}


	/**
	 * @return if this path is readable.
	 * @see Files#isReadable(Path)
	 * @see #isAccessible(AccessMode...)
	 */
	public boolean isReadable()
	{
		return Files.isReadable(path_);
	}


	/**
	 * @return if this path is a regular file.
	 * @param options LinkOptions
	 * @see Files#isRegularFile(Path, LinkOption...)
	 */
	public boolean isRegularFile(LinkOption... options)
	{
		return Files.isRegularFile(path_, options);
	}


	/**
	 * @return if this path is the same as the other path.
	 * @param other another path, not null
	 * @throws IOException if an I/O error occurs
	 * @see Files#isSameFile(Path, Path)
	 */
	public boolean isSameFile(FilePath other) throws IOException
	{
		Check.notNull(other, "other");
		return Files.isSameFile(path_, other.path_);
	}


	/**
	 * @return if this path is a symbolic link.
	 * @throws IOException if an I/O error occurs
	 * @see Files#isSymbolicLink(Path)
	 */
	public boolean isSymbolicLink() throws IOException
	{
		return Files.isSymbolicLink(path_);
	}


	/**
	 * @return if this path is writable.
	 * @see Files#isWritable(Path)
	 * @see #isAccessible(AccessMode...)
	 */
	public boolean isWritable()
	{
		return Files.isWritable(path_);
	}


	/**
	 * @return a FileTarget object to specify the target of a move operation.
	 * @param options options for the move operation
	 * @see Files#move(Path, Path, CopyOption...)
	 */
	@CheckReturnValue
	public FileTarget move(CopyOption... options)
	{
		return new FilePathTarget()
		{
			@Override
			protected void execute(FilePath target) throws IOException
			{
				Files.move(path_, target.path_, options);
			}
		};
	}


	/**
	 * @return a Builder to open a binary stream or channel to this file.
	 */
	@CheckReturnValue
	public ByteOpen open()
	{
		return new ByteOpen();
	}


	private static final OpenOption[] EMPTY_OPEN_OPTIONS = new OpenOption[0];


	/**
	 * A builder to open a binary stream or channel to this file.
	 */
	public class ByteOpen implements AsCharset<CharOpen>
	{
		private OpenOption[] options_ = EMPTY_OPEN_OPTIONS;


		/**
		 * @return a builder to open a character stream to this FilePath, encoded or decoded
		 * by the given Charset.
		 * @param charset a charset, not null
		 */
		@CheckReturnValue
		@Override
		public CharOpen as(Charset charset)
		{
			return new CharOpen(charset, options_);
		}


		/**
		 * Opens or creates a file, returning a seekable byte channel to access the file.
		 * @param attrs an optional list of file attributes to set atomically when creating the file
		 * @return the channel
		 * @throws IOException if an I/O error occurs
		 * @see Files#newByteChannel(Path, Set, FileAttribute...)
		 */
		public SeekableByteChannel channel(FileAttribute<?>... attrs) throws IOException
		{
			return Files.newByteChannel(path_, Set.of(options_), attrs);
		}


		/**
		 * @return an InputStream to read from this FilePath.
		 * @throws IOException if an I/O error occurs
		 */
		public InputStream inputStream() throws IOException
		{
			return Files.newInputStream(path_, options_);
		}


		/**
		 * @return an OutputStream to write to this FilePath.
		 * @throws IOException if an I/O error occurs
		 */
		public OutputStream outputStream() throws IOException
		{
			return Files.newOutputStream(path_, options_);
		}


		/**
		 * Sets the options to {@link StandardOpenOption#APPEND}
		 * @return this
		 */
		public ByteOpen append()
		{
			return options(StandardOpenOption.APPEND);
		}


		/**
		 * Specifies options how to open this file.
		 * @param options the options
		 * @return this
		 */
		public ByteOpen options(OpenOption... options)
		{
			Check.elems(options, "options").noneNull();
			options_ = options;
			return this;
		}
	}


	/**
	 * A a builder to open a character stream to this FilePath.
	 */
	public class CharOpen
	{
		private final Charset charset_;
		private OpenOption[] options_ = EMPTY_OPEN_OPTIONS;


		private CharOpen(Charset charset, OpenOption[] options)
		{
			charset_ = Check.notNull(charset, "charset");
			options_ = options;
		}


		/**
		 * @return a Reader to read from this file.
		 * @throws IOException if an I/O error occurs
		 */
		public Reader reader() throws IOException
		{
			return new InputStreamReader(Files.newInputStream(path_, options_), charset_);
		}


		/**
		 * @return a BufferedReader to read from this file.
		 * @throws IOException if an I/O error occurs
		 */
		public BufferedReader bufferedReader() throws IOException
		{
			return new BufferedReader(reader());
		}


		/**
		 * @return a Writer to write to this file.
		 * @throws IOException if an I/O error occurs
		 */
		public Writer writer() throws IOException
		{
			return new OutputStreamWriter(Files.newOutputStream(path_, options_), charset_);
		}


		/**
		 * @return a PrintWriter to write to this file.
		 * @throws IOException if an I/O error occurs
		 */
		public PrintWriter printWriter() throws IOException
		{
			return new PrintWriter(writer());
		}
	}


	/**
	 * @return a path that is this path with redundant name elements eliminated.
	 * @see Path#normalize()
	 */
	public FilePath normalize()
	{
		return thisOrNew(path_.normalize());
	}


	/**
	 * @return if the path does not exists
	 * @param options indicating how symbolic links are handled
	 * @throws IOException if an I/O error occurs
	 * @see Files#notExists(Path, LinkOption...)
	 */
	public boolean notExists(LinkOption... options) throws IOException
	{
		return Files.notExists(path_, options);
	}


	/**
	 * Probes the content type of a file.
	 * @return  The content type of the file, or {@code null} if the content
	 *          type cannot be determined
	 * @see Files#probeContentType(Path)
	 * @throws IOException if an I/O error occurs
	 */
	public String probeContentType() throws IOException
	{
		return Files.probeContentType(path_);
	}


	/**
	 * @return a builder which allows to read the content of this file.
	 */
	@CheckReturnValue
	public ByteRead read()
	{
		return new ByteRead(IO.Bytes.from(path_));
	}


	public class ByteRead extends ByteReadData<IOException> implements AsCharset<CharReadData<IOException>>
	{
		private ByteRead(ByteSource source)
		{
			super(source, ErrorFunction.throwUncheckedOrIOE());
		}


		@Override
		public CharReadData<IOException> as(Charset charset)
		{
			return source_.as(charset).read();
		}
	}


	/**
	 * Constructs a relative path between this path and a given path.
	 * @param other the other path
	 * @see Path#relativize(Path)
	 * @return the new path
	 * @see Path#relativize(Path)
	 */
	public FilePath relativize(FilePath other)
	{
		Check.notNull(other, "other");
		return thisOrNew(path_.relativize(other.path_));
	}


	/**
	 * Resolve the given path against this path.
	 * @param other the path to resolve against this path
	 * @return the resulting path
	 * @see Path#resolve(Path)
	 */
	public FilePath resolve(FilePath other)
	{
		Check.notNull(other, "other");
		return thisOrNew(path_.resolve(other.path_));
	}


	/**
	 * Converts a given path string to a {@code FilePath} and resolves it against
	 * this {@code FilePath}
	 * @param other the path string to resolve against this path
	 * @return the resulting path
	 * @see Path#resolve(String)
	 */
	public FilePath resolve(String other)
	{
		return thisOrNew(path_.resolve(other));
	}


	/**
	 * Iteratively resolves path strings against this path.
	 * @param other the initial path string to resolve against this path
	 * @param more more path strings to resolve
	 * @return the resulting path
	 */
	public FilePath resolve(String other, String... more)
	{
		FilePath p = resolve(other);
		for (String m : more)
			p = p.resolve(m);
		return p;
	}


	/**
	 * Resolves the given path against this path's {@link #getParent parent}
	 * path.
	 * @param other the path string to resolve against this path's parent
	 * @return the resulting path
	 * @see Path#resolveSibling(Path)
	 */
	public FilePath resolveSibling(FilePath other)
	{
		Check.notNull(other, "other");
		return thisOrNew(path_.resolveSibling(other.path_));
	}


	/**
	 * Converts a given path string to a {@code FilePath} and resolves it against
	 * this path's {@link #getParent parent} path.
	 * @param other the path string to resolve against this path's parent
	 * @return the resulting path
	 * @see Path#resolveSibling(String)
	 * @see FilePath#resolveSibling(FilePath)
	 */
	public FilePath resolveSibling(String other)
	{
		return thisOrNew(path_.resolveSibling(other));
	}


	/**
	 * @return the path to which this symbolic link points to-
	 * @throws IOException if an I/O error occurs
	 * @see Files#readSymbolicLink(Path)
	 */
	public FilePath resolveSymbolicLink() throws IOException
	{
		return thisOrNew(Files.readSymbolicLink(path_));
	}


	/**
	 * @return the size of the path.
	 * @throws IOException if an I/O error occurs
	 */
	public long size() throws IOException
	{
		return Files.size(path_);
	}


	/**
	 * @return if this path starts with the other path.
	 * @param other another path
	 */
	public boolean startsWith(FilePath other)
	{
		Check.notNull(other, "other");
		return path_.startsWith(other.path_);
	}


	/**
	 * @return if this path starts with the other path.
	 * @param other another path
	 */
	public boolean startsWith(String other)
	{
		return path_.startsWith(other);
	}


	/**
	 * @return a builder to specify what to write to this FilePath.
	 */
	@CheckReturnValue
	public ByteWrite write()
	{
		return new ByteWrite(IO.Bytes.to(this));
	}


	/**
	 * A builder class to specify what to write to this {@link FilePath}.
	 * @see FilePath#write()
	 */
	public class ByteWrite extends ByteWriteData<Void,IOException> implements AsCharset<CharWriteData<Void,IOException>>
	{
		private ByteWrite(ByteTarget target)
		{
			super(target, ErrorFunction.throwUncheckedOrIOE());
		}


		/**
		 * Configures this builder to append to an existing file.
		 * Calls {@link #append()} with {@link StandardOpenOption#APPEND}
		 * @return a new builder object with updated options
		 */
		public ByteWrite append()
		{
			return options(StandardOpenOption.APPEND);
		}


		/**
		 * Specifies options how to open this file.
		 * @param options the options
		 * @return a new builder object with updated options
		 * @see Files#newOutputStream(Path, OpenOption...)
		 */
		public ByteWrite options(OpenOption... options)
		{
			return new ByteWrite(IO.Bytes.to(FilePath.this, options));
		}


		/**
		 * Returns a {@link CharWriteData} to write the bytes of this source encoded with
		 * a certain charset.
		 * @param charset a charset, not null
		 * @return t the CharWriteData
		 */
		@CheckReturnValue
		@Override
		public CharWriteData<Void,IOException> as(Charset charset)
		{
			return new CharWriteData<>(target_.as(charset), error_);
		}
	}


	private FilePath thisOrNew(Path path)
	{
		return path == path_ || path_.equals(path) ? this : of(path);
	}


	@Override
	public int compareTo(FilePath other)
	{
		return path_.compareTo(other.path_);
	}


	/**
	 * @return a hash code for this path.
	 * @see Path#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return path_.hashCode();
	}


	/**
	 * @return equality with the given object.
	 * @see Path#equals(Object)
	 */
	@Override
	public boolean equals(Object other)
	{
		return other instanceof FilePath && (((FilePath)other).path_).equals(path_);
	}


	/**
	 * @return a {@code FilePath} object representing the absolute path of this path.
	 * @see Path#toAbsolutePath()
	 */
	public FilePath toAbsolutePath()
	{
		return thisOrNew(path_.toAbsolutePath());
	}


	/**
	 * @return the <em>real</em> path of an existing file.
	 * @param options indicating how symbolic links are handled
	 * @see Path#toRealPath(LinkOption...)
	 * @throws IOException if an I/O error occurs
	 */
	public FilePath toRealPath(LinkOption... options) throws IOException
	{
		return thisOrNew(path_.toRealPath(options));
	}


	/**
	 * @return the Path object used by this FilePath.
	 */
	public Path toNioPath()
	{
		return path_;
	}


	/**
	 * @return a URI representing this path.
	 * @see Path#toUri()
	 */
	public URI toUri()
	{
		return path_.toUri();
	}


	/**
	 * @return a closeable FilePath which deletes the underlying path when closed.
	 */
	public FilePath.Closeable toCloseable()
	{
		return new Closeable(path_);
	}


	/**
	 * A FilePath class that implements Closeable.
	 * When closed, it deletes the underlying file or recursively deletes the underlying directory.
	 * @see FilePath#toCloseable()
	 */
	public static class Closeable extends FilePath implements java.io.Closeable
	{
		public Closeable(Path path)
		{
			super(path);
		}


		@Override
		public void close() throws IOException
		{
			if (isDirectory())
				deleteRecursively();
			else
				deleteIfExists();
		}
	}



	/**
	 * @return a File representing this path.
	 * @see Path#toFile()
	 */
	public File toFile()
	{
		return path_.toFile();
	}


	// TODO toSource(options)
	// TODO toTarget(options)


	/**
	 * @return a String to represent this path.
	 * @see Path#toString()
	 */
	@Override
	public String toString()
	{
		return path_.toString();
	}


	/**
	 * A builder class to select a target for a FilePath operation.
	 */
	protected abstract class FilePathTarget implements FileTarget
	{
		@Override
		public FilePath toSibling(String name) throws IOException
		{
			Check.notNull(name, "name");
			return to(FilePath.this.resolveSibling(name));
		}


		// TODO
		@Override
		public FilePath to(FilePath path) throws IOException
		{
			Check.notNull(path, "path");
			execute(path);
			return path;
		}


		/**
		 * Executes the operation of this target.
		 * @param path a path
		 * @throws IOException if an I/O error occurs
		 */
		protected abstract void execute(FilePath path) throws IOException;
	}
}
