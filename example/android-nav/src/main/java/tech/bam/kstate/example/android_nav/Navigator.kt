package tech.bam.kstate.example.android_nav

import androidx.fragment.app.Fragment
import tech.bam.kstate.core.*

interface ScreenInterface<C : Context> {
    val fragmentFactory: (context: C) -> Fragment
}

class LoggedInContext(val userId: String) : Context

class Screen(override val fragmentFactory: (context: Context) -> Fragment) : StateId,
    ScreenInterface<Context>

class ScreenWithContext<C : Context>(override val fragmentFactory: (context: C) -> Fragment) :
    StateIdWithContext<C>, ScreenInterface<C>

val Welcome = Screen { WelcomeFragment() }
val LoggedIn = ScreenWithContext<LoggedInContext> { c -> LoggedInFragment(c.userId) }

class LogIn(val userId: String) : Event
object LogOut : Event

class Navigator(private val mainActivity: MainActivity) {
    private val machine = createMachine {
        initial(Welcome)
        state(Welcome) {
            transition(
                on = LogIn::class,
                target = LoggedIn,
                effect = { event -> LoggedInContext(event.userId) })
        }
        state(LoggedIn) {
            transition(on = LogOut, target = Welcome)
        }
    }

    fun login(userId: String) {
        machine.send(LogIn(userId))
    }

    fun logout() {
        machine.send(LogOut)
    }

    fun start() {
        machine.onTransitionWithContext { _, next ->
            val fragmentTransaction = mainActivity.supportFragmentManager.beginTransaction()
            val statePair = next.last()
            val screen = statePair.stateIdWithContext
            val context = statePair.context
            if (screen is ScreenInterface<*>) {
                fragmentTransaction.replace(
                    R.id.root,
                    @Suppress("UNCHECKED_CAST")
                    (screen as ScreenInterface<Context>).fragmentFactory(context)
                )
            }
            fragmentTransaction.commit()
        }

        mainActivity.supportFragmentManager.beginTransaction().also {
            val fragmentTransaction = mainActivity.supportFragmentManager.beginTransaction()
            val screen = machine.currentStateId
            val context = machine.context
            if (screen is ScreenInterface<*>) {
                fragmentTransaction.replace(
                    R.id.root,
                    @Suppress("UNCHECKED_CAST")
                    (screen as ScreenInterface<Context>).fragmentFactory(context)
                )
            }
            fragmentTransaction.commit()
        }
    }
}