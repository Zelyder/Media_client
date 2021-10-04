package com.zelyder.mediaclient.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.signature.ObjectKey
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.zelyder.mediaclient.R
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.CURRENT_FRAGMENT
import com.zelyder.mediaclient.data.PLAYER_FRAGMENT
import com.zelyder.mediaclient.ui.core.GlideApp
import com.zelyder.mediaclient.viewModelFactoryProvider
import java.net.SocketTimeoutException
import java.util.*


class PlayerFragment : Fragment() {

    companion object {
        private const val TAG = "PlayerFragment"
    }

    private val viewModel: PlayerViewModel by viewModels { viewModelFactoryProvider().viewModelFactory() }
    private val args: PlayerFragmentArgs by navArgs()

    private var playerView: PlayerView? = null
    private var imageView: ImageView? = null
    private var player: SimpleExoPlayer? = null
    private var url = ""
    private var isVideo = false

    private lateinit var hubConnection: HubConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CURRENT_FRAGMENT = PLAYER_FRAGMENT
        // live update
        connectToSocket()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerView = view.findViewById(R.id.video_view)
        imageView = view.findViewById(R.id.ivContent)

        viewModel.media.observe(this.viewLifecycleOwner) {
            url = it.url
            if (it.type == "img" || it.type == "gif") {
                isVideo = false
                switchToImage()
                initializeImage()
            } else if (it.type == "vid") {
                isVideo = true
                switchToVideo()
                initializePlayer()
            }
        }
        viewModel.connection.observe(this.viewLifecycleOwner) { connected ->
            if (!connected) {
                Toast.makeText(
                    requireContext(),
                    "Ошибка подключения! Удостоверьтесь в подключении кабеля и правильности url",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        viewModel.updateMedia(args.screenId)
    }

    override fun onStart() {
        super.onStart()
        CURRENT_FRAGMENT = PLAYER_FRAGMENT
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    override fun onStop() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(KEY_IS_FIRST_OPEN, false)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        hubConnection.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        releaseImage()
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(requireContext()).build()
        playerView?.player = player

        val mediaItem: MediaItem = MediaItem.fromUri(url)
        player?.apply {
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
            prepare()
        }
    }


    private fun initializeImage() {
        if (imageView != null) {
            GlideApp.with(this)
                .load(url)
                .error(R.drawable.ic_close)
//                .skipMemoryCache(true)
                .signature(ObjectKey(Calendar.getInstance().time))
                .into(imageView!!)

        }
    }

    private fun releasePlayer() {
        if (player != null) {
            player?.release()
            player = null
        }
    }

    private fun releaseImage() {
        if (imageView != null) {
            GlideApp.with(this).clear(imageView!!)
            imageView = null
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private fun switchToVideo() {
        releasePlayer()
        imageView?.visibility = View.GONE
        playerView?.visibility = View.VISIBLE
    }

    private fun switchToImage() {
        imageView?.visibility = View.VISIBLE
        playerView?.visibility = View.GONE
        releasePlayer()
    }

    private fun connectToSocket() {
        try {
            hubConnection = HubConnectionBuilder
                .create("${BASE_URL}refresh")
                .build()


            Log.d(TAG, "connection OK")
            hubConnection.on(
                "Refresh",
                { message: String ->
                    if (message.toInt() == args.screenId || message.toInt() == 0) {
                        viewModel.updateMedia(args.screenId)
                    }
                },
                String::class.java
            )
            hubConnection.start()
            hubConnection.onClosed {
                Log.d(TAG, "connection lost")
            }
        } catch (ex: SocketTimeoutException) {
            Log.d(TAG, resources.getText(R.string.connection_exception).toString())
            Toast.makeText(
                requireContext(),
                resources.getText(R.string.connection_exception),
                Toast.LENGTH_LONG
            ).show()
        } catch (ex: Exception) {
            Log.d(TAG, resources.getText(R.string.unexpected_error).toString())
            Toast.makeText(
                requireContext(),
                resources.getText(R.string.unexpected_error),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
