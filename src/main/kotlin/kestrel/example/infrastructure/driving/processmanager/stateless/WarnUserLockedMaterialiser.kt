package kestrel.example.infrastructure.driving.processmanager.stateless

import com.dreweaster.ddd.kestrel.application.BoundedContextEventSources
import com.dreweaster.ddd.kestrel.application.BoundedContextSubscriptionEdenPolicy.FROM_NOW
import com.dreweaster.ddd.kestrel.infrastructure.processmanagers.StatelessProcessManagerMaterialiser
import io.micronaut.context.annotation.Context
import kestrel.example.application.BoundedContexts.UserContext
import kestrel.example.application.processmanager.stateless.WarnUserLocked
import kestrel.example.domain.aggregates.user.UserLocked

@Context
class WarnUserLockedMaterialiser(
    boundedContexts: BoundedContextEventSources,
    warnUserLocked: WarnUserLocked,
) : StatelessProcessManagerMaterialiser(boundedContexts) {

    init {
        materialise(warnUserLocked) {

            subscribe(name = "user-events", context = UserContext, edenPolicy = FROM_NOW) {
                event<UserLocked>()
            }
        }.start()
    }
}