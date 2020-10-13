// Created by abdif on 8/20/2020

package com.glunode.abuhurerira.klasses

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.glunode.abuhurerira.App
import com.glunode.abuhurerira.QuickHelper
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.students.info.notes.NotesViewModel
import com.glunode.api.Attendance
import com.glunode.api.Date
import com.glunode.api.Klass
import com.glunode.api.Student
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : AppCompatActivity() {

    private val viewModel by viewModels<ReportViewModel> {
        ReportViewModel.Factory((this.applicationContext as App).appRepo)
    }

    private val klass by lazy { intent.extras?.getParcelable("klass") as Klass? }
    private val date by lazy { (intent.extras!!.getParcelable("date")) as Date? }

    private val adapter = ReportAdapter()

    init {
        lifecycleScope.launchWhenCreated {
            setContentView(R.layout.activity_report)
            setSupportActionBar(toolbar)
            supportActionBar?.title = "Report"
            supportActionBar?.subtitle = "${date?.year}-${date?.month}"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            recycler_view.adapter = adapter

            viewModel.loadReports(klass!!, date!!)

            viewModel.loading.observe(this@ReportActivity, Observer {
                progress.isVisible = it
            })

            viewModel.reports.observe(this@ReportActivity, Observer {
                adapter.reports = it.toMutableList()
                progress.isVisible = false
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}