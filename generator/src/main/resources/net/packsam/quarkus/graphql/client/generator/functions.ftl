<#function toUpperSnakeCase s><#-- @ftlvariable name="subscription" type="java.lang.String" -->
    <#return s?replace("([a-z])([A-Z])", "$1_$2", "r")?upper_case>
</#function>
