package net.packsam.quarkus.graphql.client.generator;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneratorTest {
	@Test
	public void testGeneratingUserService() throws GeneratorException {
		var schema = readSchema("/user-service.graphqls");
		var packageName = getClass().getPackage().getName() + ".example1";

		var sources = new Generator(
				schema,
				"UserServiceApi",
				packageName,
				Map.of(
						"allUserIds", "{ allUsers { id } }",
						"allUserNames", "{ allUsers { name } }"
				)
		).generateJavaSources();

		assertThat(sources).containsOnlyKeys(
				packageName + ".CreateUpdateUserInput",
				packageName + ".EventType",
				packageName + ".User",
				packageName + ".UserEvent",
				packageName + ".UserServiceApi"
		);
	}

	@Test
	public void testGeneratingStarWarsService() throws GeneratorException {
		var schema = readSchema("/star-wars-service.graphqls");
		var packageName = getClass().getPackage().getName() + ".example2";

		var sources = new Generator(
				schema,
				"StarWarsServiceApi",
				packageName,
				Map.of(
						"allFilmsAndAllPeople",
						"{ allFilms { films { title planetConnection { planets { name } } } } allPeople { people { name homeworld { name } } } }"
				)
		).generateJavaSources();

		assertThat(sources).containsOnlyKeys(
				packageName + ".Film",
				packageName + ".FilmCharactersConnection",
				packageName + ".FilmCharactersEdge",
				packageName + ".FilmPlanetsConnection",
				packageName + ".FilmPlanetsEdge",
				packageName + ".FilmsConnection",
				packageName + ".FilmsEdge",
				packageName + ".FilmSpeciesConnection",
				packageName + ".FilmSpeciesEdge",
				packageName + ".FilmStarshipsConnection",
				packageName + ".FilmStarshipsEdge",
				packageName + ".FilmVehiclesConnection",
				packageName + ".FilmVehiclesEdge",
				packageName + ".Node",
				packageName + ".PageInfo",
				packageName + ".PeopleConnection",
				packageName + ".PeopleEdge",
				packageName + ".Person",
				packageName + ".PersonFilmsConnection",
				packageName + ".PersonFilmsEdge",
				packageName + ".PersonStarshipsConnection",
				packageName + ".PersonStarshipsEdge",
				packageName + ".PersonVehiclesConnection",
				packageName + ".PersonVehiclesEdge",
				packageName + ".Planet",
				packageName + ".PlanetFilmsConnection",
				packageName + ".PlanetFilmsEdge",
				packageName + ".PlanetResidentsConnection",
				packageName + ".PlanetResidentsEdge",
				packageName + ".PlanetsConnection",
				packageName + ".PlanetsEdge",
				packageName + ".Species",
				packageName + ".SpeciesConnection",
				packageName + ".SpeciesEdge",
				packageName + ".SpeciesFilmsConnection",
				packageName + ".SpeciesFilmsEdge",
				packageName + ".SpeciesPeopleConnection",
				packageName + ".SpeciesPeopleEdge",
				packageName + ".Starship",
				packageName + ".StarshipFilmsConnection",
				packageName + ".StarshipFilmsEdge",
				packageName + ".StarshipPilotsConnection",
				packageName + ".StarshipPilotsEdge",
				packageName + ".StarshipsConnection",
				packageName + ".StarshipsEdge",
				packageName + ".StarWarsServiceApi",
				packageName + ".Vehicle",
				packageName + ".VehicleFilmsConnection",
				packageName + ".VehicleFilmsEdge",
				packageName + ".VehiclePilotsConnection",
				packageName + ".VehiclePilotsEdge",
				packageName + ".VehiclesConnection",
				packageName + ".VehiclesEdge"
		);
	}

	private String readSchema(String schemaResourceName) {
		try (var is = getClass().getResourceAsStream(schemaResourceName)) {
			return new String(Objects.requireNonNull(is).readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
