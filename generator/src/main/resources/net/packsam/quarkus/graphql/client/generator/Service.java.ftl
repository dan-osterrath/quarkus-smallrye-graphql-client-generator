<#-- @ftlvariable name="serviceName" type="java.lang.String" -->
<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="queryTypeName" type="java.lang.String" -->
<#-- @ftlvariable name="mutationTypeName" type="java.lang.String" -->
<#-- @ftlvariable name="subscriptionTypeName" type="java.lang.String" -->
<#-- @ftlvariable name="generatorName" type="java.lang.String" -->
<#-- @ftlvariable name="generationDate" type="java.time.ZonedDateTime" -->
<#-- @ftlvariable name="schema" type="graphql.schema.idl.TypeDefinitionRegistry" -->
<#include "comments.ftl">
<#include "types.ftl">
<#assign queries=schema.getType(queryTypeName)>
<#assign mutations=schema.getType(mutationTypeName)>
<#assign subscriptions=schema.getType(subscriptionTypeName)>
package ${packageName};

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.graphql.NonNull;

import java.util.List;

import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;

@GraphQLClientApi(configKey = "${serviceName}")
@ApplicationScoped
@Generated(
	value="${generatorName}",
	date="${generationDate}"
)
public interface ${serviceName} {
<#if queries.isPresent()><#list queries.get().children as query><#-- @ftlvariable name="query" type="graphql.language.FieldDefinition" -->
	/**
	 * Query name for "<@description query />".
	 */
	String QUERY_${query.name?upper_case} = "${query.name}";

	<#list query.inputValueDefinitions as arg><#-- @ftlvariable name="arg" type="graphql.language.InputValueDefinition" -->
	/**
	  * Argument name for "<@description arg />" of query ${query.name}.
	  */
	String ARGUMENT_${query.name?upper_case}_${arg.name?upper_case} = "${arg.name}";

	</#list>
</#list></#if>
<#if mutations.isPresent()><#list mutations.get().children as mutation><#-- @ftlvariable name="mutation" type="graphql.language.FieldDefinition" -->
	/**
	 * Mutation name for "<@description mutation />".
	 */
	String MUTATION_${mutation.name?upper_case} = "${mutation.name}";

    <#list mutation.inputValueDefinitions as arg><#-- @ftlvariable name="arg" type="graphql.language.InputValueDefinition" -->
	/**
	 * Argument name for "<@description arg />" of mutation ${mutation.name}.
	 */
	String ARGUMENT_${mutation.name?upper_case}_${arg.name?upper_case} = "${arg.name}";

    </#list>
</#list></#if>
<#if subscriptions.isPresent()><#list subscriptions.get().children as subscription><#-- @ftlvariable name="subscription" type="graphql.language.FieldDefinition" -->
	/**
	 * Subscription name for "<@description subscription />".
	 */
	String SUBSCRIPTION_${subscription.name?upper_case} = "${subscription.name}";

    <#list subscription.inputValueDefinitions as arg><#-- @ftlvariable name="arg" type="graphql.language.InputValueDefinition" -->
	/**
	 * Argument name for "<@description arg />" of subscription ${subscription.name}.
	 */
	String ARGUMENT_${subscription.name?upper_case}_${arg.name?upper_case} = "${arg.name}";

    </#list>
</#list></#if>

<#if queries.isPresent()><#list queries.get().children as query><#-- @ftlvariable name="query" type="graphql.language.FieldDefinition" -->
	<@javadoc query />
	<@javaType query.type /> ${query.name}(<@inputValueList query.inputValueDefinitions />);

</#list></#if>
<#if mutations.isPresent()><#list mutations.get().children as mutation><#-- @ftlvariable name="mutation" type="graphql.language.FieldDefinition" -->
	<@javadoc mutation />
	<@javaType mutation.type /> ${mutation.name}(<@inputValueList mutation.inputValueDefinitions />);

</#list></#if>
<#if subscriptions.isPresent()><#list subscriptions.get().children as subscription><#-- @ftlvariable name="subscription" type="graphql.language.FieldDefinition" -->
	<@javadoc subscription />
	Multi${"<"}<@javaType subscription.type />${">"} ${subscription.name}(<@inputValueList subscription.inputValueDefinitions />);

	</#list></#if>
}
