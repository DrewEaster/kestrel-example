package kestrel.example.application.processmanager.stateless

import com.dreweaster.ddd.kestrel.application.BoundedContextEventSources
import com.dreweaster.ddd.kestrel.application.BoundedContextSubscriptionEdenPolicy.FROM_NOW
import com.dreweaster.ddd.kestrel.application.StatelessProcessManager
import io.micronaut.context.annotation.Context
import kestrel.example.application.BoundedContexts.UserContext
import kestrel.example.domain.aggregates.user.UserRegistered
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono.fromRunnable

@Context
class HelloNewUser constructor(boundedContexts: BoundedContextEventSources): StatelessProcessManager(boundedContexts) {

    companion object {
        private val LOG = LoggerFactory.getLogger(HelloNewUser::class.java)
    }

    init {
        processManager(name = "hello-new-user") {

            subscribe(context = UserContext, edenPolicy = FROM_NOW) {

                event<UserRegistered> { event, _ ->
                    fromRunnable { LOG.info("Hello ${event.username}!") }
                }
            }
        }.start()
    }
}