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
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.IOException;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
class Generator {
	private final String schemaString;

	private final String serviceName;

	private final String packageName;

	public Map<String, String> generateJavaSources() throws GeneratorException {
		var schema = parseSchema(schemaString);
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
				packageName + "." + serviceName,
				generateService(templates.getServiceTemplate(), schema, serviceName, packageName, queryTypeName, mutationTypeName, subscriptionTypeName)
		);

		for (var typeDefinition : schema.getTypes(InputObjectTypeDefinition.class)) {
			var typeName = typeDefinition.getName();
			generatedFiles.put(
					packageName + "." + typeName,
					generateType(templates.getInputTemplate(), typeDefinition, typeName, packageName)
			);
		}

		for (var typeDefinition : schema.getTypes(ObjectTypeDefinition.class)) {
			var typeName = typeDefinition.getName();
			if (typeName.equals(queryTypeName) || typeName.equals(mutationTypeName) || typeName.equals(subscriptionTypeName)) {
				continue;
			}
			generatedFiles.put(
					packageName + "." + typeName,
					generateType(templates.getObjectTemplate(), typeDefinition, typeName, packageName)
			);
		}

		for (var typeDefinition : schema.getTypes(InterfaceTypeDefinition.class)) {
			var typeName = typeDefinition.getName();
			generatedFiles.put(
					packageName + "." + typeName,
					generateType(templates.getInterfaceTemplate(), typeDefinition, typeName, packageName)
			);
		}

		for (var typeDefinition : schema.getTypes(EnumTypeDefinition.class)) {
			var typeName = typeDefinition.getName();
			generatedFiles.put(
					packageName + "." + typeName,
					generateType(templates.getEnumTemplate(), typeDefinition, typeName, packageName)
			);
		}

		return generatedFiles;
	}

	private String generateService(Template template, TypeDefinitionRegistry schema, String serviceName, String packageName, String queryTypeName, String mutationTypeName, String subscriptionTypeName) throws GeneratorException {
		return generateSourceFile(
				template,
				Map.of(
						"schema", schema,
						"serviceName", serviceName,
						"packageName", packageName,
						"queryTypeName", queryTypeName,
						"mutationTypeName", mutationTypeName,
						"subscriptionTypeName", subscriptionTypeName,
						"generatorName", getClass().getName(),
						"generationDate", ZonedDateTime.now()
				)
		);
	}

	private String generateType(Template template, TypeDefinition<?> typeDefinition, String typeName, String packageName) throws GeneratorException {
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

	private String generateSourceFile(Template template, Map<String, Object> model) throws GeneratorException {
		try (var out = new StringWriter()) {
			template.process(model, out);
			return out.toString();
		} catch (Exception e) {
			throw new GeneratorException("Could not generate Java file: " + e.getMessage(), e);
		}
	}

	private TemplateCollection readTemplates() throws GeneratorException {
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
		} catch (IOException e) {
			throw new GeneratorException("Error reading templates", e);
		}
	}

	private TypeDefinitionRegistry parseSchema(String schemaString) throws GeneratorException {
		try {
			return new SchemaParser().parse(schemaString);
		} catch (Exception e) {
			throw new GeneratorException("Can not parse GraphQL schema: " + e.getMessage(), e);
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
