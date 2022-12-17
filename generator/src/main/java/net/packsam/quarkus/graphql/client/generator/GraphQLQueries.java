package net.packsam.quarkus.graphql.client.generator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for {@link GraphQLQuery} annotations.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface GraphQLQueries {
	/**
	 * GraphQL queries.
	 *
	 * @return GraphQL queries
	 */
	GraphQLQuery[] value();
}
