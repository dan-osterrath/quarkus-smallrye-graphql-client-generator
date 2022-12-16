<#macro description node><#-- @ftlvariable name="node" type="graphql.language.AbstractDescribedNode" -->
<#compress>
	<#if node.description?? && node.description.content??>
${node.description.content}
	</#if>
</#compress>
</#macro>

<#macro javadoc node><#-- @ftlvariable name="node" type="graphql.language.FieldDefinition" -->
	/**
	 * <@description node />.
    <#list node.inputValueDefinitions as arg>
     * @param ${arg.name} <@description arg />
    </#list>
	*/
</#macro>
