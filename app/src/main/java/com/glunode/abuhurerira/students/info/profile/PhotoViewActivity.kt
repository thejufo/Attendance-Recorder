// Created by abdif on 8/22/2020

package com.glunode.abuhurerira.students.info.profile

import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.glunode.abuhurerira.App
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.databinding.ActivityViewPhotoBinding
import com.glunode.abuhurerira.students.StudentsViewModel
import com.glunode.api.Student
import kotlinx.android.synthetic.main.activity_view_photo.*
import kotlinx.android.synthetic.main.activity_view_photo.avatar_image
import kotlinx.android.synthetic.main.fragment_profile.*

class PhotoViewActivity : AppCompatActivity() {

    private val viewModel by viewModels<StudentsViewModel> {
        StudentsViewModel.Factory((applicationContext as App).appRepo)
    }


    private lateinit var intentStudent: Student

    init {
        lifecycleScope.launchWhenCreated {
            intentStudent = intent.extras?.getParcelable<Student>("student")!!
            DataBindingUtil.setContentView<ActivityViewPhotoBinding>(this@PhotoViewActivity, R.layout.activity_view_photo).run {
                this.student = intentStudent
            }

            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_view_photo, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.action_remove_avatar) {
            viewModel.deleteAvatarFromFirebase(intentStudent)
        }
        return super.onOptionsItemSelected(item)
    }
}