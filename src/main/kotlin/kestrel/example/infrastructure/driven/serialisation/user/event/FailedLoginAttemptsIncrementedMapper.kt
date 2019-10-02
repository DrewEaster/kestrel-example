package kestrel.example.infrastructure.driven.serialisation.user.event

import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapperBuilderFactory
import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapper
import com.dreweaster.ddd.kestrel.util.json.jsonObject
import kestrel.example.domain.aggregates.user.FailedLoginAttemptsIncremented
import javax.inject.Singleton

@Singleton
class FailedLoginAttemptsIncrementedMapper : JsonMapper<FailedLoginAttemptsIncremented> {

    override fun configure(factory: JsonMapperBuilderFactory<FailedLoginAttemptsIncremented>) {
        factory.create(FailedLoginAttemptsIncremented::class.qualifiedName!!)
            .mappingFunctions({ _ -> jsonObject() }, { _ -> FailedLoginAttemptsIncremented })
    }
}