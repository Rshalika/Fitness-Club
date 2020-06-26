package com.strawhat.fitness_club.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.strawhat.fitness_club.MyApplication
import com.strawhat.fitness_club.R
import com.strawhat.fitness_club.utils.toVisibility
import com.strawhat.fitness_club.vm.MainViewModel
import com.strawhat.fitness_club.vm.events.ClubViewState
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.Serializable
import kotlin.math.abs


private const val APP_BAR_STATE = "APP_BAR_STATE"

private const val VISIBLE_THRESHOLD = 1

class MainActivity : AppCompatActivity() {


    private var appBarState: AppBarStateChangeListener.State =
        AppBarStateChangeListener.State.EXPANDED

    private val disposable = CompositeDisposable()

    private val viewModel: MainViewModel by viewModels()

    private val adapter = MemberAdapter(mutableListOf())

    private var lastVisibleItem = 0

    private var totalItemCount: Int = 0

    private lateinit var layoutManager: FixedForAppBarLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            appBarState = it.getSerializable(APP_BAR_STATE) as AppBarStateChangeListener.State
        }
        (applicationContext as MyApplication).appComponent.inject(viewModel)

        setContentView(R.layout.activity_main)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        layoutManager = FixedForAppBarLayoutManager(this)
        members_list.adapter = adapter
        members_list.layoutManager = layoutManager
        app_bar.addOnOffsetChangedListener(object :
            AppBarStateChangeListener(layoutManager, viewModel) {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                appBarState = state
                invalidateOptionsMenu()
            }
        })

        disposable.add(viewModel.viewStateRelay.subscribeBy(
            onNext = {
                updateState(it)
            },
            onError = {
                throw OnErrorNotImplementedException(it)
            }
        ))

        setUpLoadMoreListener()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.initSubscriptions()
    }

    private fun updateState(state: ClubViewState) {
        state.info?.let { info ->
            members_info.text = info.members.toString()
            time_info.text = info.avgTime
            total_time_info.text = info.totalTime
            club_name.text = info.clubName
            if (state.lastVisiblePosition < state.info.myId) {
                number.text = info.myId.toString()
                profile_name.text = info.myName
                profile_time.text = info.myHours
                Glide
                    .with(this)
                    .load(info.myImageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.loader_image)
                    .into(profile_image)
            }
            me_layout.visibility =
                (state.lastVisiblePosition < state.info.myId && state.lastVisiblePosition != 0).toVisibility()

            Glide
                .with(this)
                .load(info.imageUrl)
                .circleCrop()
                .placeholder(R.drawable.loader_image)
                .into(club_image)
        }

        loading_bar.visibility = state.loading.toVisibility()
        if (state.changedMembers) {
            adapter.setMembers(state.members)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(APP_BAR_STATE, appBarState)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val colapsed = appBarState == AppBarStateChangeListener.State.COLLAPSED
        if (colapsed) {
            menu?.findItem(R.id.add)?.isEnabled = true
            menu?.findItem(R.id.add)?.icon =
                resources.getDrawable(R.drawable.ic_baseline_add_black_24, theme)
        } else {
            menu?.findItem(R.id.add)?.isEnabled = false
            menu?.findItem(R.id.add)?.icon =
                resources.getDrawable(R.drawable.ic_baseline_add_white_24, theme)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    abstract class AppBarStateChangeListener(
        private val layoutManager: LinearLayoutManager,
        private val viewModel: MainViewModel
    ) : OnOffsetChangedListener {
        enum class State : Serializable {
            EXPANDED, COLLAPSED, IDLE
        }

        private var mCurrentState = State.IDLE

        override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
            val findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition()
            viewModel.updateLastVisibleItem(findLastVisibleItemPosition)

            mCurrentState = when {
                i == 0 -> {
                    if (mCurrentState != State.EXPANDED) {
                        onStateChanged(
                            appBarLayout,
                            State.EXPANDED
                        )
                    }
                    State.EXPANDED
                }
                abs(i) >= appBarLayout.totalScrollRange -> {
                    if (mCurrentState != State.COLLAPSED) {
                        onStateChanged(
                            appBarLayout,
                            State.COLLAPSED
                        )
                    }
                    State.COLLAPSED
                }
                else -> {
                    if (mCurrentState != State.IDLE) {
                        onStateChanged(
                            appBarLayout,
                            State.IDLE
                        )
                    }
                    State.IDLE
                }
            }
        }

        abstract fun onStateChanged(appBarLayout: AppBarLayout, state: State)
    }

    private fun setUpLoadMoreListener() {

        scroll.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            val findLastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            println("-=-=-=- findLastVisibleItemPosition setOnScrollChangeListener $findLastVisibleItemPosition")
            viewModel.updateLastVisibleItem(findLastVisibleItemPosition)
            totalItemCount = layoutManager.itemCount
            lastVisibleItem = findLastVisibleItemPosition
            if (totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD) {
                viewModel.loadNextPage()
            }
        }

    }

}