package kestrel.example.infrastructure.driving.http.controllers

import com.dreweaster.ddd.kestrel.application.Backend
import com.dreweaster.ddd.kestrel.infrastructure.http.eventsource.producer.BoundedContextHttpJsonEventProducer
import com.dreweaster.ddd.kestrel.infrastructure.http.eventsource.producer.EventPayloadSerialisationStrategy
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.micronaut.http.HttpParameters
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import reactor.core.publisher.Mono
import javax.inject.Inject

@Controller("/events")
class EventsController @Inject constructor(backend: Backend, private val objectMapper: ObjectMapper) {

    private val eventsProducer = BoundedContextHttpJsonEventProducer(backend)

    @Get(produces = [MediaType.APPLICATION_JSON])
    fun events(parameters: HttpParameters): Mono<ObjectNode> =
        eventsProducer.produceFrom(parameters.asMap(), EventPayloadSerialisationStrategy.json(objectMapper))
}