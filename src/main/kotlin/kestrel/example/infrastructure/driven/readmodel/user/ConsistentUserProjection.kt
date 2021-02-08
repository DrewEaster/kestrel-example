package kestrel.example.infrastructure.driven.readmodel.user

import com.dreweaster.ddd.kestrel.domain.Persistable
import com.dreweaster.ddd.kestrel.infrastructure.rdbms.ConsistentDatabaseProjection
import com.dreweaster.ddd.kestrel.infrastructure.rdbms.Database
import com.dreweaster.ddd.kestrel.infrastructure.rdbms.ResultRow
import kestrel.example.application.readmodel.user.UserDTO
import kestrel.example.application.readmodel.user.UserReadModel
import kestrel.example.domain.aggregates.user.*

import reactor.core.publisher.Mono
import javax.inject.Singleton

interface AggregatePublicEventContractState: Persistable
interface AggregatePublicEventContractEvent: Persistable
interface AggregatePublicEventContract<E: AggregatePublicEventContractEvent, S: AggregatePublicEventContractState>

sealed class UserPublicEventContractEvent: AggregatePublicEventContractEvent
data class UserAccountLocked(val username: String): UserPublicEventContractEvent()

data class UserPublicEventContractState(
        val username: String = "",
        val password: String = "",
        val locked: Boolean = false): AggregatePublicEventContractState

object UserPublicEventContract: AggregatePublicEventContract<UserPublicEventContractEvent, UserPublicEventContractState>

class UserEventContractPublisher : AggregatePublicEventContractPublisher<User, UserEvent, UserPublicEventContract> {

    val initialState = UserPublicEventContractState()

    val handleEvent = publisher {

        event<UserRegistered> {
            update { event, state -> state.copy(username = event.username, password = event.password) }
        }

        event<UsernameChanged> {
            update { event, state -> state.copy(username = event.username) }
        }

        event<PasswordChanged> {
            update { event, state -> state.copy(password = event.password) }
        }

        event<UserLocked> {
            update { event, state -> state.copy(locked = true) }
            publish { state -> UserAccountLocked(state.username) }
        }

        event<UserUnlocked> {
            update { event, state -> state.copy(locked = false) }
        }
    }
}

@Singleton
class ConsistentUserProjection constructor(private val database: Database): ConsistentDatabaseProjection(), UserReadModel {

    private val userDtoMapper: (ResultRow) -> UserDTO = {
        UserDTO(
            id = it["id"].string,
            username = it["username"].string,
            password = it["password"].string,
            locked = it["locked"].bool
        )
    }

    override val update = projection<User, UserEvent> {

        event<UserRegistered> { e ->
            statement("INSERT into usr (id, username, password, locked) VALUES (:id, :username, :password, :locked)") {
                this["id"] = e.aggregateId.value
                this["username"] = e.rawEvent.username
                this["password"] = e.rawEvent.password
                this["locked"] = false
            }
        }

        event<UsernameChanged> { e ->
            statement("UPDATE usr SET username = :username WHERE id = :id") {
                this["id"] = e.aggregateId.value
                this["username"] = e.rawEvent.username
            }.expect(1)
        }

        event<PasswordChanged> { e ->
            statement("UPDATE usr SET password = :password WHERE id = :id") {
                this["id"] = e.aggregateId.value
                this["password"] = e.rawEvent.password
            }.expect(1)
        }

        event<UserLocked> { e ->
            statement("UPDATE usr SET locked = :locked WHERE id = :id") {
                this["id"] = e.aggregateId.value
                this["locked"] = true
            }.expect(1)
        }

        event<UserUnlocked> { e ->
            statement("UPDATE usr SET locked = :locked WHERE id = :id") {
                this["id"] = e.aggregateId.value
                this["locked"] = false
            }.expect(1)
        }
    }

    override fun findAllUsers(): Mono<List<UserDTO>> = database.withContext { tx ->
        tx.select("SELECT * from usr") { userDtoMapper(it) }
    }.collectList()

    override fun findUserById(id: String): Mono<UserDTO> = Mono.from(database.withContext { tx ->
        tx.select("SELECT * from usr where id = :id", "id" to id) { userDtoMapper(it) }
    })
}