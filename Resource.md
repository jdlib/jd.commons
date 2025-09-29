# jd.commons.io.Resource

The `jd.commons.io.Resource` class makes it easy to access classpath resources:
- it allows you to build a resource name from joining parts or deriving a path from package names of objects or classes
- it provides a wide variety of `ResourceLoader` instances which are 
    used to obtain URLs or InputStreams for resources.

Examples:

    import jd.commons.io.Resource;
    import jd.commons.io.ResourceLoader;

    Resource res = Resource.of().pathTo(this).path("test.txt");
    Resource res = Resource.of().path("META-INF", "services.ini").loadBy(ResourceLoader.system());

`Resource` objects can provide a `URL` and `InputStream` to the resource:

    // contrary to Class.getResourceAsStream() this call fails if the resource
    // does not exist, so you can use it in a try-with-resources statement:
    try (InputStream in = res.getInputStream()) {
        ...
    }

A non existing resource will return a null URL. Or simply call the `exists` method
    
    res.getURL() 
    res.exists()

Or make sure that a `Resource` exists: 

    res.checkExists().getURL() // throws an IOException if not existent

Reading resource content is easy since `Resource` itself is a `ByteSource` (see [FluentIO](FluentIO.md)).

    import jd.commons.config.PropsConfig;

    byte[] testdata = Resource.of("testdata.bin").read().all();
    List<String> examples = Resource.of("examples").asUtf8().read().lines().toList();  
    PropsConfig appProps = PropsConfig.read().from(Resource.of(META-INF/app.properties"));