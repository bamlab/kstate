package tech.bam.kstate.core.usecase

import tech.bam.kstate.core.State
import tech.bam.kstate.core.createParallelMachine
import tech.bam.kstate.core.domain.mock.CoffeeMachineContext
import tech.bam.kstate.core.domain.mock.CoffeeMachineEvent.POWER_ON
import tech.bam.kstate.core.domain.mock.CoffeeMachineEvent.SERVE_COFFEE
import tech.bam.kstate.core.domain.mock.CoffeeMachineStateId.*
import tech.bam.kstate.core.domain.types.SimpleStateId
import tech.bam.kstate.core.domain.types.StateId
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

object PowerAdapter : StateId<PowerContext>
data class PowerContext(var electricity: Int)

object CoffeeMachine : StateId<CoffeeMachineContext>
object Distribution : SimpleStateId

class ContextStateTest {
    private val machine = createParallelMachine(
        id = PowerAdapter,
        context = PowerContext(100)
    ) {
        parallelState(id = CoffeeMachine, context = CoffeeMachineContext(10, 10)) {
            state(id = Distribution, initial = OFF) {
                state(id = OFF) {
                    transition(on = POWER_ON, target = IDLE)
                }

                state(id = IDLE) {
                    transition(on = SERVE_COFFEE::class, target = SERVING) { event ->
                        this@parallelState.assign(
                            this@parallelState.context.copy(
                                waterStock = this@parallelState.context.waterStock - 1,
                                coffeeStock = this@parallelState.context.coffeeStock - event.coffeeAmount
                            )
                        )

                        this@createParallelMachine.assign(
                            this@createParallelMachine.context.copy(
                                electricity = this@createParallelMachine.context.electricity - 1
                            )
                        )
                    }
                }

                state(id = SERVING)
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
    fun `it saves the context`() {
        machine.send(POWER_ON)

        machine.send(SERVE_COFFEE(3))

        assertEquals(99, machine.context.electricity)
        @Suppress("UNCHECKED_CAST")
        assertEquals(9, (machine.states[0] as State<CoffeeMachineContext, *>).context.waterStock)
        @Suppress("UNCHECKED_CAST")
        assertEquals(7, (machine.states[0] as State<CoffeeMachineContext, *>).context.coffeeStock)
    }
}