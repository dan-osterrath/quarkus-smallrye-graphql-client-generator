package net.packsam.quarkus.graphql.client.generator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying a GraphQL query. This query is parsed, validated against the GraphQL schema and used to
 * generate a {@code io.smallrye.graphql.client.core.Document} instance. This can be used in combination with a
 * {@code io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient} to execute the query in a typesafe way.
 * <br>
 * You have to add this annotation to the interface where the @{@link GraphQLSchema} annotation is present.
 * <br>
 * Examples:
 * <pre>{@code @GraphQLSchema("resource:schema.graphql") }
 * {@code @GraphQLQuery("{ allUsers(filter: { maxResults: 10}) { id name } }") }
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Repeatable(GraphQLQueries.class)
@Documented
public @interface GraphQLQuery {
	/**
	 * Query identifier. Has to be unique within the interface.
	 *
	 * @return query identifier
	 */
	String identifier();

	/**
	 * GraphQL query.
	 *
	 * @return GraphQL query
	 */
	String value();
}
