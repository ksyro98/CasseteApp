package com.syroniko.casseteapp.profile

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.MediaStoreSignature
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.databinding.FragmentProfileBinding
import com.syroniko.casseteapp.mainClasses.MainViewModel
import com.syroniko.casseteapp.mainClasses.longToast
import com.syroniko.casseteapp.mainClasses.toast
import com.syroniko.casseteapp.utils.addImage
import java.io.*

const val REQUEST_IMAGE_CAPTURE = 1
const val REQUEST_FILE = 2
const val FILE_NAME = "versions.txt"

class ProfileFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var imageView: ImageView
    private lateinit var profileRef: StorageReference
    private lateinit var aboutUserTextView: TextView
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
        aboutUserTextView = view.findViewById(R.id.about_user_tv)

        binding.viewModel = viewModel

        profileRef = Firebase.storage.reference.child("images/${viewModel.uid}.jpg")

        version = readFromVersionFile()
        addImage(requireContext(), viewModel.uid, imageView, cropCircle = true, useSignature = true, version = version.toLong())

        imageView.setOnClickListener {
            val bottomSheet = PhotoPickerBottomSheetFragment { which ->
                when (which) {
                    0 -> openFile()
                    1 -> dispatchTakePictureIntent()
                }
            }
            bottomSheet.show(requireActivity().supportFragmentManager, "ModalBottomSheet")
        }

        aboutUserTextView.setOnClickListener {
            val bottomSheet = BioBottomSheetFragment()
            bottomSheet.show(requireActivity().supportFragmentManager, "ModalBottomSheet")
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultIntent)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = resultIntent?.extras?.get("data") as Bitmap
            updateUserImage(imageBitmap, viewModel.user.name)
        }
        else if (requestCode == REQUEST_FILE && resultCode == RESULT_OK){
            val uri = resultIntent?.data ?: return

            val pfd = requireActivity().contentResolver.openFileDescriptor(uri, "r")
            val fd = pfd?.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fd)
            pfd?.close()

            updateUserImage(bitmap, viewModel.user.name)
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

    private fun openFile(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }

        startActivityForResult(intent, REQUEST_FILE)
    }

    private fun updateUserImage(imageBitmap: Bitmap, userName: String?){
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)

        val bitmapData = baos.toByteArray()
        val uploadTask = profileRef.putBytes(bitmapData)

        val metadata = storageMetadata {
            contentType = "image/jpg"
            setCustomMetadata("name", userName)
        }

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

                profileRef.updateMetadata(metadata)
            }
            .addOnFailureListener {
                activity?.toast("A problem occurred while updating your profile image.")
            }
    }

    //MOVE THOSE FUNCTIONS TO ANOTHER FILE
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