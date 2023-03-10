package net.packsam.quarkus.graphql.client.generator.example2;

import net.packsam.quarkus.graphql.client.generator.GraphQLQuery;
import net.packsam.quarkus.graphql.client.generator.GraphQLSchema;

@GraphQLSchema(
		value = "resource:star-wars-service.graphqls",
		targetModelPackage = "net.packsam.quarkus.graphql.client.generator.example2.model"
)
@GraphQLQuery(
		identifier = "allFilmsAndAllPeople",
		value = "{" +
				"   allFilms {" +
				"       films {" +
				"           title" +
				"           planetConnection {" +
				"               planets {" +
				"                   name" +
				"               }" +
				"           }" +
				"       }" +
				"   }" +
				"   allPeople {" +
				"       people {" +
				"           name" +
				"           homeworld {" +
				"               name" +
				"           }" +
				"       }" +
				"   }" +
				"}"
)
interface StarWarsService {
}
