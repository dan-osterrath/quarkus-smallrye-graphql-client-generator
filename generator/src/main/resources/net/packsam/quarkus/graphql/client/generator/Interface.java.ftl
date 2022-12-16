<#-- @ftlvariable name="typeName" type="java.lang.String" -->
<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="generatorName" type="java.lang.String" -->
<#-- @ftlvariable name="generationDate" type="java.time.ZonedDateTime" -->
<#-- @ftlvariable name="typeDefinition" type="graphql.language.InterfaceTypeDefinition" -->
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
public interface ${typeName} {
<#list typeDefinition.fieldDefinitions as fieldDefinition>
	/**
	 * Returns the <@description fieldDefinition />.
	 */
	<@javaType fieldDefinition.type /> get${fieldDefinition.name?cap_first}();

	/**
	 * Sets the <@description fieldDefinition />.
	 */
	void set${fieldDefinition.name?cap_first}(<@javaType fieldDefinition.type /> ${fieldDefinition.name});

</#list>
}
