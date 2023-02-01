package net.packsam.quarkus.graphql.client.generator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying the URI of the GraphQL schema.
 * <br>
 * Examples:
 * <pre>{@code @GraphQLSchema("resource:schema.graphql") }</pre>
 * <pre>{@code @GraphQLSchema("file:src/main/resources/schema.graphql") }</pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface GraphQLSchema {
	/**
	 * Schema location. Has to be prefixed with a valid protocol like <code>file:</code>, <code>https:</code> or <code>resource:</code>.
	 *
	 * @return schema location
	 */
	String value();

	/**
	 * Target package for the generated classes.
	 *
	 * @return package name
	 */
	String targetPackage() default "";

	/**
	 * Target package for the generated model classes.
	 *
	 * @return package name
	 */
	String targetModelPackage() default "";
}
