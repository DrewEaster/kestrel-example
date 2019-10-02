package kestrel.example.infrastructure

import io.micronaut.runtime.Micronaut
import org.flywaydb.core.Flyway

object Application {

    @JvmStatic
    fun main(args: Array<String>) {

        // Migrate DB
        val flywayConfiguration =  Flyway.configure()
            .locations("classpath:kestrel/example")
            .dataSource("jdbc:postgresql://example-db/postgres", "postgres", "password")

        Flyway(flywayConfiguration).migrate()

        Micronaut.build()
            .packages("kestrel.example")
            .mainClass(Application.javaClass)
            .start()
    }
}