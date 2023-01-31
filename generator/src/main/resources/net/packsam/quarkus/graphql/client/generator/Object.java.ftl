<#-- @ftlvariable name="typeName" type="java.lang.String" -->
<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="generatorName" type="java.lang.String" -->
<#-- @ftlvariable name="generationDate" type="java.time.ZonedDateTime" -->
<#-- @ftlvariable name="typeDefinition" type="graphql.language.ObjectTypeDefinition" -->
<#include "functions.ftl">
<#include "comments.ftl">
<#include "types.ftl">
package ${packageName};

import java.util.List;

import javax.annotation.processing.Generated;

/**
 * <@description typeDefinition />.
 */
@Generated(
	value="${generatorName}",
	date="${generationDate}"
)
public class ${typeName} <#if typeDefinition.implements?? && typeDefinition.implements?has_content>implements <#list typeDefinition.implements as iface>${iface.name}<#if iface_has_next>, </#if></#list></#if> {
<#list typeDefinition.fieldDefinitions as fieldDefinition>
	/**
	 * Field name for "<@description fieldDefinition />".
	 */
	public final static String FIELD_${toUpperSnakeCase(fieldDefinition.name)} = "${fieldDefinition.name}";

</#list>
<#list typeDefinition.fieldDefinitions as fieldDefinition>
	/**
	 * <@description fieldDefinition />.
	 */
	private <@javaType fieldDefinition.type /> ${fieldDefinition.name};

</#list>
<#list typeDefinition.fieldDefinitions as fieldDefinition>
	/**
	 * Returns the <@description fieldDefinition />.
	 */
	public <@javaType fieldDefinition.type /> get${fieldDefinition.name?cap_first}() {
		return ${fieldDefinition.name};
	}

	/**
	 * Sets the <@description fieldDefinition />.
	 */
	public void set${fieldDefinition.name?cap_first}(<@javaType fieldDefinition.type /> ${fieldDefinition.name}) {
		this.${fieldDefinition.name} = ${fieldDefinition.name};
	}

</#list>
}
