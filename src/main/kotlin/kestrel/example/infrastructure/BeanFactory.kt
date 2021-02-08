package kestrel.example.infrastructure

import com.dreweaster.ddd.kestrel.application.*
import com.dreweaster.ddd.kestrel.application.offset.OffsetTracker
import com.dreweaster.ddd.kestrel.application.scheduling.Scheduler
import com.dreweaster.ddd.kestrel.domain.*
import com.dreweaster.ddd.kestrel.infrastructure.cluster.LocalCluster
import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMapper
import com.dreweaster.ddd.kestrel.infrastructure.driven.backend.mapper.json.JsonMappingContext
import com.dreweaster.ddd.kestrel.infrastructure.http.eventsource.consumer.BoundedContextHttpEventSourceFactory
import com.dreweaster.ddd.kestrel.infrastructure.rdbms.ConsistentDatabaseProjection
import com.dreweaster.ddd.kestrel.infrastructure.rdbms.Database
import com.dreweaster.ddd.kestrel.infrastructure.rdbms.backend.PostgresBackend
import com.dreweaster.ddd.kestrel.infrastructure.rdbms.offset.PostgresOffsetTracker
import com.dreweaster.ddd.kestrel.infrastructure.rdbms.r2dbc.R2dbcDatabase
import com.dreweaster.ddd.kestrel.infrastructure.scheduling.ClusterAwareScheduler
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.r2dbc.client.R2dbc
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import kestrel.example.infrastructure.configuration.BoundedContextConfiguration
import kestrel.example.infrastructure.configuration.DatabaseConfiguration
import reactor.netty.http.client.HttpClient
import java.time.Duration
import javax.inject.Singleton

@Factory
internal class BeanFactory {

    @Singleton
    fun kotlinJacksonModule(): Module {
        return KotlinModule()
    }

    @Singleton
    fun databaseConnectionConfiguration(config: DatabaseConfiguration): PostgresqlConnectionConfiguration {
        return PostgresqlConnectionConfiguration.builder()
                .host(config.host)
                .database(config.name)
                .username(config.username)
                .password(config.password)
                .build()
    }

    @Bean(preDestroy = "close")
    @Singleton
    fun databaseConnectionPool(configuration: PostgresqlConnectionConfiguration): ConnectionPool {
        val poolConfiguration = ConnectionPoolConfiguration.builder(PostgresqlConnectionFactory(configuration))
                .validationQuery("SELECT 1")
                .maxIdleTime(Duration.ofMillis(1000))
                .maxSize(20)
                .build()

        return ConnectionPool(poolConfiguration)
    }

    @Singleton
    fun database(connectionPool: ConnectionPool): Database {
        return R2dbcDatabase(R2dbc(connectionPool))
    }

    @Singleton
    fun aggregateDataMappingContext(jsonMappers: List<JsonMapper<*>>): JsonMappingContext {
        return JsonMappingContext(jsonMappers as List<JsonMapper<Persistable>>)
    }

    @Singleton
    fun backend(database: Database, mappingContext: JsonMappingContext, readModels: List<ConsistentDatabaseProjection>): Backend {
        return PostgresBackend(database, mappingContext, readModels)
    }

    @Singleton
    fun eventSourcingConfiguration(): EventSourcingConfiguration {
        return object : EventSourcingConfiguration {
            override fun <E : DomainEvent, S : AggregateState, A : Aggregate<*, E, S>> commandDeduplicationThresholdFor(aggregateType: Aggregate<*, E, S>) = 100
            override fun <E : DomainEvent, S : AggregateState, A : Aggregate<*, E, S>> snapshotThresholdFor(aggregateType: Aggregate<*, E, S>) = 5
        }
    }

    @Singleton
    fun domainModel(backend: Backend, eventSourcingConfiguration: EventSourcingConfiguration): DomainModel {
        return EventSourcedDomainModel(backend, eventSourcingConfiguration)
    }

    @Bean(preDestroy = "shutdown")
    @Singleton
    fun scheduler(): Scheduler {
        return ClusterAwareScheduler(LocalCluster)
    }

    @Singleton
    fun offsetTracker(database: Database): OffsetTracker {
        return PostgresOffsetTracker(database)
    }

    @Singleton
    fun boundedContextEventSources(
            factories: List<BoundedContextHttpEventSourceFactory>,
            configurations: List<BoundedContextConfiguration>,
            scheduler: Scheduler,
            offsetTracker: OffsetTracker): BoundedContextEventSources {

        val configurationsMap = configurations.map { it.name to it }.toMap()
        return BoundedContextEventSources(factories.map {
            it.name to it.createHttpEventSource(
                httpClient = HttpClient.create(),
                configuration = (configurationsMap[it.name.name] ?: error("No configuration found for context: ${it.name}")).toBoundedContextHttpEventSourceConfiguration(),
                jobManager = scheduler,
                offsetTracker = offsetTracker
            )
        })
    }
}