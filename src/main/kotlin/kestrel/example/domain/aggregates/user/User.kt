package kestrel.example.domain.aggregates.user

import com.dreweaster.ddd.kestrel.domain.Aggregate

object User : Aggregate<UserCommand, UserEvent, UserState> {

    override val blueprint =

        aggregate("user") {

            edenBehaviour {

                receive {
                    command<RegisterUser> { (username, password) -> accept(UserRegistered(username, password)) }
                }

                apply {
                    event<UserRegistered> { (username, password) -> ActiveUser(username, password) }
                }
            }

            behaviour<ActiveUser> {

                receive {
                    command<ChangePassword> { currentState, (newPassword) -> accept(PasswordChanged(currentState.password, newPassword)) }
                    command<ChangeUsername> { _, (username) -> accept(UsernameChanged(username)) }
                    command<Login> { (_, password, failedLoginAttempts), cmd ->
                        when (cmd.password) {
                            password -> accept()
                            else -> when(failedLoginAttempts) {
                                in 0..2 -> accept(FailedLoginAttemptsIncremented)
                                else -> accept(FailedLoginAttemptsIncremented, UserLocked)
                            }
                        }
                    }
                }

                apply {
                    event<PasswordChanged> { currentState, (_, newPassword) -> currentState.copy(password = newPassword) }
                    event<UsernameChanged> { currentState, (username) -> currentState.copy(username = username) }
                    event<FailedLoginAttemptsIncremented> { currentState, _ -> currentState.copy(failedLoginAttempts = currentState.failedLoginAttempts + 1) }
                    event<UserLocked> { (username, password, _), _ -> LockedUser(username, password)}
                }
            }

            behaviour<LockedUser> {

                receive {
                    command<ChangePassword> { _, _ -> reject(UserIsLocked) }
                    command<ChangeUsername> { _, _ -> reject(UserIsLocked) }
                    command<Login> { _, _ -> reject(UserIsLocked) }
                    command<UnlockUser> { _, _ -> accept(UserUnlocked)}
                }

                apply {
                    event<UserUnlocked> { currentState, _ -> ActiveUser(currentState.username, currentState.password)}
                }
            }
        }
}