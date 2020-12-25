package com.syroniko.casseteapp.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.syroniko.casseteapp.R


const val SPOTIFY_FRAGMENT_TAG = "SPOTIFY_DIALOG_TAG"

class SpotifyDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogMessage = "In order to have access to all of the app's features you need to connect to your Spotify account."
        val positiveText = "Let's go"
        val negativeText = "No"
        val neutralText = "Maybe later"

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(dialogMessage)
                .setPositiveButton(positiveText) { _, _ ->
                    SpotifyAuthRequest.initSpotifyAuth(requireActivity())
                    Log.d("SpotifyDialogFragment", "Positive")
                }
                .setNegativeButton(negativeText) { _, _ ->
                    denySpotifyConnection(requireActivity())
                    Log.d("SpotifyDialogFragment", "Negative")
                }
                .setNeutralButton(neutralText){ _, _ ->
                    Log.d("SpotifyDialogFragment", "Neutral")
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}