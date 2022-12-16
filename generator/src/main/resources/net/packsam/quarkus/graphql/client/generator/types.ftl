<#macro javaType t><#-- @ftlvariable name="node" type="graphql.language.Type" -->
<#compress>
	<#assign className=t.class.name>
	<#if className == "graphql.language.NonNullType">
<@javaType t.type />
	<#elseif className == "graphql.language.ListType">
List${"<"}<@javaType t.type />${">"}
    <#elseif className == "graphql.language.TypeName">
		<#if t.name == "ID">
String
		<#elseif t.name == "Int">
Integer
		<#else>
${t.name}
		</#if>
	</#if>
</#compress>
</#macro>

<#macro javaTypeWithNonNull t><#-- @ftlvariable name="node" type="graphql.language.Type" -->
<#compress>
	<#assign className=t.class.name>
	<#if className == "graphql.language.NonNullType">
@NonNull <@javaTypeWithNonNull t.type />
    <#elseif className == "graphql.language.ListType">
List${"<"}<@javaTypeWithNonNull t.type />${">"}
    <#elseif className == "graphql.language.TypeName">
<@javaType t />
    </#if>
</#compress>
</#macro>

<#macro inputValueList l><#-- @ftlvariable name="node" type="java.util.List<graphql.language.InputValueDefinition>" -->
<#compress>
	<#list l as arg><@javaTypeWithNonNull arg.type /> ${arg.name}<#if arg_has_next>, </#if></#list>
</#compress>
</#macro>
