package com.zelyder.mediaclient.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.MediaStoreSignature
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.snackbar.Snackbar
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.zelyder.mediaclient.R
import com.zelyder.mediaclient.data.BASE_URL
import com.zelyder.mediaclient.data.CACHED_IMAGE_NAME
import com.zelyder.mediaclient.data.CURRENT_FRAGMENT
import com.zelyder.mediaclient.data.PLAYER_FRAGMENT
import com.zelyder.mediaclient.ui.core.GlideApp
import com.zelyder.mediaclient.viewModelFactoryProvider
import java.io.File
import java.lang.Thread.sleep
import java.net.SocketTimeoutException
import java.util.*
import kotlin.concurrent.thread


class PlayerFragment : Fragment() {

    companion object {
        private const val TAG = "PlayerFragment"
        private const val REFRESH_EVENT = "Refresh"
        private const val CHANGE_BG_EVENT = "ChangeBackground"
        private const val PING_EVENT = "Ping"

    }

    private val viewModel: PlayerViewModel by viewModels { viewModelFactoryProvider().viewModelFactory() }
    private val args: PlayerFragmentArgs by navArgs()

    private var rootView: FrameLayout? = null
    private var playerView: PlayerView? = null
    private var imageView: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var player: SimpleExoPlayer? = null
    private var url = ""
    private var isVideo = false
    private var t1: Thread? = null
    private var lastModified = Calendar.getInstance().timeInMillis

    private lateinit var hubConnection: HubConnection
    private var snackbar: Snackbar? = null

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

        rootView = view.findViewById(R.id.root)
        playerView = view.findViewById(R.id.video_view)
        imageView = view.findViewById(R.id.ivContent)
        progressBar = view.findViewById(R.id.progressBar)

        viewModel.media.observe(this.viewLifecycleOwner) {
            url = it.url
            if (it.type == "img" || it.type == "gif") {
                isVideo = false
                lastModified = Calendar.getInstance().timeInMillis
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
                switchToImage()
                initializeCashedImage()
                if (hubConnection.connectionState == HubConnectionState.DISCONNECTED) {
                    Log.d(TAG, "try to reconnect in connection changed")
                    launchConnectionLoop()
                }
            } else if (t1 != null && t1!!.isAlive) {
                t1?.interrupt()
            }
        }
        viewModel.bgUrl.observe(this.viewLifecycleOwner) { bgUrl ->
            downloadImage(bgUrl)
        }
        viewModel.snackMsg.observe(this.viewLifecycleOwner) { msg ->
            if (msg != null) {
                snackbar = showSnackMsg(msg, Snackbar.LENGTH_SHORT)
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
            progressBar?.visibility = View.VISIBLE
            GlideApp.with(this)
                .load(url)
                .error(R.drawable.ic_close)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar?.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar?.visibility = View.GONE
                        return false
                    }

                })
                .skipMemoryCache(true)
                .thumbnail(0.25f)
                .signature(MediaStoreSignature("img", lastModified, 0))
                .into(imageView!!)

        }
    }

    private fun initializeCashedImage() {
        if (imageView != null) {
            GlideApp.with(this)
                .load(File("/storage/emulated/0/Pictures", CACHED_IMAGE_NAME))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_close)
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

    private fun launchConnectionLoop() {
        val uiHandler = Handler(Looper.getMainLooper())

        t1 = thread {
            while (hubConnection.connectionState == HubConnectionState.DISCONNECTED) {
                try {
                    hubConnection.stop()
                    connectToSocket()
                    sleep(10000)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                    Log.d(TAG, "launchConnectionLoop failed")
                }
            }
            uiHandler.post {
                Log.d(TAG, "Connection restored!")
            }
        }


    }

    private fun connectToSocket() {
        try {
            hubConnection = HubConnectionBuilder
                .create("${BASE_URL}refresh")
                .build()

            Log.d(TAG, "try to connect")
            hubConnection.on(
                PING_EVENT,
                { message: String ->
                    Log.d(TAG, "Socket event: $PING_EVENT \n message $message")
                    if(message.toInt() == args.screenId) {
                        hubConnection.send(PING_EVENT, "${args.screenId} OK")
                    }
                },
                String::class.java
            )
            hubConnection.on(
                REFRESH_EVENT,
                { message: String ->
                    Log.d(TAG, "Socket event: $REFRESH_EVENT \n message $message")
                    if (message.toInt() == args.screenId || message.toInt() == 0) {
                        viewModel.updateMedia(args.screenId)
                    }
                },
                String::class.java
            )
            hubConnection.on(
                CHANGE_BG_EVENT,
                { message: String ->
                    Log.d(TAG, "Socket event: $CHANGE_BG_EVENT \n message: $message")
                    if (message.toInt() == args.screenId || message.toInt() == 0) {
                        Log.d(TAG, "Socket event applied: $CHANGE_BG_EVENT \n message: $message")
                        viewModel.updateBgImage(args.screenId)
                    }
                },
                String::class.java
            )
            hubConnection.start()
            hubConnection.onClosed {
                Log.d(TAG, "connection lost state = ${hubConnection.connectionState}")
                Log.d(TAG, "try to reconnect in hub Connection")
                launchConnectionLoop()
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

    private fun downloadImage(imageURL: String) {
        if (!verifyPermissions()) {
            return
        }
        Glide.with(this)
            .load(imageURL)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    @NonNull resource: Drawable,
                    @Nullable transition: Transition<in Drawable?>?
                ) {
                    val bitmap = (resource as BitmapDrawable).bitmap
                    Log.d(TAG, "Saving Image...")
                    viewModel.saveImage(
                        bitmap,
                        File("/storage/emulated/0/Pictures"),
                        CACHED_IMAGE_NAME
                    )
                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                    Log.d(TAG, "onLoadCleared")
                }
                override fun onLoadFailed(@Nullable errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Log.d(TAG, "Failed to Download Image! Please try again later.")
                }
            })
    }

    private fun showSnackMsg(msg: String, duration: Int): Snackbar {
        val snackbar = Snackbar.make(this.rootView!!, msg, duration)
        snackbar.show()
        return snackbar
    }


    private fun verifyPermissions(): Boolean {

        // This will return the current Status
        val permissionExternalMemory =
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            val storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(requireActivity(), storagePermissions, 1)
            return false
        }
        return true
    }


}
