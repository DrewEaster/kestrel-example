package kestrel.example.infrastructure.configuration

import com.dreweaster.ddd.kestrel.infrastructure.http.eventsource.consumer.BoundedContextHttpEventSourceConfiguration
import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter
import java.time.Duration

@EachProperty("contexts")
class BoundedContextConfiguration constructor(@param:Parameter val name: String) {
    var protocol: String = ""
    var host: String = ""
    var port: Int = 8080
    var path: String = ""
    var subscriptions: List<BoundedContextSubscriptionConfiguration> = emptyList()

    class BoundedContextSubscriptionConfiguration {
        var name: String = ""
        var batchSize: Int = 0
        var repeatSchedule: Long = 0
        var timeout: Long = 0
        var enabled: Boolean = true
        var ignoreUnrecognisedEvents: Boolean = false
    }

    fun toBoundedContextHttpEventSourceConfiguration(): BoundedContextHttpEventSourceConfiguration {
        val subscriptionsConfigurationMap = subscriptions.map { it.name to it }.toMap()

        return object : BoundedContextHttpEventSourceConfiguration {

            override val producerEndpointProtocol = protocol

            override val producerEndpointHostname = host

            override val producerEndpointPort = port

            override val producerEndpointPath = path

            override fun batchSizeFor(subscriptionName: String) = subscriptionsConfigurationMap.configurationFor(subscriptionName).batchSize

            override fun repeatScheduleFor(subscriptionName: String) = Duration.ofMillis(subscriptionsConfigurationMap.configurationFor(subscriptionName).repeatSchedule)

            override fun timeoutFor(subscriptionName: String) = Duration.ofMillis(subscriptionsConfigurationMap.configurationFor(subscriptionName).timeout)

            override fun enabled(subscriptionName: String) = subscriptionsConfigurationMap.configurationFor(subscriptionName).enabled

            override fun ignoreUnrecognisedEvents(subscriptionName: String) = subscriptionsConfigurationMap.configurationFor(subscriptionName).ignoreUnrecognisedEvents
        }
    }

    private fun Map<String, BoundedContextSubscriptionConfiguration>.configurationFor(subscriptionName: String) =
        this[subscriptionName] ?: error("No configuration found for subscription: $name:$subscriptionName")
}