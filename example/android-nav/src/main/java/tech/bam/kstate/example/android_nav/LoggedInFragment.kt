package tech.bam.kstate.example.android_nav

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

class LoggedInFragment : Fragment(R.layout.fragment_layout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.button)
        button.text = "Logout"
        button.setOnClickListener {
            (activity as MainActivity).navigator.logout()
        }
    }
}