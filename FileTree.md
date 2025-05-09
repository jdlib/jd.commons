 # jd.commons.io.FileTree

`FileTree` represents a root "file" and all its descendants, constructed from a `java.io.File`, `java.nio.file.Path` or `jd.commons.io.FilePath`.

It allows to restrict the set of members of the tree to
- exclude the root
- members matching a filter
- members upto a certain depth
- follow symbolic links

Given a `FileTree` you can
- stream the tree members
- visit the tree members, allowing for control if to dive into subtrees or apply changes to visited tree members
- copy a tree to a target destination
- delete all matched members





If the root does not exist the FileTree is empty. If the root is a regular file, the tree consists of just that file. If the root is a directory the tree consists of the directory and all its descendants.







