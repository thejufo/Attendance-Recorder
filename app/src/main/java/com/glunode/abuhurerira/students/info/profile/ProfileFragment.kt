// Created by abdif on 8/9/2020

package com.glunode.abuhurerira.students.info.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.glunode.abuhurerira.App
import com.glunode.abuhurerira.QuickHelper
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.databinding.FragmentProfileBinding
import com.glunode.abuhurerira.klasses.KlassesViewModel
import com.glunode.abuhurerira.setMarkVisible
import com.glunode.abuhurerira.students.StudentsViewModel
import com.glunode.api.Klass
import com.glunode.api.Student
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_profile.*
import timber.log.Timber
import java.util.*

class ProfileFragment : Fragment() {

    private val archiveState = MutableLiveData<Boolean>()

    private val student by lazy {
        (requireArguments().getParcelable("student") as Student?).apply {
            archiveState.value = this?.archived
        }
    }

    private val viewModel by viewModels<StudentsViewModel> {
        viewLifecycleOwner
        StudentsViewModel.Factory((requireContext().applicationContext as App).appRepo)
    }

    private val klassesViewModel by viewModels<KlassesViewModel> {
        viewLifecycleOwner
        KlassesViewModel.Factory((requireContext().applicationContext as App).appRepo)
    }

    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.student = student
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCall.setOnClickListener {
            QuickHelper.showCallMessagePopup(it, student) {
                QuickHelper.makePhoneCall(requireContext(), it)
                true
            }
        }

        binding.btnMessage.setOnClickListener {
            QuickHelper.showCallMessagePopup(it, student) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.fromParts("sms", "0$it", null)
                )
                requireActivity().startActivity(intent)
                true
            }
        }

        add_avatar_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        avatar_image.setOnClickListener {
            QuickHelper.goToStudentPhotoView(requireContext(), student!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedPhotoUri = data.data ?: return
            // Get and resize profile image
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            requireActivity().contentResolver.query(selectedPhotoUri, filePathColumn, null,
                null, null)?.use {
                it.moveToFirst()
                val columnIndex = it.getColumnIndex(filePathColumn[0])
                // If picture chosen from camera rotate by 270 degrees else
                viewModel.uploadImageToFirebaseStorage(selectedPhotoUri, student?.uid.toString()) {uri ->
                    student?.avatar = uri.toString()
                    viewModel.updateStudent(student!!) {
                        Glide.with(requireContext()).load(uri).into(avatar_image)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        archiveState.observe(viewLifecycleOwner, Observer {
            menu.findItem(R.id.action_archive)
                .setIcon(if (it) R.drawable.ic_unarchived else R.drawable.ic_archive)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_archive -> {
                QuickHelper.showConfirmDialog(
                    requireContext(),
                    msg = "Confirm?"
                ) {
                    val update = !student?.archived!!
                    student?.archived = update
                    archiveState.value = update
                    viewModel.updateStudent(student!!) {
                        if (it) {
                            Toast.makeText(requireContext(), "Archive toggled", Toast.LENGTH_SHORT).show()
                        } else
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            R.id.action_delete -> {
                QuickHelper.showConfirmDialog(
                    requireContext(),
                    msg = "Confirm?"
                ) {
                    viewModel.deleteStudent(student!!) {
                        if (it) {
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "And error occurred!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }

            R.id.action_change_klass -> {
                klassesViewModel.klasses.observe(viewLifecycleOwner, Observer {klasses ->
                    val arr = Array<String>(klasses.size) {
                        klasses.map {it.name!!}[it]
                    }

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Pick class")
                        .setItems(arr){_, index ->
                            student?.klass = klasses[index]
                            viewModel.updateStudent(student!!) {
                                if (it) {
                                    (requireActivity() as AppCompatActivity)
                                        .supportActionBar?.subtitle = klasses[index].name
                                }
                            }
                        }
                        .show()
                })
            }

            R.id.action_bold,
            R.id.action_red,
            R.id.action_blue -> {
                val mark = item.title.toString()
                student?.mark = mark
                viewModel.updateStudent(student!!) {
                    Toast.makeText(requireContext(), "Mark Set!", Toast.LENGTH_SHORT).show()
                    name_text.setMarkVisible(mark)
                }
            }
            R.id.action_none -> {
                student?.mark = null
                viewModel.updateStudent(student!!) {
                    Toast.makeText(requireContext(), "Mark Removed!", Toast.LENGTH_SHORT).show()
                    name_text.setMarkVisible(null)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onEditStudent() {
        QuickHelper.addEditStudentDialog(requireContext(), student!!.klass!!, student) {
            viewModel.updateStudent(it) {updated ->
                if (updated) {
                    binding.student = it
                    binding.executePendingBindings()
                    Toast.makeText(requireContext(), "Student saved!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Error!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {

        fun newInstance(student: Student): ProfileFragment {
            val frag = ProfileFragment()
            frag.arguments = bundleOf("student" to student)
            return frag
        }
    }
}