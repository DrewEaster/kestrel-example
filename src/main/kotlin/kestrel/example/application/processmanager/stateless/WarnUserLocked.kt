package kestrel.example.application.processmanager.stateless

import com.dreweaster.ddd.kestrel.application.StatelessProcessManager
import kestrel.example.domain.aggregates.user.UserLocked
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono.fromRunnable
import javax.inject.Singleton

@Singleton
class WarnUserLocked: StatelessProcessManager {

    companion object {
        private val LOG = LoggerFactory.getLogger(WarnUserLocked::class.java)
    }

    override val behaviour = processManager("warn-user-locked") {

        event<UserLocked> { _, metadata ->
            fromRunnable { LOG.warn("User ${metadata.aggregateId} was locked!") }
        }
    }
}