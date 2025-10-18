package jd.commons.demo;


import java.io.IOException;
import jd.commons.io.FilePath;
import jd.commons.io.FileTree;


public class FileTreeDemo
{
	public static void run(FilePath src, FilePath target) throws IOException
	{
		FileTree tree = FileTree.of(src);

		tree.addFileFilter((file,attr) -> file.getName().endsWith(".txt")).copy().to(target);
		tree.setExcludeRoot().delete();
	}
}
