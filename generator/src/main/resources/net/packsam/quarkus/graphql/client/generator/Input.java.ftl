<#-- @ftlvariable name="typeName" type="java.lang.String" -->
<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="generatorName" type="java.lang.String" -->
<#-- @ftlvariable name="generationDate" type="java.time.ZonedDateTime" -->
<#-- @ftlvariable name="typeDefinition" type="graphql.language.InputObjectTypeDefinition" -->
<#include "functions.ftl">
<#include "comments.ftl">
<#include "types.ftl">
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
public class ${typeName} {
<#list typeDefinition.inputValueDefinitions as inputValueDefinition>
	/**
	 * Field name for "<@description inputValueDefinition />".
	 */
	public final static String FIELD_${toUpperSnakeCase(inputValueDefinition.name)} = "${inputValueDefinition.name}";

</#list>
<#list typeDefinition.inputValueDefinitions as inputValueDefinition>
	/**
	 * <@description inputValueDefinition />.
	 */
	private <@javaTypeWithNonNull inputValueDefinition.type /> ${inputValueDefinition.name};

</#list>
<#list typeDefinition.inputValueDefinitions as inputValueDefinition>
	/**
	 * Returns the <@description inputValueDefinition />.
	 */
	public <@javaTypeWithNonNull inputValueDefinition.type /> get${inputValueDefinition.name?cap_first}() {
		return ${inputValueDefinition.name};
	}

	/**
	 * Sets the <@description inputValueDefinition />.
	 */
	public void set${inputValueDefinition.name?cap_first}(<@javaTypeWithNonNull inputValueDefinition.type /> ${inputValueDefinition.name}) {
		this.${inputValueDefinition.name} = ${inputValueDefinition.name};
	}

</#list>
}
