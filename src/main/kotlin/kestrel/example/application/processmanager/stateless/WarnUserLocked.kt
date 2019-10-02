package kestrel.example.application.processmanager.stateless

import com.dreweaster.ddd.kestrel.application.BoundedContextEventSources
import com.dreweaster.ddd.kestrel.application.BoundedContextSubscriptionEdenPolicy.BEGINNING_OF_TIME
import com.dreweaster.ddd.kestrel.application.StatelessProcessManager
import io.micronaut.context.annotation.Context
import kestrel.example.application.BoundedContexts.UserContext
import kestrel.example.domain.aggregates.user.UserLocked
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono.fromRunnable

@Context
class WarnUserLocked constructor(boundedContexts: BoundedContextEventSources): StatelessProcessManager(boundedContexts) {

    companion object {
        private val LOG = LoggerFactory.getLogger(WarnUserLocked::class.java)
    }

    init {
        processManager(name = "warn-user-locked") {

            subscribe(context = UserContext, edenPolicy = BEGINNING_OF_TIME) {

                event<UserLocked> { _, metadata ->
                    fromRunnable { LOG.warn("User ${metadata.aggregateId} was locked!") }
                }
            }
        }.start()
    }
}