package kestrel.example.infrastructure.driven.serialisation.user.event

import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapperBuilderFactory
import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapper
import com.dreweaster.ddd.kestrel.util.json.jsonObject
import kestrel.example.domain.aggregates.user.UserLocked
import javax.inject.Singleton

@Singleton
class UserLockedMapper : JsonMapper<UserLocked> {

    override fun configure(factory: JsonMapperBuilderFactory<UserLocked>) {
        factory.create(UserLocked::class.qualifiedName!!)
            .mappingFunctions({ _ -> jsonObject() }, { _ -> UserLocked})
    }
}