package net.packsam.quarkus.graphql.client.generator;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

/**
 * Annotation processor for creating a Quarkus SmallRye GraphQL client.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
@SupportedAnnotationTypes({
		"net.packsam.quarkus.graphql.client.generator.GraphQLSchema"
})
public class AnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		return annotations.stream()
				.map(roundEnv::getElementsAnnotatedWith)
				.flatMap(Collection::stream)
				.map(element -> (TypeElement) element)
				.map(this::generateClient)
				.reduce(false, Boolean::logicalOr);
	}

	private boolean generateClient(TypeElement typeElement) {
		var graphQLSchemaAnnotation = typeElement.getAnnotation(GraphQLSchema.class);
		var serviceName = typeElement.getSimpleName().toString() + "Api";
		var packageName = typeElement.getEnclosingElement().toString();

		var schemaString = readSchema(graphQLSchemaAnnotation.value(), typeElement);

		var generator = new Generator(schemaString, serviceName, packageName);
		try {
			var sources = generator.generateJavaSources();
			sources.forEach(this::writeSource);
			return !sources.isEmpty();
		} catch (GeneratorException e) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					e.getMessage()
			);
			return false;
		}
	}

	private void writeSource(String className, String content) {
		try {
			var sourceFile = processingEnv.getFiler().createSourceFile(className);
			try (var writer = sourceFile.openWriter()) {
				writer.write(content);
			}
		} catch (IOException e) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					"Could not write source file " + className + ": " + e.getMessage()
			);
		}
	}

	private String readSchema(String schemaLocation, TypeElement typeElement) {
		if (schemaLocation == null) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					"GraphQL schema location is not set"
			);
			return null;
		}

		try {
			URI schemaUri;
			if (schemaLocation.startsWith("resource:")) {
				String outputPath = getCompilationOutputPath(typeElement);
				if (outputPath == null) return null;

				schemaUri = new File(outputPath + schemaLocation.substring("location:".length())).toURI();
			} else {
				schemaUri = URI.create(schemaLocation);
			}

			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.NOTE,
					"Reading GraphQL schema from " + schemaUri
			);

			try (var is = schemaUri.toURL().openStream()) {
				return new String(is.readAllBytes(), StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					"GraphQL schema could not be loaded: " + e.getMessage()
			);
			return null;
		}
	}

	private String getCompilationOutputPath(TypeElement typeElement) throws IOException {
		Elements elementUtils = processingEnv.getElementUtils();
		var fqdn = elementUtils.getBinaryName(typeElement).toString();
		var lastDotIdx = fqdn.lastIndexOf(".");
		var pkg = fqdn.substring(0, lastDotIdx);
		var name = fqdn.substring(lastDotIdx + 1);
		var absolutePath = processingEnv.getFiler().getResource(CLASS_OUTPUT, pkg, name).getName();

		// javac and IntelliJ compilers behave differently here, so try both path separators
		var relativePath1 = fqdn.replace('.', '/');
		var relativePath2 = fqdn.replace('.', File.separatorChar);

		var rootPkgIdx = Math.max(absolutePath.indexOf(relativePath1), absolutePath.indexOf(relativePath2));
		if (rootPkgIdx < 0) {
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					"Could not find javac output path"
			);
			return null;
		}

		return absolutePath.substring(0, rootPkgIdx);
	}
}
