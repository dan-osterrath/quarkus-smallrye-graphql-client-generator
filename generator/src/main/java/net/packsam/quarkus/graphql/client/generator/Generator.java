package net.packsam.quarkus.graphql.client.generator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import graphql.language.EnumTypeDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.OperationTypeDefinition;
import graphql.language.TypeDefinition;
import graphql.parser.Parser;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class Generator {
	private final String schemaString;

	private final String serviceName;

	private final String servicePackageName;
	private final String modelPackageName;

	private final Map<String, String> queries;

	public Map<String, String> generateJavaSources() {
		var schema = parseSchema(schemaString);
		var parsedQueries = parseQueries(this.queries);
		var templates = readTemplates();

		var queryTypeNameRef = new AtomicReference<>("Query");
		var mutationTypeNameRef = new AtomicReference<>("Mutation");
		var subscriptionTypeNameRef = new AtomicReference<>("Subscription");
		schema.schemaDefinition().ifPresent(schemaDefinition -> {
			for (OperationTypeDefinition operationTypeDefinition : schemaDefinition.getOperationTypeDefinitions()) {
				switch (operationTypeDefinition.getName()) {
					case "query":
						queryTypeNameRef.set(operationTypeDefinition.getTypeName().getName());
						break;
					case "mutation":
						mutationTypeNameRef.set(operationTypeDefinition.getTypeName().getName());
						break;
					case "subscription":
						subscriptionTypeNameRef.set(operationTypeDefinition.getTypeName().getName());
						break;
				}
			}
		});
		var queryTypeName = queryTypeNameRef.get();
		var mutationTypeName = mutationTypeNameRef.get();
		var subscriptionTypeName = subscriptionTypeNameRef.get();

		var generatedFiles = new LinkedHashMap<String, String>();

		generatedFiles.put(
				servicePackageName + "." + serviceName,
				generateService(
						templates.getServiceTemplate(),
						schema,
						serviceName,
						servicePackageName,
						modelPackageName,
						queryTypeName,
						mutationTypeName,
						subscriptionTypeName,
						parsedQueries
				)
		);

		for (var typeDefinition : schema.getTypes(InputObjectTypeDefinition.class)) {
			var typeName = typeDefinition.getName();
			generatedFiles.put(
					modelPackageName + "." + typeName,
					generateType(templates.getInputTemplate(), typeDefinition, typeName, modelPackageName)
			);
		}

		for (var typeDefinition : schema.getTypes(ObjectTypeDefinition.class)) {
			var typeName = typeDefinition.getName();
			if (typeName.equals(queryTypeName) || typeName.equals(mutationTypeName) || typeName.equals(subscriptionTypeName)) {
				continue;
			}
			generatedFiles.put(
					modelPackageName + "." + typeName,
					generateType(templates.getObjectTemplate(), typeDefinition, typeName, modelPackageName)
			);
		}

		for (var typeDefinition : schema.getTypes(InterfaceTypeDefinition.class)) {
			var typeName = typeDefinition.getName();
			generatedFiles.put(
					modelPackageName + "." + typeName,
					generateType(templates.getInterfaceTemplate(), typeDefinition, typeName, modelPackageName)
			);
		}

		for (var typeDefinition : schema.getTypes(EnumTypeDefinition.class)) {
			var typeName = typeDefinition.getName();
			generatedFiles.put(
					modelPackageName + "." + typeName,
					generateType(templates.getEnumTemplate(), typeDefinition, typeName, modelPackageName)
			);
		}

		return generatedFiles;
	}

	private List<ParsedQuery> parseQueries(Map<String, String> queries) {
		return queries.entrySet()
				.stream()
				.map(e -> parseQuery(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

	private ParsedQuery parseQuery(String identifier, String query) {
		try {
			Parser.parse(query);
			return new ParsedQuery(identifier, query);
		} catch (Exception e) {
			throw new GeneratorException("Could not parse GraphQL query \"" + query + "\": " + e.getMessage(), e);
		}
	}

	private String generateService(
			Template template,
			TypeDefinitionRegistry schema,
			String serviceName,
			String packageName,
			String modelPackageName,
			String queryTypeName,
			String mutationTypeName,
			String subscriptionTypeName,
			List<ParsedQuery> parsedQueries
	) {
		return generateSourceFile(
				template,
				Map.of(
						"schema", schema,
						"serviceName", serviceName,
						"packageName", packageName,
						"modelPackageName", modelPackageName,
						"queryTypeName", queryTypeName,
						"mutationTypeName", mutationTypeName,
						"subscriptionTypeName", subscriptionTypeName,
						"parsedQueries", parsedQueries,
						"generatorName", getClass().getName(),
						"generationDate", ZonedDateTime.now()
				)
		);
	}

	private String generateType(
			Template template,
			TypeDefinition<?> typeDefinition,
			String typeName,
			String packageName
	) {
		return generateSourceFile(
				template,
				Map.of(
						"typeDefinition", typeDefinition,
						"typeName", typeName,
						"packageName", packageName,
						"generatorName", getClass().getName(),
						"generationDate", ZonedDateTime.now()
				));
	}

	private String generateSourceFile(Template template, Map<String, Object> model) {
		try (var out = new StringWriter()) {
			template.process(model, out);
			return out.toString();
		} catch (Exception e) {
			throw new GeneratorException("Could not generate Java file: " + e.getMessage(), e);
		}
	}

	private TemplateCollection readTemplates() {
		var cfg = new Configuration(Configuration.VERSION_2_3_31);
		cfg.setClassForTemplateLoading(this.getClass(), "");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		cfg.setWrapUncheckedExceptions(true);

		try {
			return new TemplateCollection(
					cfg.getTemplate("Service.java.ftl"),
					cfg.getTemplate("Input.java.ftl"),
					cfg.getTemplate("Object.java.ftl"),
					cfg.getTemplate("Interface.java.ftl"),
					cfg.getTemplate("Enum.java.ftl")
			);
		} catch (Exception e) {
			throw new GeneratorException("Error reading templates: " + e.getMessage(), e);
		}
	}

	private TypeDefinitionRegistry parseSchema(String schemaString) {
		try {
			return new SchemaParser().parse(schemaString);
		} catch (Exception e) {
			throw new GeneratorException("Can not parse GraphQL schema: " + e.getMessage(), e);
		}
	}

	@Value
	public static class ParsedQuery {
		String identifier;
		String query;

		public String getSingleLineQuery() {
			return query.replaceAll("\\s+", " ");
		}
	}

	@Value
	private static class TemplateCollection {
		Template serviceTemplate;
		Template inputTemplate;
		Template objectTemplate;
		Template interfaceTemplate;
		Template enumTemplate;

	}

}

