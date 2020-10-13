package com.glunode.abuhurerira

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.glunode.abuhurerira.klasses.KlassesFragment
import com.glunode.abuhurerira.search.MaterialSearchView
import com.glunode.abuhurerira.students.StudentsFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val searchView: MaterialSearchView by lazy { search_view }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val progress = ProgressDialog(this)
        progress.setMessage("Signing in anonymously...")
        progress.setCancelable(false)

        (applicationContext as App).appRepo.checkCurrentUser {alreadyLoggedIn ->
            if (alreadyLoggedIn) {
                setupUi(savedInstanceState)
            } else {
                progress.show()
                (applicationContext as App).appRepo.login { success, exception->
                    if (success) {
                        progress.dismiss()
                        setupUi(savedInstanceState)
                    } else {
                        progress.dismiss()
                        MaterialAlertDialogBuilder(this@MainActivity).setMessage("Error: ${exception?.message}")
                            .setPositiveButton("Retry") { di,_ ->
                                di.dismiss()
                                recreate()
                            }
                            .setNegativeButton("Finish") { di,_ ->
                                di.dismiss()
                                finish()
                            }
                            .setCancelable(false)
                            .show()
                    }
                }
            }
        }
    }

    private fun setupUi(savedInstanceState: Bundle?) {

        val klassesFragment = KlassesFragment.newInstance()
        val studentsFragment = StudentsFragment.newInstance()
        view_pager.apply {
            adapter = PagerAdapter(
                supportFragmentManager,
                klassesFragment,
                studentsFragment,
                titles = arrayOf("Attendance", "Students")
            )
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    if (searchView.isSearchOpen) searchView.closeSearch()
                    if (position == 0) {
                        fab.show()
                    } else {
                        fab.hide()
                    }
                }
            })
        }

        val tabs: TabLayout = tabs
        tabs.setupWithViewPager(view_pager)

        fab.startPulsing(1000)
        fab.setOnClickListener {
            if (savedInstanceState == null)
                when (view_pager.currentItem) {
                    0 -> klassesFragment.onNewKlass()
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MaterialAlertDialogBuilder(this)
            .setTitle("About App")
            .setMessage("Version: AR 2.0.12/3222\n\nCredit: Abdilfatah Abdullahi\nContact: 0634103005")
            .setPositiveButton("Close") { di, _ ->
                di.dismiss()
            }
            .show()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) searchView.closeSearch()
        else if(view_pager.currentItem == 1) view_pager.currentItem = 0
        else super.onBackPressed()
    }
}