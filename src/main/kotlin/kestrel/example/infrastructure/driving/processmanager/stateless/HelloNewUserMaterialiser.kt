package kestrel.example.infrastructure.driving.processmanager.stateless

import com.dreweaster.ddd.kestrel.application.BoundedContextEventSources
import com.dreweaster.ddd.kestrel.application.BoundedContextSubscriptionEdenPolicy.FROM_NOW
import com.dreweaster.ddd.kestrel.infrastructure.processmanagers.StatelessProcessManagerMaterialiser
import io.micronaut.context.annotation.Context
import kestrel.example.application.BoundedContexts.UserContext
import kestrel.example.application.processmanager.stateless.HelloNewUser
import kestrel.example.domain.aggregates.user.UserRegistered

@Context
class HelloNewUserMaterialiser(
    boundedContexts: BoundedContextEventSources,
    helloNewUser: HelloNewUser,
) : StatelessProcessManagerMaterialiser(boundedContexts) {

    init {
        materialise(helloNewUser) {

            subscribe(name = "user-events", context = UserContext, edenPolicy = FROM_NOW) {
                event<UserRegistered>()
            }
        }.start()
    }
}