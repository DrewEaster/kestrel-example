package kestrel.example.infrastructure.driving.eventsource

import com.dreweaster.ddd.kestrel.domain.Persistable
import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMappingContext
import kestrel.example.domain.aggregates.user.*
import com.dreweaster.ddd.kestrel.infrastructure.http.eventsource.consumer.BoundedContextHttpEventSourceFactory
import kestrel.example.application.BoundedContexts.UserContext
import javax.inject.Singleton

@Singleton
class UserContextHttpEventSourceFactory(private val userContextAggregateDataMappingContext: JsonMappingContext): BoundedContextHttpEventSourceFactory(UserContext) {

    override val deserialisers = eventDeserialisers {

        tag("user-event") {

            event<UserRegistered> {
                deserialiser(type = "kestrel.example.domain.aggregates.user.UserRegistered", version = 1, handler = mappingContextDeserialiser())
            }

            event<PasswordChanged> {
                deserialiser(type = "kestrel.example.domain.aggregates.user.PasswordChanged", version = 1, handler = mappingContextDeserialiser())
            }

            event<UsernameChanged> {
                deserialiser(type = "kestrel.example.domain.aggregates.user.UsernameChanged", version = 1, handler = mappingContextDeserialiser())
            }

            event<UserLocked> {
                deserialiser(type = "kestrel.example.domain.aggregates.user.UserLocked", version = 1, handler = mappingContextDeserialiser())
            }

            event<UserUnlocked> {
                deserialiser(type = "kestrel.example.domain.aggregates.user.UserUnlocked", version = 1, handler = mappingContextDeserialiser())
            }

            event<FailedLoginAttemptsIncremented> {
                deserialiser(type = "kestrel.example.domain.aggregates.user.FailedLoginAttemptsIncremented", version = 1, handler = mappingContextDeserialiser())
            }
        }
    }

    private fun <Data : Persistable> mappingContextDeserialiser(): (String, String, Int) -> Data = { payload, type, version ->
        userContextAggregateDataMappingContext.deserialise(payload, type, version)
    }
}