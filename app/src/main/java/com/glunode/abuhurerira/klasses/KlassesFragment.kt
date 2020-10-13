// Created by abdif on 8/2/2020

package com.glunode.abuhurerira.klasses

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.glunode.abuhurerira.App
import com.glunode.abuhurerira.ItemSpacingDecoration
import com.glunode.abuhurerira.QuickHelper
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.attendance.AttendanceActivity
import com.glunode.abuhurerira.databinding.FragmentKlassesBinding
import com.glunode.api.Date
import com.glunode.api.Klass
import com.glunode.api.Student
import com.glunode.api.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*
import java.text.DateFormatSymbols
import java.util.*

class KlassesFragment : Fragment(), KlassesAdapter.KlassClickListener {

    private var importSelectedKlass: Klass? = null

    private lateinit var repo: AppRepository
    private val viewModel by viewModels<KlassesViewModel> {
        this.viewLifecycleOwner
        repo = (requireContext().applicationContext as App).appRepo
        KlassesViewModel.Factory(repo)
    }

    private val klassesAdapter = KlassesAdapter(this)

    private lateinit var binding: FragmentKlassesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_klasses, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val spacing = R.dimen.size_medium
        binding.klassesContainer.run {
            addItemDecoration(ItemSpacingDecoration(requireContext(), spacing))
            adapter = klassesAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.klasses.observe(viewLifecycleOwner, Observer {
            klassesAdapter.submitList(null)
            klassesAdapter.submitList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_klasses, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            else -> false
        }
    }

    override fun onKlassClick(klass: Klass) {
        QuickHelper.showCalendarDialog(requireContext()) { eventDay ->
            val dfs = DateFormatSymbols()
            val year = eventDay.calendar[Calendar.YEAR]
            val month = dfs.shortMonths[(eventDay.calendar[Calendar.MONTH])]
            val day = eventDay.calendar[Calendar.DAY_OF_MONTH]
            val dayStr = dfs.shortWeekdays[eventDay.calendar[Calendar.DAY_OF_WEEK]]
            val intent = Intent(context, AttendanceActivity::class.java)
            intent.putExtra("date", Date(year, month, day, dayStr))
            intent.putExtra("klass", klass as Parcelable?)
            startActivity(intent)
        }
    }

    override fun onKlassOverflowClick(klass: Klass, root: View) {
        val popupMenu = PopupMenu(requireContext(), root)
        popupMenu.menuInflater.inflate(R.menu.menu_klass_popup, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add_student -> {
                    onNewStudent(klass)
                    true
                }

                R.id.action_rename -> {
                    QuickHelper.showAddEditKlassDialog(requireContext(), klass.name) { name ->
                        if (name.isNotEmpty()) {
                            viewModel.renameKlass(klass, name)
                        } else
                            Toast.makeText(context, "No name provided!", Toast.LENGTH_SHORT).show()
                    }
                    true
                }

                R.id.action_import -> {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "file/*"
                        try {
                            importSelectedKlass = klass
                            startActivityForResult(
                                Intent.createChooser(intent, "Select a csv.."), 1101
                            )
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }

                R.id.action_export -> {
                    exportClass(klass)
                    true
                }

                R.id.action_report -> {
                    QuickHelper.showCalendarDialog(requireContext()) { eventDay ->
                        val dfs = DateFormatSymbols()
                        val year = eventDay.calendar[Calendar.YEAR]
                        val month = dfs.shortMonths[(eventDay.calendar[Calendar.MONTH])]
                        val day = eventDay.calendar[Calendar.DAY_OF_MONTH]
                        val dayStr = dfs.shortWeekdays[eventDay.calendar[Calendar.DAY_OF_WEEK]]
                        val intent = Intent(context, ReportActivity::class.java)
                        intent.putExtra("date", Date(year, month, day, dayStr))
                        intent.putExtra("klass", klass as Parcelable?)
                        startActivity(intent)
                    }
                    true
                }

                R.id.action_delete -> {
                    QuickHelper.showConfirmDialog(
                        requireContext(),
                        "Confirm Deletion?",
                        "This class and all it's students will be deleted!"
                    ) {
                        viewModel.deleteKlass(klass)
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val progress = ProgressDialog(requireContext()).also {
            it.setCancelable(false)
            it.setMessage("Loading")
            it.setTitle("Loading")
        }
        progress.show()
        if (resultCode == Activity.RESULT_OK && requestCode == 1101) {
            if (data != null) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        BufferedReader(FileReader(data.data?.path)).use { reader ->
                            val students = reader.lineSequence().map {
                                val input = it.split(",".toRegex()).toTypedArray()
                                val no = input[0].toInt()
                                val name = input[1]
                                val phoneNumber1 = input[2]
                                val phoneNumber2 = input[3]
                                val phoneNumber3 = input[4]
                                return@map Student(
                                    no,
                                    name = name,
                                    klass = importSelectedKlass,
                                    phoneNumber1 = phoneNumber1,
                                    phoneNumber2 = phoneNumber2,
                                    phoneNumber3 = phoneNumber3,
                                    archived = false
                                )
                            }

                            viewModel.addStudents(students.toList()) {
                                progress.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun exportClass(klass: Klass) {
        val file = File(
            Environment.getExternalStorageDirectory().toString() +
                    File.separator + klass.name + ".csv"
        )
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                return
            }
        }
        val writer: FileWriter
        writer = try {
            FileWriter(file, false)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val sb = StringBuilder()
            val students = repo.getAllStudentsWhere(klass)
                .sortedBy { it.no }
            students.forEach {
                sb.append(it.no).append(',')
                    .append(it.name).append(',')
                    .append(it.phoneNumber1).append(',')
                    .append(it.phoneNumber2).append(',')
                    .append(it.phoneNumber3).append('\n')
            }

            Timber.e(sb.toString())
            withContext(Dispatchers.IO) {
                try {
                    writer.write(sb.toString())
                    writer.flush()
                    writer.close()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "File exported to " + file.absolutePath,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    writer.close()
                    withContext(Dispatchers.IO) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun onNewKlass() {
        QuickHelper.showAddEditKlassDialog(requireContext(), null) {
            val uid = System.currentTimeMillis()
            if (it.isNotEmpty()) {
                viewModel.addKlass(Klass(uid, it))
            } else
                Toast.makeText(context, "No name provided!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onNewStudent(klass: Klass) {
        QuickHelper.addEditStudentDialog(requireContext(), klass, null) {
            viewModel.addStudents(it) {
                Toast.makeText(requireContext(), "Student saved!", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {

        fun newInstance(): KlassesFragment {
            return KlassesFragment()
        }
    }
}