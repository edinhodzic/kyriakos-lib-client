# About

A http client abstraction library based on [Akka HTTP](http://doc.akka.io/docs/akka-stream-and-http-experimental/current/scala/http/).

# What's under the hood?

Implementation:

- [Scala](http://www.scala-lang.org/)
- [Akka HTTP](http://doc.akka.io/docs/akka-stream-and-http-experimental/current/scala/http/)

Testing:

- [Specs2](https://etorreborre.github.io/specs2/)

## Usage

First implement a client:

```scala
class CustomerHttpCrudClient
(serviceHost: String)
(implicit actorSystem: ActorSystem, executionContext: ExecutionContext, manifest: Manifest[Customer])
  extends AbstractHttpCrudClient[Customer](serviceHost)
```

Given the above and say a `val resourceId: String = "569639c2d4c6508b2f6838eb"` then the below describes how to perform CRUD operations using the client implementation.

### Create a resource
```scala
client create Customer("edin") map { resource =>
  println(s"created [$resource]")
}
```
    
### Read a resource    
```scala
client read resourceId map {
  case Some(resource) => println(s"read [$resource]")
  case None => println(s"failed to read resource with id [$resourceId]")
}
```

### Update a resource
    
    TODO document this

### Delete a resource 
```scala
client delete resourceId map {
  case Some(_) => println(s"deleted resource with id [$resourceId]")
  case None => println(s"failed to delete resource with id [$resourceId] - not found")
}
```

### Query a resource
    
    TODO document this

## Incomplete features

- [ ] update and query operations

## Future development ideas

- wrap client with a circuit breaker