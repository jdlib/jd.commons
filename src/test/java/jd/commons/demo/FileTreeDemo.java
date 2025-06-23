package jd.commons.demo;


import java.io.IOException;
import jd.commons.io.FilePath;
import jd.commons.io.FileTree;


public class FileTreeDemo
{
	public static void run(FilePath src, FilePath target) throws IOException
	{
		FileTree.of(src).addFileFilter((file,attr) -> file.getName().endsWith(".txt")).copy().to(target);
		FileTree.of(src).setExcludeRoot().delete();
	}
}
