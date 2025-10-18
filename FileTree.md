# jd.commons.io.FileTree

`FileTree` represents a root file and all its descendants, constructed from a `java.io.File`, `java.nio.file.Path` or `jd.commons.io.FilePath`.

If the root does not exist then the FileTree is empty. If the root is a regular file, the tree consists of just that file. If the root is a directory the tree consists of the directory and all its descendants.

Given a `FileTree` you can 
- stream the tree elements
- delete all tree elements
- copy all tree elements to a target destination
- visit the tree members, allowing for control if to dive into subtrees or apply changes to visited tree members

	FilePath srcDir = ...
	FilePath targetDir = ...
	FileTree srcTree = FileTree.of(srcDir);
	srcTree.copy().to(targetDir);
	srcTree.delete();
	
`FileTree` allows to restrict operations on the tree to
- exclude the root
- apply to descendants matching a filter
- apply to descendants up to a certain depth
- follow symbolic links

    srcTree.addFileFilter((file,attr) -> file.getName().endsWith(".txt")).copy().to(targetDir);
    srcTree.setExcludeRoot().delete();






