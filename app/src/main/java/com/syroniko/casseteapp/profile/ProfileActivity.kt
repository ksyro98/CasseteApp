package com.syroniko.casseteapp.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FieldValue
import com.syroniko.casseteapp.R
import com.syroniko.casseteapp.databinding.ActivityProfileBinding
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.mainClasses.toast
import com.syroniko.casseteapp.utils.addImage
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.uid = intent.getStringExtra(PROFILE_UID_EXTRA) ?: return
        viewModel.currentUid = intent.getStringExtra(PROFILE_CURRENT_UID_EXTRA) ?: return
        activity_send_message_tv.isEnabled = intent.getBooleanExtra(PROFILE_SEND_MESSAGE_ENABLED_EXTRA, false)

        addImage(this, viewModel.uid, activity_profile_image)

        activity_add_friend_tv.setOnClickListener {
            if (viewModel.isFriend.value == false){
                UserDB.update(viewModel.currentUid, hashMapOf(Pair("friends", FieldValue.arrayUnion(viewModel.uid))))
                UserDB.update(viewModel.uid, hashMapOf(Pair("friends", FieldValue.arrayUnion(viewModel.currentUid))))
                viewModel.user.value?.friends?.add(viewModel.currentUid)
                viewModel.isFriend.value = true
                binding.invalidateAll()
            }
        }

        activity_profile_image.setOnClickListener {
            FullScreenImageActivity.startActivity(this, viewModel.uid)
        }

        activity_about_user_tv.setOnClickListener {
            val bottomSheet = BioBottomSheetFragment()
            bottomSheet.show(supportFragmentManager, "ModalBottomSheet")
        }
    }

    companion object {
        private const val PROFILE_UID_EXTRA = "profile_user_extra"
        private const val PROFILE_CURRENT_UID_EXTRA = "profile_current_uid_extra"
        private const val PROFILE_SEND_MESSAGE_ENABLED_EXTRA = "profile_send_message_enabled_extra"

        fun startActivity(context: Context, uid: String, currentUid: String, isSendMessageEnabled: Boolean = false){
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra(PROFILE_UID_EXTRA, uid)
            intent.putExtra(PROFILE_CURRENT_UID_EXTRA, currentUid)
            intent.putExtra(PROFILE_SEND_MESSAGE_ENABLED_EXTRA, isSendMessageEnabled)
            context.startActivity(intent)
        }
    }
}