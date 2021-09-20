package com.zelyder.mediaclient.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zelyder.mediaclient.MyApp
import com.zelyder.mediaclient.R
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.CURRENT_FRAGMENT
import com.zelyder.mediaclient.data.SETTINGS_FRAGMENT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi


class ScreenIdFragment : Fragment() {

    private lateinit var etScreenId: EditText
    private lateinit var etIp: EditText
    private lateinit var btnOk: Button

    private lateinit var sharePrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CURRENT_FRAGMENT = SETTINGS_FRAGMENT
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screen_id, container, false)
    }

    @ExperimentalSerializationApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etScreenId = view.findViewById(R.id.etScreenId)
        etIp = view.findViewById(R.id.etIp)
        btnOk = view.findViewById(R.id.btnScreenId)

        sharePrefs = requireContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)


        val screenId = sharePrefs.getInt(KEY_SCREEN_ID, 0)
        val screenIp: String = sharePrefs.getString(KEY_SCREEN_IP, BASE_URL)!!


        if (savedInstanceState == null) {
            if (screenId in 1..6) {
                etScreenId.setText(screenId.toString())
            }
            etIp.setText(screenIp)
            btnOk.requestFocus()
        }


        btnOk.setOnClickListener {
            val text = etScreenId.text.toString()
            (this.activity?.application as MyApp).updateIp(etIp.text.toString())
            if (text.isNotEmpty() && text.isDigitsOnly() && text.toIntOrNull() != null && text.toIntOrNull() in 1..6) {
                val id = text.toInt()
                btnOk.isActivated = false
                GlobalScope.launch(Dispatchers.Main) {
                    toPlayerFragment(id, etIp.text.toString())
                }


            } else {
                etScreenId.error = getString(R.string.screen_id_et_error_msg)
            }
        }
    }

    private fun toPlayerFragment(id: Int, ip: String) {
        val editor = sharePrefs.edit()
        findNavController().navigate(
            ScreenIdFragmentDirections.actionScreenIdFragmentToPlayerFragment(
                id,
                ip
            )
        )
        editor.putInt(KEY_SCREEN_ID, id)
        editor.putString(KEY_SCREEN_IP, ip)
        editor.apply()
    }
}

const val SHARED_PREF_NAME = "preferences"
const val KEY_SCREEN_ID = "pref_screen_id"
const val KEY_SCREEN_IP = "pref_screen_ip"