package com.videoment.videoexample.videoplayer

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.videoment.videoexample.R

class HomeVideoFragment() : Fragment(R.layout.video_player) {
    private var PLAYER_CURRENT_POS_KEY = "PLAYER_CURRENT_POS_KEY"
    private var fullScreen = 0
    var name = ""
    private var url = ""
    private var count = 0
    private var currentPosition: Long = 0
    private var playWhenReady = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        url = arguments?.getString("videoUrl") ?: ""
        name = arguments?.getString("videoName") ?: ""
        count = arguments?.getInt("videoCount") ?: 0

        if (savedInstanceState != null) {
            onViewStateRestored(savedInstanceState)
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switchToolbar()
            fullScreen = 1
        } else {
            val videoShareBtn = requireActivity().findViewById<ImageButton>(R.id.videoShareBtn)

            videoShareBtn.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share To:"))
            }
        }
    }

    private fun initialVideo() {
        val playerScreen = requireView().findViewById<PlayerView>(R.id.matchPlayer)
        val videoName = requireActivity().findViewById<TextView>(R.id.videoNameTextView)
        val videoCount = requireActivity().findViewById<TextView>(R.id.view_count)
        val playerControllerLayout =
            requireActivity().findViewById<FrameLayout>(R.id.playerController)
        val contentRelativeLayout =
            requireActivity().findViewById<RelativeLayout>(R.id.contentRelativeLayout)

        if (videoCount != null) {
            videoCount.text = count.toString()
        }
        videoName.text = name
        playerScreen.requestFocus()

        val player = SimpleExoPlayer.Builder(requireContext())
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(requireContext()).setLiveTargetOffsetMs(
                    5000
                )
            )
            .build()

        val mediaItem: MediaItem = MediaItem.Builder()
            .setUri(url)
            .setLiveMaxPlaybackSpeed(1.02f)
            .build()

        player.setMediaItem(mediaItem)
        playerScreen.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        playerScreen.player = player
        player.prepare()
        player.seekTo(currentPosition)
        player.playWhenReady = playWhenReady
        player.play()

        playerScreen.videoSurfaceView?.setOnClickListener {
            playerScreen.showController()
            playerControllerLayout.visibility = View.VISIBLE
            contentRelativeLayout.visibility = View.VISIBLE
        }

    }

    private fun switchToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.let {
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                it.hide(WindowInsets.Type.systemBars())
            }
        } else {
            val uiOptions = requireActivity().window.decorView.systemUiVisibility
            var newUiOptions = uiOptions
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            requireActivity().window.decorView.systemUiVisibility = newUiOptions
        }
    }

    override fun onPause() {
        super.onPause()
        val playerScreen = requireView().findViewById<PlayerView>(R.id.matchPlayer)
        currentPosition = playerScreen.player?.currentPosition!!
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val playerScreen = requireView().findViewById<PlayerView>(R.id.matchPlayer)
        super.onSaveInstanceState(outState)
        outState.putLong(
            PLAYER_CURRENT_POS_KEY,
            currentPosition
        )
        playerScreen.player?.stop()
        playerScreen.player?.release()
        playerScreen.player = null
    }

    override fun onResume() {
        super.onResume()
        val playerScreen = requireView().findViewById<PlayerView>(R.id.matchPlayer)
        if(playerScreen.player == null)
            initialVideo()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            currentPosition = (it.getLong(PLAYER_CURRENT_POS_KEY))
        }
    }

    operator fun invoke(): Fragment {
        return this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val playerScreen = requireView().findViewById<PlayerView>(R.id.matchPlayer)
        playerScreen.player?.stop()
        playerScreen.player?.release()
        playerScreen.player = null
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        val frameLayout = requireActivity().findViewById<FrameLayout>(R.id.playerController)
        if (isInPictureInPictureMode) {
            frameLayout.visibility = View.GONE
        }else{
            frameLayout.visibility = View.VISIBLE
        }
    }

}