package net.packsam.quarkus.graphql.client.generator.example2;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.graphql.client.core.OperationType;
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.smallrye.graphql.client.core.Document.document;
import static io.smallrye.graphql.client.core.Field.field;
import static io.smallrye.graphql.client.core.Operation.operation;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class StarWarsServiceTest {

	@Inject
	@GraphQLClient("DynamicStarWarsServiceApi")
	DynamicGraphQLClient client;

	@Test
	public void testLoadingAllFilms() throws Exception {
		var document = document(
				operation(OperationType.QUERY,
						field(StarWarsServiceApi.QUERY_ALL_FILMS,
								field(FilmsConnection.FIELD_FILMS,
										field(Film.FIELD_TITLE),
										field(Film.FIELD_PLANET_CONNECTION,
												field(PlanetsConnection.FIELD_PLANETS,
														field(Planet.FIELD_NAME)
												)
										)
								)
						),
						field(StarWarsServiceApi.QUERY_ALL_PEOPLE,
								field(PeopleConnection.FIELD_PEOPLE,
										field(Person.FIELD_NAME),
										field(Person.FIELD_HOMEWORLD,
												field(Planet.FIELD_NAME)
										)
								)
						)
				)
		);
		var response = client.executeSync(document);

		assertThat(response).isNotNull();
		assertThat(response.hasData()).isTrue();
		assertThat(response.hasError()).isFalse();

		var filmPlanetMap = response.getObject(FilmsConnection.class, StarWarsServiceApi.QUERY_ALL_FILMS)
				.getFilms()
				.stream()
				.collect(Collectors.toMap(
						Film::getTitle,
						film -> film.getPlanetConnection()
								.getPlanets()
								.stream()
								.map(Planet::getName)
								.collect(Collectors.toSet())
				));

		var peoplePlanetMap = response.getObject(PeopleConnection.class, StarWarsServiceApi.QUERY_ALL_PEOPLE)
				.getPeople()
				.stream()
				.collect(Collectors.toMap(
						Person::getName,
						person -> person.getHomeworld().getName()
				));

		assertThat(filmPlanetMap).isEqualTo(Map.of(
				"A New Hope", Set.of(
						"Tatooine",
						"Alderaan",
						"Yavin IV"
				),
				"The Empire Strikes Back", Set.of(
						"Hoth",
						"Dagobah",
						"Bespin",
						"Ord Mantell"
				),
				"Return of the Jedi", Set.of(
						"Tatooine",
						"Dagobah",
						"Endor",
						"Naboo",
						"Coruscant"
				),
				"The Phantom Menace", Set.of(
						"Tatooine",
						"Naboo",
						"Coruscant"
				),
				"Attack of the Clones", Set.of(
						"Tatooine",
						"Naboo",
						"Coruscant",
						"Kamino",
						"Geonosis"
				),
				"Revenge of the Sith", Set.of(
						"Tatooine",
						"Alderaan",
						"Dagobah",
						"Naboo",
						"Coruscant",
						"Utapau",
						"Mustafar",
						"Kashyyyk",
						"Polis Massa",
						"Mygeeto",
						"Felucia",
						"Cato Neimoidia",
						"Saleucami"
				)
		));

		assertThat(peoplePlanetMap).isEqualTo(Map.<String, String>ofEntries(
				entry("Jar Jar Binks", "Naboo"),
				entry("Arvel Crynyd", "unknown"),
				entry("Rugor Nass", "Naboo"),
				entry("Boba Fett", "Kamino"),
				entry("Darth Maul", "Dathomir"),
				entry("Wilhuff Tarkin", "Eriadu"),
				entry("Finis Valorum", "Coruscant"),
				entry("Darth Vader", "Tatooine"),
				entry("Ben Quadinaros", "Tund"),
				entry("Cordé", "Naboo"),
				entry("Chewbacca", "Kashyyyk"),
				entry("Nien Nunb", "Sullust"),
				entry("Bib Fortuna", "Ryloth"),
				entry("Biggs Darklighter", "Tatooine"),
				entry("Shmi Skywalker", "Tatooine"),
				entry("Grievous", "Kalee"),
				entry("R4-P17", "unknown"),
				entry("Lama Su", "Kamino"),
				entry("Raymus Antilles", "Alderaan"),
				entry("Yoda", "unknown"),
				entry("Palpatine", "Naboo"),
				entry("San Hill", "Muunilinst"),
				entry("Ackbar", "Mon Cala"),
				entry("Gregar Typho", "Naboo"),
				entry("Jabba Desilijic Tiure", "Nal Hutta"),
				entry("Owen Lars", "Tatooine"),
				entry("Eeth Koth", "Iridonia"),
				entry("R2-D2", "Naboo"),
				entry("Obi-Wan Kenobi", "Stewjon"),
				entry("Lobot", "Bespin"),
				entry("Mas Amedda", "Champala"),
				entry("Bossk", "Trandosha"),
				entry("Shaak Ti", "Shili"),
				entry("IG-88", "unknown"),
				entry("Greedo", "Rodia"),
				entry("C-3PO", "Tatooine"),
				entry("Poggle the Lesser", "Geonosis"),
				entry("Jocasta Nu", "Coruscant"),
				entry("Gasgano", "Troiken"),
				entry("Watto", "Toydaria"),
				entry("Barriss Offee", "Mirial"),
				entry("Zam Wesell", "Zolan"),
				entry("Wedge Antilles", "Corellia"),
				entry("Luminara Unduli", "Mirial"),
				entry("Nute Gunray", "Cato Neimoidia"),
				entry("Quarsh Panaka", "Naboo"),
				entry("Qui-Gon Jinn", "unknown"),
				entry("Luke Skywalker", "Tatooine"),
				entry("R5-D4", "Tatooine"),
				entry("Roos Tarpals", "Naboo"),
				entry("Mace Windu", "Haruun Kal"),
				entry("Padmé Amidala", "Naboo"),
				entry("Wat Tambor", "Skako"),
				entry("Leia Organa", "Alderaan"),
				entry("Anakin Skywalker", "Tatooine"),
				entry("Ki-Adi-Mundi", "Cerea"),
				entry("Adi Gallia", "Coruscant"),
				entry("Dexter Jettster", "Ojom"),
				entry("Jango Fett", "Concord Dawn"),
				entry("Cliegg Lars", "Tatooine"),
				entry("Tarfful", "Kashyyyk"),
				entry("Sebulba", "Malastare"),
				entry("Taun We", "Kamino"),
				entry("Tion Medon", "Utapau"),
				entry("Plo Koon", "Dorin"),
				entry("Kit Fisto", "Glee Anselm"),
				entry("Lando Calrissian", "Socorro"),
				entry("Ratts Tyerel", "Aleen Minor"),
				entry("Saesee Tiin", "Iktotch"),
				entry("Ayla Secura", "Ryloth"),
				entry("Wicket Systri Warrick", "Endor"),
				entry("Sly Moore", "Umbara"),
				entry("Bail Prestor Organa", "Alderaan"),
				entry("Beru Whitesun lars", "Tatooine"),
				entry("Dormé", "Naboo"),
				entry("Dud Bolt", "Vulpter"),
				entry("Yarael Poof", "Quermia"),
				entry("Ric Olié", "Naboo"),
				entry("Han Solo", "Corellia"),
				entry("Jek Tono Porkins", "Bestine IV"),
				entry("Dooku", "Serenno"),
				entry("Mon Mothma", "Chandrila")
		));
	}
}
