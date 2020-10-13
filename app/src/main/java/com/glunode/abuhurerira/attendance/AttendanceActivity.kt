package com.glunode.abuhurerira.attendance

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.glunode.abuhurerira.App
import com.glunode.abuhurerira.QuickHelper
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.databinding.ActivityAttendanceBinding
import com.glunode.api.Attendance
import com.glunode.api.Date
import com.glunode.api.Klass
import kotlinx.android.synthetic.main.activity_attendance.*
import kotlinx.android.synthetic.main.activity_attendance.refresh_layout
import kotlinx.android.synthetic.main.fragment_students.*
import java.text.DateFormatSymbols
import java.util.*

class AttendanceActivity : AppCompatActivity(), AttendanceAdapter.AttendanceTableListener {

    private val attendanceVM by viewModels<AttendanceViewModel> {
        AttendanceViewModel.Factory((applicationContext as App).appRepo)
    }

    private lateinit var binding: ActivityAttendanceBinding
    private val attendanceAdapter = AttendanceAdapter(this)
    private val filterAdapter = AttendanceAdapter(this);

    private var filterDialogBuilder: AlertDialog.Builder? = null

    private val klass by lazy { intent.getParcelableExtra<Klass>("klass") }
    private lateinit var date: Date

    private fun updateUiAndSetup(date: Date) {
        supportActionBar?.subtitle = "${date.day}-${date.month}-${date.year}, ${date.dayStr}"


        binding.refreshLayout.setOnRefreshListener {
            attendanceVM.loadAttendances(klass!!, date)
        }

        refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        binding = DataBindingUtil.setContentView(
            this@AttendanceActivity,
            R.layout.activity_attendance
        )

        binding.viewModel = attendanceVM
        binding.lifecycleOwner = this@AttendanceActivity

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = klass?.name

        container.apply {
            setHasFixedSize(true)
            adapter = attendanceAdapter
        }

        attendanceVM.attendances.observe(this@AttendanceActivity, Observer {
            attendanceAdapter.data = it.toMutableList()
            binding.refreshLayout.isRefreshing = false
        })

        date = intent.getParcelableExtra("date")!!
        updateUiAndSetup(date)

        filterDialogBuilder = AlertDialog.Builder(this)
    }

    private fun refresh() {
        binding.refreshLayout.isRefreshing = true
        attendanceVM.loadAttendances(klass!!, date)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_attendance, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_refresh -> {
                refresh()
            }

            R.id.action_filter -> {
                filter()
                filterDialogBuilder?.show()
            }

            R.id.action_update_date -> {
                QuickHelper.showCalendarDialog(this) { eventDay ->
                    val dfs = DateFormatSymbols()
                    val year = eventDay.calendar[Calendar.YEAR]
                    val month = dfs.shortMonths[(eventDay.calendar[Calendar.MONTH])]
                    val day = eventDay.calendar[Calendar.DAY_OF_MONTH]
                    val dayStr = dfs.shortWeekdays[eventDay.calendar[Calendar.DAY_OF_WEEK]]
                    date = Date(year, month, day, dayStr)

                    updateUiAndSetup(date)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun filter() {
        val inflater = layoutInflater
        val view =
            inflater.inflate(R.layout.dialog_filter_attendance, null, false)
        val container: RecyclerView = view.findViewById(R.id.container)
        container.layoutManager = LinearLayoutManager(this)
        container.setHasFixedSize(true)
        container.adapter = filterAdapter
        filterDialogBuilder?.setView(view)
        filterDialogBuilder?.setPositiveButton("Hide") { dialogInterface, i -> dialogInterface.dismiss() }

        attendanceVM.attendances.observe(this, Observer { list ->
            filterAdapter.data = (list.filter {
                it.status != null &&
                        (!it.status.equals(getString(R.string.status_no_uniform)) || !it.status.equals(getString(R.string.status_excused_abs)))
            }).toMutableList()
        })
    }

    override fun onStudentNameClick(view: View?, attendance: Attendance) {
        val student = attendance.student!!
        QuickHelper.goToStudentDetails(this, student)
    }

    override fun onStatusClick(view: View?, attendance: Attendance) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_attendance_status, popup.menu)
        popup.setOnMenuItemClickListener {
            if (it.title == "None") {
                attendance.status = null
                attendanceVM.updateAttendance(attendance)
            } else {
                val status = it.title.toString()
                attendance.status = status
                attendance.date = date
                attendanceVM.updateAttendance(attendance)
            }
            true
        }
        popup.show()
    }
}