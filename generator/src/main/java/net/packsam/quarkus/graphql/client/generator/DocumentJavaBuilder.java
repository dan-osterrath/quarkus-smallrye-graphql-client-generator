package net.packsam.quarkus.graphql.client.generator;

import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.OperationDefinition;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class DocumentJavaBuilder {

	private static final String NL = "\n";

	private final StringBuilder builder = new StringBuilder();


	private int indent = 3;

	public static String build(Document document) {
		return new DocumentJavaBuilder().generate(document);
	}

	private String generate(Document document) {
		if (document == null) {
			return null;
		}

		append(document);

		return builder.toString();
	}

	private void append(Document document) {
		builder.append(NL);
		builder.append(indent()).append("Document.document(").append(NL);
		indent++;
		var definitions = document.getDefinitions();
		var definitionsNum = definitions.size();
		for (int i = 0; i < definitionsNum; i++) {
			append(definitions.get(i));
			if (i < definitionsNum - 1) {
				builder.append(",");
			}
			builder.append(NL);
		}
		indent--;
		builder.append(indent()).append(")");
	}

	private void append(Definition<?> definition) {
		if (definition instanceof OperationDefinition) {
			append((OperationDefinition) definition);
		} else {
			throw new GeneratorException("Unsupported definition type: " + definition.getClass().getName());
		}
	}

	private void append(OperationDefinition definition) {
		builder.append(indent()).append("Operation.operation(OperationType.").append(definition.getOperation());
		var hadMultiLineArguments = false;

		var selectionSet = definition.getSelectionSet();
		if (selectionSet != null) {
			hadMultiLineArguments = true;
			builder.append(",").append(NL);
			indent++;
			append(selectionSet);
			indent--;
		}

		if (hadMultiLineArguments) {
			builder.append(indent()).append(")");
		} else {
			builder.append(")");
		}

	}

	private void append(SelectionSet selectionSet) {
		var selections = selectionSet.getSelections();
		var selectionsNum = selections.size();
		for (int i = 0; i < selectionsNum; i++) {
			append(selections.get(i));
			if (i < selectionsNum - 1) {
				builder.append(",");
			}
			builder.append(NL);
		}
	}

	private void append(Selection<?> selection) {
		if (selection instanceof Field) {
			append((Field) selection);
		} else {
			throw new GeneratorException("Unsupported selection type: " + selection.getClass().getName());
		}
	}

	private void append(Field field) {
		builder.append(indent()).append("Field.field(\"").append(field.getName()).append("\"");
		var hadMultiLineArguments = false;

		var selectionSet = field.getSelectionSet();
		if (selectionSet != null) {
			hadMultiLineArguments = true;
			builder.append(",").append(NL);
			indent++;
			append(selectionSet);
			indent--;
		}

		if (hadMultiLineArguments) {
			builder.append(indent()).append(")");
		} else {
			builder.append(")");
		}
	}

	private String indent() {
		return "\t".repeat(Math.max(0, indent));
	}
}
