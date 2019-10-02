package kestrel.example.infrastructure.driving.http.controllers

import com.dreweaster.ddd.kestrel.application.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponse.*
import io.micronaut.http.HttpStatus.*

import io.micronaut.http.MediaType.*
import io.micronaut.http.annotation.*
import kestrel.example.application.readmodel.user.UserDTO
import kestrel.example.application.readmodel.user.UserReadModel
import kestrel.example.domain.aggregates.user.RegisterUser
import kestrel.example.domain.aggregates.user.User
import reactor.core.publisher.Mono
import javax.inject.Inject

data class RegisterUserRequest(val username: String, val password: String)

@Controller("/users")
class UsersController @Inject constructor(private val domainModel: DomainModel, private val readModel: UserReadModel) {

    @Post(consumes = [APPLICATION_JSON], produces = [APPLICATION_JSON])
    fun create(@Body request: Mono<RegisterUserRequest>): Mono<HttpResponse<AggregateId>> {
        return request.flatMap { registerUserRequest ->
            val userId = AggregateId()
            val user = domainModel.aggregateRootOf(User, userId)
            user.handleCommand(RegisterUser(registerUserRequest.username, registerUserRequest.password)).map { result ->
                when(result) {
                    is SuccessResult -> created(userId)
                    is RejectionResult -> badRequest()
                    is ConcurrentModificationResult -> status(CONFLICT)
                    is UnexpectedExceptionResult -> serverError()
                }
            }
        }
    }

    @Get(produces = [APPLICATION_JSON])
    fun findAllUsers(): Mono<List<UserDTO>> {
        return readModel.findAllUsers()
    }

    @Get("/{id}", produces = [APPLICATION_JSON])
    fun findUserById(@PathVariable id: String): Mono<UserDTO> {
        return readModel.findUserById(id)
    }
}
