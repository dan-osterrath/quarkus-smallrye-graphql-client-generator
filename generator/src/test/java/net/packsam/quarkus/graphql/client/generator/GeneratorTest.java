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
		var servicePackageName = getClass().getPackage().getName() + ".example1";
		var modelPackageName = servicePackageName + ".model";

		var sources = new Generator(
				schema,
				"UserServiceApi",
				servicePackageName,
				modelPackageName,
				Map.of(
						"allUserIds", "{ allUsers { id } }",
						"allUserNames", "{ allUsers { name } }"
				)
		).generateJavaSources();

		assertThat(sources).containsOnlyKeys(
				modelPackageName + ".CreateUpdateUserInput",
				modelPackageName + ".EventType",
				modelPackageName + ".User",
				modelPackageName + ".UserEvent",
				servicePackageName + ".UserServiceApi"
		);
	}

	@Test
	public void testGeneratingStarWarsService() throws GeneratorException {
		var schema = readSchema("/star-wars-service.graphqls");
		var servicePackageName = getClass().getPackage().getName() + ".example2";
		var modelPackageName = servicePackageName + ".model";

		var sources = new Generator(
				schema,
				"StarWarsServiceApi",
				servicePackageName,
				servicePackageName + ".model",
				Map.of(
						"allFilmsAndAllPeople",
						"{ allFilms { films { title planetConnection { planets { name } } } } allPeople { people { name homeworld { name } } } }"
				)
		).generateJavaSources();

		assertThat(sources).containsOnlyKeys(
				modelPackageName + ".Film",
				modelPackageName + ".FilmCharactersConnection",
				modelPackageName + ".FilmCharactersEdge",
				modelPackageName + ".FilmPlanetsConnection",
				modelPackageName + ".FilmPlanetsEdge",
				modelPackageName + ".FilmsConnection",
				modelPackageName + ".FilmsEdge",
				modelPackageName + ".FilmSpeciesConnection",
				modelPackageName + ".FilmSpeciesEdge",
				modelPackageName + ".FilmStarshipsConnection",
				modelPackageName + ".FilmStarshipsEdge",
				modelPackageName + ".FilmVehiclesConnection",
				modelPackageName + ".FilmVehiclesEdge",
				modelPackageName + ".Node",
				modelPackageName + ".PageInfo",
				modelPackageName + ".PeopleConnection",
				modelPackageName + ".PeopleEdge",
				modelPackageName + ".Person",
				modelPackageName + ".PersonFilmsConnection",
				modelPackageName + ".PersonFilmsEdge",
				modelPackageName + ".PersonStarshipsConnection",
				modelPackageName + ".PersonStarshipsEdge",
				modelPackageName + ".PersonVehiclesConnection",
				modelPackageName + ".PersonVehiclesEdge",
				modelPackageName + ".Planet",
				modelPackageName + ".PlanetFilmsConnection",
				modelPackageName + ".PlanetFilmsEdge",
				modelPackageName + ".PlanetResidentsConnection",
				modelPackageName + ".PlanetResidentsEdge",
				modelPackageName + ".PlanetsConnection",
				modelPackageName + ".PlanetsEdge",
				modelPackageName + ".Species",
				modelPackageName + ".SpeciesConnection",
				modelPackageName + ".SpeciesEdge",
				modelPackageName + ".SpeciesFilmsConnection",
				modelPackageName + ".SpeciesFilmsEdge",
				modelPackageName + ".SpeciesPeopleConnection",
				modelPackageName + ".SpeciesPeopleEdge",
				modelPackageName + ".Starship",
				modelPackageName + ".StarshipFilmsConnection",
				modelPackageName + ".StarshipFilmsEdge",
				modelPackageName + ".StarshipPilotsConnection",
				modelPackageName + ".StarshipPilotsEdge",
				modelPackageName + ".StarshipsConnection",
				modelPackageName + ".StarshipsEdge",
				servicePackageName + ".StarWarsServiceApi",
				modelPackageName + ".Vehicle",
				modelPackageName + ".VehicleFilmsConnection",
				modelPackageName + ".VehicleFilmsEdge",
				modelPackageName + ".VehiclePilotsConnection",
				modelPackageName + ".VehiclePilotsEdge",
				modelPackageName + ".VehiclesConnection",
				modelPackageName + ".VehiclesEdge"
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
