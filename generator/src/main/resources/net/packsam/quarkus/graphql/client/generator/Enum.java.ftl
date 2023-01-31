<#-- @ftlvariable name="typeName" type="java.lang.String" -->
<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="generatorName" type="java.lang.String" -->
<#-- @ftlvariable name="generationDate" type="java.time.ZonedDateTime" -->
<#-- @ftlvariable name="typeDefinition" type="graphql.language.EnumTypeDefinition" -->
<#include "comments.ftl">
package ${packageName};

import org.eclipse.microprofile.graphql.NonNull;

import java.util.List;

import javax.annotation.processing.Generated;

/**
 * <@description typeDefinition />.
 */
@Generated(
	value="${generatorName}",
	date="${generationDate}"
)
public enum ${typeName} {
<#list typeDefinition.enumValueDefinitions as valueDefinition>
	/**
	 * <@description valueDefinition />
	 */
	${valueDefinition.name}<#if valueDefinition_has_next>,</#if>
</#list>
}
