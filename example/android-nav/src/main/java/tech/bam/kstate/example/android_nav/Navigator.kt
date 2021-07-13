package tech.bam.kstate.example.android_nav

import androidx.fragment.app.Fragment
import tech.bam.kstate.core.*

interface ScreenInterface {
    val fragment: Class<out Fragment>
}

class LoggedInContext(val userId: String) : Context

class Screen(override val fragment: Class<out Fragment>) : StateId, ScreenInterface
class ScreenWithContext<C : Context>(override val fragment: Class<out Fragment>) :
    StateIdWithContext<C>, ScreenInterface

val Welcome = Screen(WelcomeFragment::class.java)
val LoggedIn = ScreenWithContext<LoggedInContext>(LoggedInFragment::class.java)

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
        machine.onTransition { _, _ ->
            val fragmentTransaction = mainActivity.supportFragmentManager.beginTransaction()
            val currentStateId = machine.currentStateId
            if (currentStateId is ScreenInterface) {
                fragmentTransaction.replace(R.id.root, currentStateId.fragment, null)
            }
            fragmentTransaction.commit()
        }
    }
}