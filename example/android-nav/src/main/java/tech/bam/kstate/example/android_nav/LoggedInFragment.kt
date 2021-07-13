package tech.bam.kstate.example.android_nav

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class LoggedInFragment(private val userId: String) : Fragment(R.layout.fragment_layout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.button)
        button.text = "Hello $userId ! Logout ?"
        button.setOnClickListener {
            (activity as MainActivity).navigator.logout()
        }
    }
}