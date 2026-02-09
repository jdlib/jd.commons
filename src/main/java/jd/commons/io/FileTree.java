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


import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jd.commons.check.Check;
import jd.commons.util.UncheckedException;


/**
 * FileTree represents the tree of files defined by a root file
 * and all of its descendant files.
 * <p>
 * It cane be created from a {@link #of(FilePath) FilePath}, {@link #of(Path) Path} or {@link #of(File) File}.
 * <p>
 * A FileTree can be restricted to
 * <ul>
 * <li>{@link #setIncludeRoot(boolean) include} or {@link #setExcludeRoot() exclude} the root
 * <li>only consist of the paths which match a {@link FileTree#addFilter(BiPredicate) filter}
 * <li>include traversal {@link #setOptions(FileVisitOption...) FileVisitOption options}, i.e. if to follow
 * 		symbolic links (by default links are not followed).
 * <li>only include descendants up to a certain {@link #setMaxDepth(int) directory depth}
 * </ul>
 * <p>
 * FileTree allows to visit or stream the FilePaths contained in the tree,
 * or apply bulk operations like delete, copy to the members of the tree.
 */
public class FileTree implements Cloneable
{
	private final FilePath root_;
	private int maxDepth_ = Integer.MAX_VALUE;
	private boolean includeRoot_ = true;
	private BiPredicate<FilePath,BasicFileAttributes> filter_;
	private Set<FileVisitOption> options_ = Set.of();


	/**
	 * @return the FileTree based on the given root.
	 * @param root a File, not null
	 */
	public static FileTree of(File root)
	{
		return of(FilePath.of(root));
	}


	/**
	 * @return the FileTree based on the given root.
	 * @param root a Path, not null
	 */
	public static FileTree of(Path root)
	{
		return of(FilePath.of(root));
	}


	/**
	 * @return the FileTree based on the given root.
	 * @param root a FilePath, not null
	 */
	public static FileTree of(FilePath root)
	{
		return new FileTree(root);
	}


	/**
	 * Creates a new FileTree.
	 * @param root the root, not null
	 */
	protected FileTree(FilePath root)
	{
		root_ = Check.notNull(root, "root");
	}


	//------------------------
	// accessors
	//------------------------


	/**
	 * @return the root of this tree.
	 */
	public FilePath getRoot()
	{
		return root_;
	}


	/**
	 * Max depth is the maximum number of levels of directories to visit in the tree.
	 * A value of {@code 0} means that only the starting root is included. By default the value
	 * is {@link Integer#MAX_VALUE MAX_VALUE} meaning that all levels should be visited.
	 * @return the max depth
	 * @see #setMaxDepth(int)
	 */
	public int getMaxDepth()
	{
		return maxDepth_;
	}


	/**
	 * Sets the maximum depth of directories to traverse.
	 * @param value a depth value, must be &gt;= 0
	 * @return this
	 * @see #getMaxDepth()
	 */
	public FileTree setMaxDepth(int value)
	{
		Check.value(value, "maxDepth").greaterEq(0);
		maxDepth_ = value;
		return this;
	}


	/**
	 * Returns the FileVisitOptions of this tree.
	 * By default they are empty.
	 * The options control how the tree is traversed:
	 * If the options do not contain the {@link FileVisitOption#FOLLOW_LINKS FOLLOW_LINKS} option
	 * then symbolic links are not followed.
	 * @return the options
	 * @see #setOptions(FileVisitOption...)
	 */
	public Set<FileVisitOption> getOptions()
	{
		return options_;
	}


	/**
	 * Sets the FileVisitOptions of this tree.
	 * @param options the options, not null
	 * @return this
	 * @see #getOptions()
	 */
	public FileTree setOptions(FileVisitOption... options)
	{
		Set<FileVisitOption> set = Set.of(Check.notNull(options, "options"));
		return setOptions(set);
	}


	/**
	 * Sets the FileVisitOptions of this tree.
	 * @param options the options, not null
	 * @return this
	 * @see #getOptions()
	 */
	public FileTree setOptions(Set<FileVisitOption> options)
	{
		options_ = Check.notNull(options, "options");
		return this;
	}


	/**
	 * Sets the options of this FileTree to {@link FileVisitOption#FOLLOW_LINKS}.
	 * @return this
	 * @see #setOptions(FileVisitOption...)
	 */
	public FileTree setFollowLinks()
	{
		return setOptions(FileVisitOption.FOLLOW_LINKS);
	}


	/**
	 * @return if this tree reports the root when {@link #accept(FileVisitor) visited} or {@link #stream() streamed}.
	 */
	public boolean getIncludeRoot()
	{
		return includeRoot_;
	}


	/**
	 * Sets that the root is not reported when {@link #accept(FileVisitor) visited} or {@link #stream() streamed}.
	 * @return this
	 */
	public FileTree setExcludeRoot()
	{
		return setIncludeRoot(false);
	}


	/**
	 * Sets that the root is reported when {@link #accept(FileVisitor) visited} or {@link #stream() streamed}.
	 * @param value true if it should be included
	 * @return this
	 */
	public FileTree setIncludeRoot(boolean value)
	{
		includeRoot_ = value;
		return this;
	}


	/**
	 * @return the filter set on this FileTree or null if no filter is set.
	 */
	public BiPredicate<FilePath,BasicFileAttributes> getFilter()
	{
		return filter_;
	}


	/**
	 * Adds a new filter for regular files to this FileTree.
	 * The filter is only applied  to regular files, all other paths, especially directories are not filtered.
	 * @param newFilter a filter, not null
	 * @return this
	 * @see #addFilter(BiPredicate)
	 * @see #getFilter()
	 */
	public FileTree addFileFilter(BiPredicate<FilePath,BasicFileAttributes> newFilter)
	{
		Check.notNull(newFilter, "filter");
		return addFilter((fp,attrs) -> !attrs.isRegularFile() || newFilter.test(fp,attrs));
	}


	/**
	 * Adds a new filter for directories to this FileTree. The filter is only applied
	 * to directories, all other paths are not filtered.
	 * Note that if a directory does not match the filter, its subtree is still processed
	 * (i.e. forwarded to a visitor or contained in the result stream).
	 * To skip a whole subtree you need to use a {@link #accept(FileVisitor) visitor} instead
	 * and implement {@link FileVisitor#preVisitDirectory(Object, BasicFileAttributes)} accordingly.
	 * @param newFilter a filter, not null
	 * @return this
	 * @see #addFilter(BiPredicate)
	 * @see #getFilter()
	 */
	public FileTree addDirFilter(BiPredicate<FilePath,BasicFileAttributes> newFilter)
	{
		Check.notNull(newFilter, "filter");
		return addFilter((fp,attrs) -> !attrs.isDirectory() || newFilter.test(fp,attrs));
	}


	/**
	 * Adds a new filter to the existing filter if this tree.
	 * Note that if a directory does not match the filter, its subtree is still processed
	 * (i.e. forwarded to a visitor or contained in the result stream).
	 * To skip a whole subtree you need to use a {@link #accept(FileVisitor) visitor} instead
	 * and implement {@link FileVisitor#preVisitDirectory(Object, BasicFileAttributes)} accordingly.
	 * @param newFilter a filter, not null
	 * @return this
	 * @see #getFilter()
	 */
	public FileTree addFilter(BiPredicate<FilePath,BasicFileAttributes> newFilter)
	{
		Check.notNull(newFilter, "newFilter");
		filter_  = filter_ != null ? filter_.and(newFilter) : newFilter;
		return this;
	}


	/**
	 * Clears the filter of this tree.
	 * @return this
	 */
	public FileTree clearFilter()
	{
		filter_ = null;
		return this;
	}


	//------------------------
	// Operations
	//------------------------


	/**
	 * Recursively walks this file tree and invokes the visitor for each path encountered.
	 * Tree traversal is depth-first, using the {@link #getOptions() options},
	 * {@link #getMaxDepth() max depth}, {@link #getIncludeRoot() root inclusion} and
	 * {@link #getFilter() filter settings} of this tree to enumerate its members.
	 * @param visitor a visitor, not null
	 * @throws IOException if an I/O error occurs
	 */
	public void accept(FileVisitor<FilePath> visitor) throws IOException
	{
		Check.notNull(visitor, "visitor");
		if (!includeRoot_)
			visitor = new SkipRootProxy(visitor);
		if (filter_ != null)
			visitor = new FilterProxy(visitor);
		Files.walkFileTree(root_.toNioPath(), options_, maxDepth_, new VisitorAdapter(visitor));
	}


    protected abstract class FileTreeTarget implements FileTarget
	{
		@Override
		public FilePath toSibling(String name) throws IOException
		{
			Check.notNull(name, "name");
			return to(FileTree.this.root_.resolveSibling(name));
		}


		@Override
		public FilePath to(FilePath path) throws IOException
		{
			Check.notNull(path, "path");
			execute(path);
			return path;
		}


		protected abstract void execute(FilePath path) throws IOException;
	}


	public FileTarget copy(CopyOption... options)
	{
		return new FileTreeTarget()
		{
			@Override
			protected void execute(FilePath target) throws IOException
			{
				Check.notNull(target, "target");
				if (!target.exists())
					target.createDirectories();
				else
					Check.path(target, "target").isDir();
				FilePath relativeToCandidate = includeRoot_ ? root_.getParent() : root_;
				FilePath relativeTo  = relativeToCandidate != null ? relativeToCandidate : root_;
				accept(new FileVisitor<FilePath>() {
					@Override
					public FileVisitResult preVisitDirectory(FilePath dir, BasicFileAttributes attrs) throws IOException
					{
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(FilePath file, BasicFileAttributes attrs) throws IOException
					{
						FilePath rel = relativeTo.relativize(file);
						FilePath targetFile = target.resolve(rel);
						targetFile.getParent().createDirectories();
						file.copy(options).to(targetFile);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(FilePath file, IOException e) throws IOException
					{
						throw e;
					}

					@Override
					public FileVisitResult postVisitDirectory(FilePath dir, IOException e) throws IOException
					{
						if (e != null)
							throw e;
						return FileVisitResult.CONTINUE;
					}
				});
			}
		};
	}


	/**
	 * Recursively walks this file tree and deletes the files included in this tree.
	 * @return the number of deleted files and directories
	 * @throws IOException if an I/O error occurs
	 */
	public int delete() throws IOException
	{
		if (!root_.existsNoFollowLinks())
			return 0;

		DeleteVisitor visitor = new DeleteVisitor();
		accept(visitor);
		return visitor.count_;
	}


	private class DeleteVisitor implements FileVisitor<FilePath>
	{
		private int count_;


		@Override
		public FileVisitResult preVisitDirectory(FilePath dir, BasicFileAttributes attrs) throws IOException
		{
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(FilePath file, BasicFileAttributes attrs) throws IOException
		{
			if (file.deleteIfExists())
				count_++;
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(FilePath file, IOException e) throws IOException
		{
			throw e;
		}

		@Override
		public FileVisitResult postVisitDirectory(FilePath dir, IOException e) throws IOException
		{
			if (e != null)
				throw e;

			if (filter_ == null || dir.children().isEmpty())
			{
				if (dir.deleteIfExists())
					count_++;
			}
			return FileVisitResult.CONTINUE;
		}
	}


	/**
	 * Returns a Stream, lazily populated with the members of this tree.
	 * Tree traversal is depth-first, using the {@link #getOptions() options},
	 * {@link #getMaxDepth() max depth}, {@link #getIncludeRoot() root inclusion} and
	 * {@link #getFilter() filter settings} of this tree to enumerate its members..
	 * @throws IOException if an I/O error occurs
	 * @return the stream
	 */
	public Stream<FilePath> stream() throws IOException
	{
		Stream<FilePath> stream = Files.walk(root_.toNioPath(), maxDepth_, options_.toArray(FileVisitOption[]::new))
			.map(FilePath::of);
		if (!includeRoot_)
			stream = stream.filter(fp -> !root_.equals(fp));
		if (filter_ != null)
			stream = stream.filter(buildStreamFilter());
		return stream;
	}


	private Predicate<FilePath> buildStreamFilter()
	{
		boolean followLinks = options_.contains(FileVisitOption.FOLLOW_LINKS);
		return followLinks ?
			path -> filter_.test(path, readAttributesFollowLinks(path)) :
			path -> filter_.test(path, readAttributesNoFollowLinks(path));
	}


	private static BasicFileAttributes readAttributesFollowLinks(FilePath path)
	{
	    try
	    {
	        return Files.readAttributes(path.toNioPath(), BasicFileAttributes.class);
	    }
	    catch (IOException e)
	    {
	    	// fallback
	    	return readAttributesNoFollowLinks(path);
	    }
	}


	private static BasicFileAttributes readAttributesNoFollowLinks(FilePath path)
	{
	    try
	    {
	        return Files.readAttributes(path.toNioPath(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
	    }
	    catch (IOException e)
	    {
	    	throw UncheckedException.create(e);
	    }
	}


	/**
	 * @return the members of this tree as list.
	 * @throws IOException if an I/O error occurs
	 */
	public List<FilePath> toList() throws IOException
	{
		return stream().collect(Collectors.toList());
	}


	// a file visitor for java.nio.file.Path translating Paths to FilePath
	private static class VisitorAdapter implements FileVisitor<Path>
	{
		private final FileVisitor<FilePath> inner_;


		public VisitorAdapter(FileVisitor<FilePath> inner)
		{
			inner_ = inner;
		}


		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
		{
			return inner_.preVisitDirectory(FilePath.of(dir), attrs);
		}


		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
		{
			return inner_.visitFile(FilePath.of(file), attrs);
		}


		@Override
		public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException
		{
			return inner_.visitFileFailed(FilePath.of(file), e);
		}


		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException
		{
			return inner_.postVisitDirectory(FilePath.of(dir), e);
		}
	}


	// a FileVisitor which continues a path if it equals the root
	class SkipRootProxy implements FileVisitor<FilePath>
	{
		private final FileVisitor<FilePath> inner_;


		public SkipRootProxy(FileVisitor<FilePath> inner)
		{
			inner_ = inner;
		}


		private boolean isRoot(FilePath fp)
		{
			return root_.equals(fp);
		}


		@Override
		public FileVisitResult preVisitDirectory(FilePath dir, BasicFileAttributes attrs) throws IOException
		{
			return isRoot(dir) ? FileVisitResult.CONTINUE : inner_.preVisitDirectory(dir, attrs);
		}


		@Override
		public FileVisitResult visitFile(FilePath file, BasicFileAttributes attrs) throws IOException
		{
			return isRoot(file) ? FileVisitResult.TERMINATE : inner_.visitFile(file, attrs);
		}


		@Override
		public FileVisitResult visitFileFailed(FilePath file, IOException e) throws IOException
		{
			if (isRoot(file))
				throw e;
			return inner_.visitFileFailed(file, e);
		}


		@Override
		public FileVisitResult postVisitDirectory(FilePath dir, IOException e) throws IOException
		{
			return isRoot(dir) ? FileVisitResult.CONTINUE : inner_.postVisitDirectory(dir, e);
		}
	}


	// a FileVisitor which forwards to another visitor for all paths which match a filter
	private class FilterProxy implements FileVisitor<FilePath>
	{
		private final FileVisitor<FilePath> inner_;


		public FilterProxy(FileVisitor<FilePath> inner)
		{
			inner_ = inner;
		}


		private boolean filter(FilePath fp,  BasicFileAttributes attrs) throws IOException
		{
			return filter_.test(fp, attrs);
		}


		@Override
		public FileVisitResult preVisitDirectory(FilePath dir, BasicFileAttributes attrs) throws IOException
		{
			return filter(dir, attrs) ? inner_.preVisitDirectory(dir, attrs) : FileVisitResult.CONTINUE;
		}


		@Override
		public FileVisitResult visitFile(FilePath file, BasicFileAttributes attrs) throws IOException
		{
			return filter(file, attrs) ? inner_.visitFile(file, attrs) : FileVisitResult.CONTINUE;
		}


		@Override
		public FileVisitResult visitFileFailed(FilePath file, IOException e) throws IOException
		{
			return inner_.visitFileFailed(file, e);
		}


		@Override
		public FileVisitResult postVisitDirectory(FilePath dir, IOException e) throws IOException
		{
			return inner_.postVisitDirectory(dir, e);
		}
	}


	/**
	 * @return a clone of this FileTree.
	 */
	@Override
	public FileTree clone()
	{
		try
		{
			return (FileTree)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError(e);
		}
	}


	/**
	 * @return a string representation of this FileTree.
	 */
	@Override
	public String toString()
	{
		return "Tree[" + root_ + ']';
	}
}
