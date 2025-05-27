# jd.commons.io.FileTree

`FileTree` represents a root file and all its descendants, constructed from a `java.io.File`, `java.nio.file.Path` or `jd.commons.io.FilePath`.

If the root does not exist the FileTree is empty. If the root is a regular file, the tree consists of just that file. If the root is a directory the tree consists of the directory and all its descendants.

`FileTree` allows to restrict operations on the tree to
- exclude the root
- apply to descendants matching a filter
- apply to descendants up to a certain depth
- follow symbolic links

Given a `FileTree` you can 
- stream the matched members
- delete all matched members
- copy all matched members to a target destination
- visit the tree members, allowing for control if to dive into subtrees or apply changes to visited tree members


