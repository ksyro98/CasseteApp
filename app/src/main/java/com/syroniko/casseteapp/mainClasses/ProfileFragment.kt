package com.syroniko.casseteapp.mainClasses

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.databinding.FragmentProfileBinding
import java.io.*

const val REQUEST_IMAGE_CAPTURE = 1
const val FILE_NAME = "versions.txt"

class ProfileFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var imageView: ImageView
    private lateinit var profileRef: StorageReference
    private var version: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this.viewLifecycleOwner

        imageView = view.findViewById(R.id.fragment_profile_image)

        binding.viewModel = viewModel

        version = readFromVersionFile()

        profileRef = Firebase.storage.reference.child("images/${viewModel.uid}.jpg")
        Glide.with(requireContext())
            .load(profileRef)
            .circleCrop()
            .signature(MediaStoreSignature("image/jpeg", version.toLong(), 0))
            .into(imageView)

        imageView.setOnClickListener {
            dispatchTakePictureIntent()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap

            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val bitmapData = baos.toByteArray()
            val uploadTask = profileRef.putBytes(bitmapData)
            uploadTask
                .addOnSuccessListener {
                    activity?.toast("Your profile image was updated successfully.")

                    version++
                    writeToVersionFile(version)

                    Glide.with(requireContext())
                        .load(imageBitmap)
                        .circleCrop()
                        .signature(MediaStoreSignature("image/jpeg", version.toLong(), 0))
                        .into(imageView)
                }
                .addOnFailureListener {
                    activity?.toast("A problem occurred while updating your profile image.")
                }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            activity?.longToast(
                "A problem occurred while opening your camera. " +
                        "Please make sure you have granted camera permissions to the app."
            )
        }
    }


    private fun writeToVersionFile(version: Int){
        try {
            val outputStreamWriter = OutputStreamWriter(
                requireContext().openFileOutput(
                    FILE_NAME,
                    MODE_PRIVATE
                )
            )
            outputStreamWriter.write(version.toString())
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e(ProfileFragment::class.simpleName, "Error while writing to $FILE_NAME", e)
        }
    }

    private fun readFromVersionFile(): Int {
        try {
            val inputStream = requireContext().openFileInput(FILE_NAME)

            if (inputStream != null){
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()

                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append(receiveString)
                }

                inputStream.close()
                version = stringBuilder.toString().toInt()
            }
        }
        catch (e: FileNotFoundException) {
            Log.e(ProfileFragment::class.simpleName, "File not found: $FILE_NAME", e)
        } catch (e: IOException) {
            Log.e(ProfileFragment::class.simpleName, "Cannot read file $FILE_NAME", e)
        }

        return version
    }

}