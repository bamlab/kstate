package tech.bam.kstate.example.android_nav

import androidx.fragment.app.DialogFragment
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
object Hide : Event

object Base : StateId
object Dialog : StateId

object Gone : StateId

val Visible = ScreenWithContext<LoggedInContext> { c -> VisibleDialogFragment(c.userId) }

class Navigator(private val mainActivity: MainActivity) {
    private val machine = createMachine(type = Type.Parallel) {
        state(Base) {
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
        state(Dialog) {
            initial(Gone)
            state(Gone) {
                transition(
                    on = LogIn::class,
                    target = Visible,
                    effect = { event -> LoggedInContext(event.userId) })
            }
            state(Visible) {
                transition(on = Hide, target = Gone)
            }
        }
    }

    fun login(userId: String) {
        machine.send(LogIn(userId))
    }

    fun logout() {
        machine.send(LogOut)
    }

    fun hide() {
        machine.send(Hide)
    }

    fun start() {
        machine.onTransitionWithContext { _, next ->
            val fragmentTransaction = mainActivity.supportFragmentManager.beginTransaction()
            val statePair = next.last { it.stateIdWithContext is ScreenInterface<*> }
            val screen = statePair.stateIdWithContext
            val context = statePair.context
            if (screen is ScreenInterface<*>) {
                @Suppress("UNCHECKED_CAST")
                val fragment = (screen as ScreenInterface<Context>).fragmentFactory(context)
                if (fragment is DialogFragment) {
                    fragment.show(mainActivity.supportFragmentManager, fragment.tag)
                } else
                    fragmentTransaction.replace(
                        R.id.root,
                        fragment
                    )
            }
            fragmentTransaction.commitAllowingStateLoss()
        }

        mainActivity.supportFragmentManager.beginTransaction().also {
            val next = machine.activeStateIdsWithContext()
            val fragmentTransaction = mainActivity.supportFragmentManager.beginTransaction()
            val statePair = next.last { it.stateIdWithContext is ScreenInterface<*> }
            val screen = statePair.stateIdWithContext
            val context = statePair.context
            if (screen is ScreenInterface<*>) {
                fragmentTransaction.replace(
                    R.id.root,
                    @Suppress("UNCHECKED_CAST")
                    (screen as ScreenInterface<Context>).fragmentFactory(context)
                )
            }
            fragmentTransaction.commitAllowingStateLoss()
        }
    }
}