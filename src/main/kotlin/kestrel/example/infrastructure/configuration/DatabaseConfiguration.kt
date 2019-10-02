package kestrel.example.infrastructure.configuration

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("database")
class DatabaseConfiguration  {
    var host: String = ""
    var name: String = ""
    var username: String = ""
    var password: String = ""
}