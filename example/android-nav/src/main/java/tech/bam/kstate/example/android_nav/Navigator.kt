package tech.bam.kstate.example.android_nav

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import tech.bam.kstate.android.navigation.FragmentFactory
import tech.bam.kstate.android.navigation.createNavigationMachine
import tech.bam.kstate.core.Event
import tech.bam.kstate.core.Type
import tech.bam.kstate.core.domain.types.StateId

object Welcome : StateId
object LoggedIn : StateId
object Base : StateId
object Dialog : StateId
object Gone : StateId
object Visible : StateId

class LogIn(val userId: String) : Event
object LogOut : Event
object Hide : Event


class Navigator(private val mainActivity: MainActivity) {
    private val machine = createNavigationMachine(type = Type.Parallel) {
        state(Base) {
            initial(Welcome)
            a()
            state(Welcome) {
                context { WelcomeFragment() }
                transition(
                    on = LogIn::class,
                    target = LoggedIn,
                    effect = { event -> { LoggedInFragment(event.userId) } })
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
                    effect = { event -> { VisibleDialogFragment(event.userId) } })
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
            val fragmentFactory = next.last { it.context is FragmentFactory }.context
            val fragment = fragmentFactory?.let { it() }
            if (fragment is DialogFragment) {
                fragment.show(mainActivity.supportFragmentManager, fragment.tag)
            } else if (fragment is Fragment)
                fragmentTransaction.replace(
                    R.id.root,
                    fragment
                )
            fragmentTransaction.commitAllowingStateLoss()
        }

        mainActivity.supportFragmentManager.beginTransaction().also {
            val fragmentTransaction = mainActivity.supportFragmentManager.beginTransaction()
            val fragmentFactory =
                machine.activeStateIdsWithContext().last { it.context is FragmentFactory }.context
            val fragment = fragmentFactory?.let { it() }
            if (fragment is DialogFragment) {
                fragment.show(mainActivity.supportFragmentManager, fragment.tag)
            } else if (fragment is Fragment)
                fragmentTransaction.replace(
                    R.id.root,
                    fragment
                )
            fragmentTransaction.commitAllowingStateLoss()
        }
    }
}
