package com.glunode.abuhurerira.students.info.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glunode.abuhurerira.App
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.klasses.KlassesViewModel
import com.glunode.api.Attendance
import com.glunode.api.Klass
import com.glunode.api.Student
import com.glunode.api.data.AppRepository
import kotlinx.android.synthetic.main.info_attendance_fragment.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class AttendanceFragment : Fragment(), YearsListAdapter.OnSelectionListener {

    private lateinit var student: Student

    private var yearAdapter = YearsListAdapter(this)
    private var attAttendance = AttAdapter()

    private val viewModel by viewModels<AttViewModel> {
        this.viewLifecycleOwner
        AttViewModel.Factory((requireContext().applicationContext as App).appRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            student = requireArguments().getParcelable("student")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.info_attendance_fragment, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val list = view.findViewById<RecyclerView>(R.id.list)
        list.adapter = attAttendance

        val container: RecyclerView = view.findViewById(R.id.yearContainer)
        container.setHasFixedSize(true)
        container.adapter = yearAdapter

        viewModel.loadYears(student.uid.toString())

        viewModel.years.observe(this.viewLifecycleOwner, Observer {
            yearAdapter.years = it.toSet()
            no_results.isVisible = it.isEmpty()
            try {
                onYearSelected(it[0])
            } catch (e: Exception) {}
        })

        viewModel.attendances.observe(this.viewLifecycleOwner, Observer {
            attAttendance.data = it.toMutableList()
            no_results.isVisible = it.isEmpty()
        })
    }

    override fun onYearSelected(year: String) {
        viewModel.loadAttendances(student.uid.toString(), year)
    }

    companion object {

        fun newInstance(student: Student): AttendanceFragment {
            val frag = AttendanceFragment()
            frag.arguments = bundleOf("student" to student)
            return frag
        }
    }
}

class AttViewModel(private val repo: AppRepository) : ViewModel() {

    private val _years = MutableLiveData<MutableList<String>>()
    val years: LiveData<List<String>>
        get() = _years.map { it.toList() }

    private val _attendances = MutableLiveData<MutableList<Attendance>>()
    val attendances: LiveData<List<Attendance>>
        get() = _attendances.map { it.toList() }

    fun loadYears(uid: String) {
        viewModelScope.launch {
            repo.getAllAttendanceYearsForStudentForever(uid).let {
                _years.value = it.toMutableList()
            }
        }
    }

    fun loadAttendances(uid: String, year: String) {
        viewModelScope.launch {
            repo.getAllAttendancesForStudentPerYearMonth(uid, year).let {
                _attendances.value = it.toMutableList()
            }
        }
    }

    class Factory(private val dataSource: AppRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AttViewModel(dataSource) as T
        }
    }
}