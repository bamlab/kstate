package tech.bam.kstate.core.usecase

import tech.bam.kstate.core.createMachine
import tech.bam.kstate.core.domain.types.SimpleStateId
import tech.bam.kstate.core.domain.types.StateId
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

data class UserProfile(
    val firstname: String? = null,
    val lastname: String? = null,
    val country: String? = null,
    val email: String? = null,
    val password: String? = null
)

object UserProfileAggregate : StateId<UserProfile>

sealed class UserProfileAggregateStateIds : SimpleStateId {
    object INITIAL : UserProfileAggregateStateIds()
    object INCOMPLETE : UserProfileAggregateStateIds()
    object COMPLETE : UserProfileAggregateStateIds()
}

data class CreateUserEvent(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String
)

data class SetUserCountry(val country: String)
data class UpdateUserPassword(val password: String)

class EventSourcingTest {
    private val machine = createMachine(
        id = UserProfileAggregate,
        context = UserProfile(),
        initial = UserProfileAggregateStateIds.INITIAL
    ) {
        state(UserProfileAggregateStateIds.INITIAL) {
            transition(
                on = CreateUserEvent::class,
                target = UserProfileAggregateStateIds.INCOMPLETE,
            ) { event, context ->
                this@createMachine.assign(
                    context.copy(
                        firstname = event.firstname,
                        lastname = event.lastname,
                        email = event.email,
                        password = event.password,
                    )
                )
            }
        }

        state(UserProfileAggregateStateIds.INCOMPLETE) {
            transition(
                on = SetUserCountry::class, target = UserProfileAggregateStateIds.INCOMPLETE
            ) { event, context ->
                this@createMachine.assign(context.copy(country = event.country))
            }

            always(target = UserProfileAggregateStateIds.COMPLETE, cond = { context ->
                context.firstname != null &&
                        context.lastname != null &&
                        context.country != null &&
                        context.password != null &&
                        context.email != null
            })
        }

        state(UserProfileAggregateStateIds.COMPLETE) {
            transition(
                on = UpdateUserPassword::class,
                target = UserProfileAggregateStateIds.COMPLETE
            ) { event, context ->
                assign(context.copy(password = event.password))
            }
        }
    }

    @BeforeTest
    fun startMachine() {
        machine.start()
    }

    @AfterTest
    fun stopMachine() {
        machine.stop()
    }

    @Test
    fun `it starts in the initial state`() {
        assertEquals(UserProfileAggregateStateIds.INITIAL, machine.currentState().id)
    }

    @Test
    fun `it transition in incomplete state`() {
        machine.send(
            CreateUserEvent(
                firstname = "Joey",
                lastname = "Tribiani",
                email = "drake@daysofourlives.com",
                password = "qwerty"
            )
        )

        assertEquals(UserProfileAggregateStateIds.INCOMPLETE, machine.currentState().id)
        assertEquals("Joey", machine.context.firstname)
    }

    @Test
    fun `it transition in complete state`() {
        machine.send(
            CreateUserEvent(
                firstname = "Joey",
                lastname = "Tribiani",
                email = "drake@daysofourlives.com",
                password = "qwerty"
            )
        )

        machine.send(
            SetUserCountry("USA")
        )

        assertEquals(UserProfileAggregateStateIds.COMPLETE, machine.currentState().id)
        assertEquals("USA", machine.context.country)
    }
}
