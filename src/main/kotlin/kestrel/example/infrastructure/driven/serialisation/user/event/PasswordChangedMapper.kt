package kestrel.example.infrastructure.driven.serialisation.user.event

import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapperBuilderFactory
import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapper
import com.dreweaster.ddd.kestrel.util.json.jsonObject
import com.dreweaster.ddd.kestrel.util.json.string
import com.fasterxml.jackson.databind.node.ObjectNode
import kestrel.example.domain.aggregates.user.PasswordChanged
import javax.inject.Singleton

@Singleton
class PasswordChangedMapper : JsonMapper<PasswordChanged> {

    companion object {
        val serialiser: (PasswordChanged) -> ObjectNode = { event ->
            jsonObject(
                    "old_password" to event.oldPassword,
                    "password" to event.password
            )
        }

        val deserialiser: (ObjectNode) -> PasswordChanged = { node ->
            PasswordChanged(
                    oldPassword = node["old_password"].string,
                    password = node["password"].string
            )
        }
    }

    override fun configure(factory: JsonMapperBuilderFactory<PasswordChanged>) {
        factory.create(PasswordChanged::class.qualifiedName!!)
            .mappingFunctions(serialiser, deserialiser)
    }
}