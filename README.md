# Quarkus SmallRye GraphQL Client Generator

[![Build and test with Maven](https://github.com/dan-osterrath/quarkus-smallrye-graphql-client-generator/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/dan-osterrath/quarkus-smallrye-graphql-client-generator/actions/workflows/maven.yml)

This project is a generator
for [SmallRye GraphQL Clients used in Quarkus](https://quarkus.io/guides/smallrye-graphql-client). It provides an
annotation processor that generates an interface for
a [typesafe client](https://quarkus.io/guides/smallrye-graphql-client#using-the-typesafe-client) including all model
objects.

Additionally, it creates constants for names of all query, mutation and subscription operations and all model
fields. This allows you to create use
the [dynamic client](https://quarkus.io/guides/smallrye-graphql-client#using-the-dynamic-client) with those constants.

## Usage

### Add dependency

Add the following repository and dependency to your Maven project:

 ```xml pom.xml

<project>

	<repositories>
		<repository>
			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		</repository>
	</repositories>

	<dependency>
		<groupId>net.packsam</groupId>
		<artifactId>quarkus-smallrye-graphql-client-generator</artifactId>
		<version>0.0.3</version>
	</dependency>

</project>
 ```

See [Jitpack.io project page](https://jitpack.io/#dan-osterrath/quarkus-smallrye-graphql-client-generator/).

### Add GraphQL schema

Put the GraphQL schema into your classpath, i.e. into `src/main/resources/`. Alternatively, you can also fetch the
schema from a remote server, but this is not recommended as this may result in failing compilations when there are
connectivity issues.

### Generate API

Create an interface annotated with `@GraphQLSchema` and provide a URI to the GraphQL schema:

 ```java MyGraphQLService.java
@GraphQLSchema("resource:schema.graphql")
interface MyGraphQLService {
}
 ```

During compilation the annotation processor will parse this schema at compile time and creates a Java interface for the
API and all models in the same package as the annotated interface.

```java MyGraphQLServiceApi.java
@GraphQLClientApi(configKey = "MyGraphQLServiceApi")
@ApplicationScoped
@Generated(
		value = "net.packsam.quarkus.graphql.client.generator.Generator",
		date = "2022-12-16T11:11:27.192760200+01:00[Europe/Berlin]"
)
public interface MyGraphQLServiceApi {
	String QUERY_ALLUSERS = "allUsers";
	String ARGUMENT_ALLUSERS_FILTER = "filter";

	List<User> allUsers(Filter filter);
}
```

```java User.java
@Generated(
		value = "net.packsam.quarkus.graphql.client.generator.Generator",
		date = "2022-12-16T11:11:27.192760200+01:00[Europe/Berlin]"
)
public class User {
	public static final String FIELD_ID = "id";
	public static final String FIELD_NAME = "name";

	private String id;
	private String name;

	// getters and setters...
}
```

```java Filter.java
@Generated(
		value = "net.packsam.quarkus.graphql.client.generator.Generator",
		date = "2022-12-16T11:11:27.192760200+01:00[Europe/Berlin]"
)
public class Filter {
	public static final String FIELD_QUERY = "query";
	public static final String FIELD_MAXRESULTS = "maxResults";

	private String query;
	private Integer maxResults;

	// getters and setters...
}
```

### Using typesafe client

Inject the generated interface into your application and use it as a typesafe client:

```java MyService.java
public class MyService {
	@Inject
	MyGraphQLServiceApi serviceApi;

	public List<User> getAllUsers() {
		return serviceApi.allUsers(null);
	}
}
```

Configure the client in `application.properties`:

```properties
quarkus.smallrye-graphql-client.MyGraphQLServiceApi.url=http://my-service/graphql
```

See the [Quarkus documentation](https://quarkus.io/guides/smallrye-graphql-client#using-the-typesafe-client) for more
details.

### Using dynamic client

Inject a `DynamicGraphQLClient` into your application and annotate it with `@GraphQLClientApi`:

```java MyService.java
public class MyService {
	@Inject
	@GraphQLClient("DynamicMyGraphQLServiceApi")
	DynamicGraphQLClient client;

	public List<User> getAllUsers() {
		Response = client.executeSync(doucment(
				operation(OperationType.Query,
						field(MyGraphQLServiceApi.QUERY_ALLUSERS,
								field(User.FIELD_ID),
								field(User.FIELD_NAME)
						)
				)
		));

		return response.getList(User.class, MyGraphQLServiceApi.QUERY_ALLUSERS);
	}
}
```

Configure the client in `application.properties`:

```properties
quarkus.smallrye-graphql-client.DynamicMyGraphQLServiceApi.url=http://my-service/graphql
```

See the [Quarkus documentation](https://quarkus.io/guides/smallrye-graphql-client#using-the-dynamic-client) for more
details.

## Credits

This project is inspired by the
experimental [SmallRye GraphQL client generator ](https://github.com/smallrye/smallrye-graphql/tree/main/client/generator).
