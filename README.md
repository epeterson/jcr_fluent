#JCR-Fluent

####A fluent way to build XPath Queries for Java Content Repositories

- - - - -
#####Warning: This project is still just a prototype, and not in a usable state.


Instead of building XPath Queries through String concatenation, it can be more pleasant and less error-prone to build them using a fluent API.

As a concrete example, the XPath query:

    "/jcr:root/content/myapp/*[@propertyA='valueA' and (@propertyB='valueB' or @propertyC='42')]"

Could be built using the following API:

    JcrQuery.at("/jcr:root/content/myapp/")
		    .with(property("propertyA").eq("valueA"),
                  any(property("propertyB").eq("valueB"), property("propertyC").eq(42)));




- Java Content Repository: [http://en.wikipedia.org/wiki/Content_repository_API_for_Java](http://en.wikipedia.org/wiki/Content_repository_API_for_Java)
- Fluent Interface: [http://martinfowler.com/bliki/FluentInterface.html](http://martinfowler.com/bliki/FluentInterface.html)
