package com.glunode.abuhurerira.search

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.search.utils.AnimationUtil

class MaterialSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), OnClickListener {

    var isSearchOpen = false
        private set

    private var animationDuration = 0
    private var clearingFocus = false

    //Views
    private val searchLayout: View
    private val mSearchSrcTextView: EditText
    private val mBackBtn: ImageButton
    private val mSearchTopBar: RelativeLayout
    private var mOldQueryText: CharSequence? = null
    private var mUserQuery: CharSequence? = null

    private var mOnQueryChangeListener: OnQueryTextListener? = null
    private var mSearchViewListener: SearchViewListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.search_view, this, true)
        searchLayout = findViewById(R.id.search_layout)
        mSearchTopBar = searchLayout.findViewById(R.id.search_top_bar)

        mBackBtn = searchLayout.findViewById(R.id.action_up_btn)
        mBackBtn.setOnClickListener(this)

        mSearchSrcTextView =
            searchLayout.findViewById<View>(R.id.searchTextView) as EditText
        mSearchSrcTextView.setOnClickListener(this)
        mSearchSrcTextView.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            onSubmitQuery()
            true
        }
        mSearchSrcTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                mUserQuery = s
                this@MaterialSearchView.onTextChanged(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        setAnimationDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM)
    }

    private fun onTextChanged(newText: CharSequence) {
        val text: CharSequence = mSearchSrcTextView.text
        mUserQuery = text
        val hasText = !TextUtils.isEmpty(text)
        if (mOnQueryChangeListener != null && !TextUtils.equals(newText, mOldQueryText)) {
            mOnQueryChangeListener?.onQueryTextChange(newText.toString())
        }
        mOldQueryText = newText.toString()
    }

    private fun onSubmitQuery() {
        val query: CharSequence? = mSearchSrcTextView.text
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (mOnQueryChangeListener == null || !mOnQueryChangeListener!!.onQueryTextSubmit(query.toString())) {
                closeSearch()
                mSearchSrcTextView.text = null
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //Public Attributes
    override fun setBackground(background: Drawable) {
        mSearchTopBar!!.background = background
    }

    override fun setBackgroundColor(color: Int) {
        mSearchTopBar!!.setBackgroundColor(color)
    }

    fun setQuery(query: CharSequence?, submit: Boolean) {
        mSearchSrcTextView!!.setText(query)
        if (query != null) {
            mSearchSrcTextView!!.setSelection(mSearchSrcTextView!!.length())
            mUserQuery = query
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery()
        }
    }

    fun setMenuItem(menuItem: MenuItem) {
        menuItem.setOnMenuItemClickListener { item: MenuItem? ->
            showSearch()
            true
        }
    }

    fun setAnimationDuration(duration: Int) {
        animationDuration = duration
    }
    /**
     * Open Search View. If animate is true, Animate the showing of the view.
     *
     * @param animate true for animate
     */
    /**
     * Open Search View. This will animate the showing of the view.
     */
    @JvmOverloads
    fun showSearch(animate: Boolean = true) {
        if (isSearchOpen) {
            return
        }

        //Request Focus
        mSearchSrcTextView.setText(null)
        mSearchSrcTextView!!.requestFocus()
        if (animate) {
            setVisibleWithAnimation()
        } else {
            searchLayout!!.visibility = View.VISIBLE
            if (mSearchViewListener != null) {
                mSearchViewListener!!.onSearchViewShown()
            }
        }
        isSearchOpen = true
    }

    private fun setVisibleWithAnimation() {
        val animationListener: AnimationUtil.AnimationListener =
            object : AnimationUtil.AnimationListener {
                override fun onAnimationStart(view: View): Boolean {
                    return false
                }

                override fun onAnimationEnd(view: View): Boolean {
                    if (mSearchViewListener != null) {
                        mSearchViewListener!!.onSearchViewShown()
                    }
                    return false
                }

                override fun onAnimationCancel(view: View): Boolean {
                    return false
                }
            }
        searchLayout.visibility = View.VISIBLE
        AnimationUtil.reveal(mSearchTopBar, animationListener)
    }

    /**
     * Close search view.
     */
    fun closeSearch() {
        if (!isSearchOpen) {
            return
        }
        mSearchSrcTextView.setText(null)
        clearFocus()
        searchLayout.isVisible = false
        if (mSearchViewListener != null) {
            mSearchViewListener!!.onSearchViewClosed()
        }
        isSearchOpen = false
    }

    fun setOnQueryTextListener(listener: OnQueryTextListener?) {
        mOnQueryChangeListener = listener
    }

    fun setOnSearchViewListener(listener: SearchViewListener?) {
        mSearchViewListener = listener
    }

    override fun requestFocus(
        direction: Int,
        previouslyFocusedRect: Rect?
    ): Boolean {
        // Don't accept focus if in the middle of clearing focus
        if (clearingFocus) return false
        // Check if SearchView is focusable.
        return if (!isFocusable) false else mSearchSrcTextView.requestFocus(
            direction,
            previouslyFocusedRect
        )
    }

    override fun clearFocus() {
        clearingFocus = true
        hideKeyboard(this)
        super.clearFocus()
        mSearchSrcTextView!!.clearFocus()
        clearingFocus = false
    }

    interface OnQueryTextListener {
        /**
         * Called when the user submits the query. This could be due to a key press on the
         * keyboard or due to pressing a submit button.
         * The listener can override the standard behavior by returning true
         * to indicate that it has handled the submit request. Otherwise return false to
         * let the SearchView handle the submission by launching any associated intent.
         *
         * @param query the query text that is to be submitted
         * @return true if the query has been handled by the listener, false to let the
         * SearchView perform the default action.
         */
        fun onQueryTextSubmit(query: String?): Boolean

        /**
         * Called when the query text is changed by the user.
         *
         * @param newText the new content of the query text field.
         * @return false if the SearchView should perform the default action of showing any
         * suggestions if available, true if the action was handled by the listener.
         */
        fun onQueryTextChange(newText: String?): Boolean
    }

    override fun onClick(v: View?) {
        if (v === mBackBtn) {
            closeSearch()
        }
    }

    interface SearchViewListener {
        fun onSearchViewShown()
        fun onSearchViewClosed()
    }
}