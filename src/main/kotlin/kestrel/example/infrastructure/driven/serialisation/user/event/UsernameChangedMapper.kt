package kestrel.example.infrastructure.driven.serialisation.user.event

import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapperBuilderFactory
import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapper
import com.dreweaster.ddd.kestrel.util.json.jsonObject
import com.dreweaster.ddd.kestrel.util.json.string
import com.fasterxml.jackson.databind.node.ObjectNode
import kestrel.example.domain.aggregates.user.UsernameChanged
import javax.inject.Singleton

@Singleton
class UsernameChangedMapper : JsonMapper<UsernameChanged> {

    companion object {
        val serialiser: (UsernameChanged) -> ObjectNode = { event ->
            jsonObject(
                    "username" to event.username
            )
        }

        val deserialiser: (ObjectNode) -> UsernameChanged = { node ->
            UsernameChanged(
                    username = node["username"].string
            )
        }
    }

    override fun configure(factory: JsonMapperBuilderFactory<UsernameChanged>) {
        factory.create(UsernameChanged::class.qualifiedName!!)
            .mappingFunctions(serialiser, deserialiser)
    }
}