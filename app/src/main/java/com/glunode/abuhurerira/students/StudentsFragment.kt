// Created by abdif on 8/2/2020

package com.glunode.abuhurerira.students

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.glunode.abuhurerira.*
import com.glunode.abuhurerira.search.MaterialSearchView
import com.glunode.api.Student
import kotlinx.android.synthetic.main.fragment_students.*

class StudentsFragment : Fragment(), StudentsAdapter.StudentClickListener {

    private val viewModel by viewModels<StudentsViewModel> {
        this.viewLifecycleOwner;
        StudentsViewModel.Factory((requireContext().applicationContext as App).appRepo)
    }

    private val studentsAdapter =
        StudentsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_students, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh_layout.setOnRefreshListener {
            viewModel.refresh(archived_check.isChecked)
        }
        archived_check.setOnCheckedChangeListener { _, b ->
            viewModel.refresh(b)
            refresh_layout.isRefreshing = true
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val spacing = R.dimen.size_small
        recycler_view.run {
            setHasFixedSize(true)
            addItemDecoration(ItemSpacingDecoration(requireContext(), spacing))
            adapter = studentsAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.students.observe(viewLifecycleOwner, Observer {
            studentsAdapter.students = it.toMutableList()
            students_size.text = "${it.count()} Students"
            if (refresh_layout.isRefreshing)
                refresh_layout.isRefreshing = false
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_students, menu)
        val menuItem = menu.findItem(R.id.action_search)
        (requireActivity() as MainActivity).searchView.apply {
            setMenuItem(menuItem)
            setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    studentsAdapter.filter!!.filter(newText)
                    return true
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            else -> false
        }
    }

    override fun onStudentClick(student: Student) {
        QuickHelper.goToStudentDetails(requireContext(), student)
    }

    companion object {

        fun newInstance(): StudentsFragment {
            return StudentsFragment()
        }
    }
}