// Created by abdif on 8/7/2020

package com.glunode.abuhurerira.students.info

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.viewpager.widget.ViewPager
import com.glunode.abuhurerira.PagerAdapter
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.students.info.attendance.AttendanceFragment
import com.glunode.abuhurerira.students.info.notes.NotesFragment
import com.glunode.abuhurerira.students.info.profile.ProfileFragment
import com.glunode.api.Student
import kotlinx.android.synthetic.main.info_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoActivity : AppCompatActivity() {

    val student by lazy { intent.getParcelableExtra<Student>("student") }

    init {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                whenCreated {
                    setContentView(R.layout.info_activity)
                    setSupportActionBar(toolbar)
                    supportActionBar?.title = "Student Info - ${student?.no}"
                    supportActionBar?.subtitle = student?.klass?.name
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)

                    val profileFragment = ProfileFragment.newInstance(student!!)
                    val attendanceFragment = AttendanceFragment.newInstance(student!!)
                    val behaviorFragment = NotesFragment.newInstance(student!!)

                    val adapter = PagerAdapter(supportFragmentManager,
                        profileFragment, attendanceFragment, behaviorFragment, titles = arrayOf("Profile", "Attendance", "Behavior"))

                    view_pager.adapter = adapter
                    view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(state: Int) {}

                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {}

                        override fun onPageSelected(position: Int) {
                            if (position == 1) {
                                fab.hide()
                            } else {
                                fab.show()
                            }
                        }
                    })
                    tabs.setupWithViewPager(view_pager)

                    fab.setOnClickListener {
                        if (view_pager.currentItem == 0) {
                            profileFragment.onEditStudent()
                        } else if(view_pager.currentItem == 2) {
                            behaviorFragment.onAddNote()
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}