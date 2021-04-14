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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.squareup.picasso.Picasso
import com.zelyder.mediaclient.MyApp
import com.zelyder.mediaclient.R
import com.zelyder.mediaclient.data.MEDIA_BASE_URL
import com.zelyder.mediaclient.viewModelFactoryProvider
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject


class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModels { viewModelFactoryProvider().viewModelFactory() }
    private val args: PlayerFragmentArgs by navArgs()

    private var playerView: PlayerView? = null
    private var imageView: ImageView? = null
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var url = ""
    private var isVideo = false

    private lateinit var mSocket: Socket
    private lateinit var onNewMessage: Emitter.Listener
    private val refreshEvent = "screen refresh"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // live update
        val instance = requireActivity().application as MyApp
        val mSocket: Socket = instance.getSocketInstance()
        val onNewMessage = Emitter.Listener { args ->
            activity?.runOnUiThread(Runnable {
                val data = args[0] as JSONObject
                try {
                    viewModel.updateMedia(this.args.screenId)
                    Log.d("LOL", data.toString())
                } catch (e: JSONException) {
                    return@Runnable
                }
            })
        }

        mSocket.on(refreshEvent, onNewMessage)
        mSocket.connect()

        if (mSocket.connected()){
            Toast.makeText(requireContext(), "Socket Connected!!", Toast.LENGTH_SHORT).show()
        }
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
//        url = "${MEDIA_BASE_URL}${args.screenId}"

        playerView = view.findViewById(R.id.video_view)
        imageView = view.findViewById(R.id.ivContent)

        viewModel.media.observe(this.viewLifecycleOwner) {
            url = it.url
            if(it.type == "image") {
                isVideo = false
                switchToImage()
               initializeImage()
            }else if (it.type == "video") {
                isVideo = true
                switchToVideo()
                initializePlayer()
            }
        }



        viewModel.updateMedia(args.screenId)

    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    //    override fun onStart() {
//        super.onStart()
//        if (isVideo) {
//            switchToVideo()
//            if (Util.SDK_INT > 23) {
//                initializePlayer()
//            }
//        } else {
//            switchToImage()
//            initializeImage()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        hideSystemUi()
//        if (isVideo) {
//            if (Util.SDK_INT <= 23 || player == null) {
//                initializePlayer()
//            }
//        }
//    }
//
//
//
//    override fun onPause() {
//        super.onPause()
//        if (Util.SDK_INT <= 23 && isVideo) {
//            releasePlayer()
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        if (isVideo) {
//            if (Util.SDK_INT > 23) {
//                releasePlayer()
//            }
//        } else if (imageView != null) {
//            Glide.with(this).clear(imageView!!)
//            imageView = null
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        releaseImage()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
        mSocket.off(refreshEvent, onNewMessage)
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(requireContext()).build()
        playerView?.player = player
        val mediaItem: MediaItem = MediaItem.fromUri(url)
        player?.apply {
            setMediaItem(mediaItem)
            playWhenReady = playWhenReady
            seekTo(currentWindow, playbackPosition)
            prepare()
        }
    }


    private fun initializeImage() {
        if (imageView != null) {
            Glide.with(this)
                .load(url)
                .error(R.drawable.ic_close)
                .placeholder(R.drawable.logo)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //.override(600, 200)
                .into(imageView!!)
//            Picasso.get()
//                .load(url)
//                .placeholder(R.drawable.logo)
//                .into(imageView)
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player?.release()
            player = null
        }
    }

    private fun releaseImage() {
        if (imageView != null) {
            Glide.with(this).clear(imageView!!)
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
        imageView?.visibility = View.GONE
        playerView?.visibility = View.VISIBLE
    }

    private fun switchToImage() {
        imageView?.visibility = View.VISIBLE
        playerView?.visibility = View.GONE
    }
}
