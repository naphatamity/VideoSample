package com.videoment.videoexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.videoment.videoexample.videoplayer.HomeVideoFragment
import org.json.JSONObject

class MainActivity : AppCompatActivity(R.layout.video_comment) {

    val VIDEO_URL = "videoUrl"
    val VIDEO_NAME = "videoName"
    val VIDEO_COUNT = "videoCount"
    val CHANNEL_ID = "channelID"

    var chID = ""
    var videoURL = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
    var videoName = "Video Rabbit"
    var videoCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(savedInstanceState)
    }

    private fun initFragment(savedInstanceState: Bundle?) {
        val channelObject = JSONObject()
        channelObject.put(VIDEO_URL, videoURL)
        channelObject.put(VIDEO_NAME, videoName)
        channelObject.put(VIDEO_COUNT, videoCount)
        channelObject.put(CHANNEL_ID, chID)

        if (savedInstanceState == null) {
            val videoFragment = newInstance(channelObject, HomeVideoFragment())
            addFragment(videoFragment, R.id.videoFragment)
        }
    }

    private fun newInstance(channelObject: JSONObject, frag: Fragment): Fragment {
        val args = Bundle()
        args.putString(VIDEO_URL, channelObject.getString(VIDEO_URL))
        args.putString(VIDEO_NAME, channelObject.getString(VIDEO_NAME))
        args.putInt(VIDEO_COUNT, channelObject.getInt(VIDEO_COUNT))
        args.putString(CHANNEL_ID, channelObject.getString(CHANNEL_ID))
        frag.arguments = args
        return frag
    }

    private fun addFragment(fragment: Fragment, viewID: Int) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.design_bottom_sheet_slide_in,
                R.anim.design_bottom_sheet_slide_out
            )
            .replace(
                viewID,
                fragment,
                fragment.javaClass.simpleName
            )
            .commit()
    }
}