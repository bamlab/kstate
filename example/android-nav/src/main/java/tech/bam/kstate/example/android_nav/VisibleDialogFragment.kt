package tech.bam.kstate.example.android_nav

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class VisibleDialogFragment(private val userId: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?) =
        AlertDialog.Builder(requireContext())
            .setMessage("Hello $userId")
            .setPositiveButton("OK") { _, _ -> (activity as MainActivity).navigator.hide() }
            .setOnDismissListener { (activity as MainActivity).navigator.hide() }
            .create()
}
