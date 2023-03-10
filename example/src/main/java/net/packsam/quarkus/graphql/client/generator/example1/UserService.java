package net.packsam.quarkus.graphql.client.generator.example1;

import net.packsam.quarkus.graphql.client.generator.GraphQLQuery;
import net.packsam.quarkus.graphql.client.generator.GraphQLSchema;

@GraphQLSchema(
		value = "resource:user-service.graphqls",
		targetModelPackage = "net.packsam.quarkus.graphql.client.generator.example1.model"
)
@GraphQLQuery(
		identifier = "allUserIds",
		value = "{ allUsers { id } }"
)
@GraphQLQuery(
		identifier = "allUserNames",
		value = "{ allUsers { name } }"
)
interface UserService {
}
