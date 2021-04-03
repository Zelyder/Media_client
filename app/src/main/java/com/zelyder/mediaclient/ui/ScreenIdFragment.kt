package com.zelyder.mediaclient.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import androidx.navigation.fragment.findNavController
import com.zelyder.mediaclient.R

class ScreenIdFragment : Fragment() {

    private lateinit var etScreenId: EditText
    private lateinit var btnOk: Button

    private lateinit var sharePrefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screen_id, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etScreenId = view.findViewById(R.id.etScreenId)
        btnOk = view.findViewById(R.id.btnScreenId)

        sharePrefs = requireContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)


        val screenId = sharePrefs.getInt(KEY_SCREEN_ID, 0)

        if(screenId in 1..6 && savedInstanceState == null) {
            etScreenId.setText(screenId.toString())
        }

        btnOk.setOnClickListener {
            val text = etScreenId.text.toString()
            if (text.isNotEmpty() && text.isDigitsOnly() && text.toIntOrNull() != null && text.toIntOrNull() in 1..6) {
                val id = text.toInt()
                toPlayerFragment(id)
            } else {
                etScreenId.error = getString(R.string.screen_id_et_error_msg)
            }
        }


    }

    private fun toPlayerFragment(id: Int) {
        val editor = sharePrefs.edit()
        findNavController().navigate(
            ScreenIdFragmentDirections.actionScreenIdFragmentToPlayerFragment(
                id
            )
        )
        editor.putInt(KEY_SCREEN_ID, id)
        editor.apply()
    }
}

const val SHARED_PREF_NAME = "preferences"
const val KEY_SCREEN_ID = "pref_screen_id"