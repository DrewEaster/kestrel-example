package kestrel.example.application.processmanager.stateless

import com.dreweaster.ddd.kestrel.application.StatelessProcessManager
import kestrel.example.domain.aggregates.user.UserRegistered
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono.fromRunnable
import javax.inject.Singleton

@Singleton
class HelloNewUser: StatelessProcessManager {

    companion object {
        private val LOG = LoggerFactory.getLogger(HelloNewUser::class.java)
    }

    override val behaviour = processManager("hello-new-user") {

        event<UserRegistered> { event, _ ->
            fromRunnable { LOG.info("Hello ${event.username}!") }
        }
    }
}