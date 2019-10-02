package kestrel.example.infrastructure.driven.serialisation.user.event

import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapperBuilderFactory
import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapper
import com.dreweaster.ddd.kestrel.util.json.jsonObject
import com.dreweaster.ddd.kestrel.util.json.string
import com.fasterxml.jackson.databind.node.ObjectNode
import kestrel.example.domain.aggregates.user.UserRegistered
import javax.inject.Singleton

@Singleton
class UserRegisteredMapper : JsonMapper<UserRegistered> {

    companion object {
        val serialiser: (UserRegistered) -> ObjectNode = { event ->
            jsonObject(
                    "username" to event.username,
                    "password" to event.password
            )
        }

        val deserialiser: (ObjectNode) -> UserRegistered = { node ->
            UserRegistered(
                    username = node["username"].string,
                    password = node["password"].string
            )
        }
    }

    override fun configure(factory: JsonMapperBuilderFactory<UserRegistered>) {
        factory.create(UserRegistered::class.qualifiedName!!)
            .mappingFunctions(serialiser, deserialiser)
    }
}