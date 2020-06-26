package com.strawhat.fitness_club.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.PublishRelay
import com.strawhat.fitness_club.services.ClubRepository
import com.strawhat.fitness_club.services.binding.MembersBinding
import com.strawhat.fitness_club.services.binding.toViewModel
import com.strawhat.fitness_club.view.ClubInfoViewModel
import com.strawhat.fitness_club.vm.events.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val clubViewActionsRelay = PublishRelay.create<ClubViewAction>()

    val viewStateRelay: BehaviorRelay<ClubViewState> = BehaviorRelay.create<ClubViewState>()

    private val disposable = CompositeDisposable()

    private var previousState: ClubViewState = ClubViewState()

    private val loadPageRelay: PublishRelay<Unit> = PublishRelay.create()

    @Inject
    lateinit var clubRepository: ClubRepository

    fun initSubscriptions() {
        val initialLoad =
            ObservableTransformer<ClubViewInitialLoadAction, ClubViewResult> { event ->
                return@ObservableTransformer event.flatMap { action ->
                    clubRepository.getClubInfo()
                        .subscribeOn(Schedulers.io())
                        .zipWith(
                            clubRepository.getMembersInfo(1)
                                .subscribeOn(Schedulers.io()),
                            BiFunction(fun(
                                clubInfo: ClubInfoViewModel,
                                binding: MembersBinding
                            ): ClubViewResult {
                                return ClubViewInitialLoadSuccessResult(
                                    clubInfo = clubInfo,
                                    members = binding.toViewModel(),
                                    hasMore = binding.hasMore
                                )
                            })
                        )
                        .onErrorReturn { ClubViewLoadErrorResult(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWithItem(ClubLoadingResult)
                }
            }
        val loadPage = ObservableTransformer<Unit, ClubViewResult> { event ->
            return@ObservableTransformer event.flatMap { action ->
                if (previousState.hasMore.not()) {
                    return@flatMap Observable.just(ClubViewLoadEndedResult)
                } else {
                    return@flatMap clubRepository.getMembersInfo(previousState.lastPage + 1)
                        .subscribeOn(Schedulers.io())
                        .map(fun(it: MembersBinding): ClubViewResult {
                            return ClubViewLoadSuccessResult(it.toViewModel(), it.hasMore)
                        })
                        .onErrorReturn { ClubViewLoadErrorResult(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWithItem(ClubLoadingResult)
                }
            }
        }

        val clubUI = ObservableTransformer<ClubViewAction, ClubViewResult> { event ->
            return@ObservableTransformer event.publish { shared ->
                return@publish Observable.mergeArray(
                    shared.ofType(ClubViewInitialLoadAction::class.java).compose(initialLoad),
                    shared.ofType(ClubViewLoadAction::class.java)
                        .map { ClubLoadPageRequestedResult },
                    shared.ofType(LastVisibleItemChangedAction::class.java)
                        .map { LastVisibleItemChangedResult(it.position) }
                )
            }

        }

        disposable.add(clubViewActionsRelay
            .startWithItem(ClubViewInitialLoadAction)
            .compose(clubUI)
            .mergeWith(loadPageRelay.compose(loadPage))
            .observeOn(AndroidSchedulers.mainThread())
            .scan(previousState, BiFunction { state, result ->
                return@BiFunction reduce(state, result)
            })
            .subscribeBy(
                onNext = {
                    emmit(it)
                },
                onError = {
                    throw OnErrorNotImplementedException(it)
                }
            ))
    }

    fun emmit(state: ClubViewState) {
        previousState = state
        viewStateRelay.accept(state)
    }

    private fun reduce(state: ClubViewState, result: ClubViewResult): ClubViewState {
        return when (result) {
            is ClubViewInitialLoadSuccessResult -> state.copy(
                changedMembers = true,
                lastPage = state.lastPage + 1,
                info = result.clubInfo,
                members = result.members.toMutableList(),
                hasMore = result.hasMore,
                loading = false,
                errorMessage = null
            )
            ClubLoadingResult -> state.copy(loading = true)
            is ClubViewLoadErrorResult -> {
                state.copy(
                    errorMessage = result.throwable.message,
                    loading = false
                )
            }
            is ClubViewLoadSuccessResult -> {
                val res = state.members
                result.members.forEach {
                    if (res.find { old -> old.number == it.number } == null) {
                        it.isCurrentUser = state.info?.myPosition == it.number
                        res.add(it)
                    }
                }
                state.copy(
                    changedMembers = true,
                    lastPage = state.lastPage + 1,
                    hasMore = result.hasMore,
                    members = res,
                    loading = false,
                    errorMessage = null
                )
            }
            ClubViewLoadEndedResult -> {
                state.copy(
                    changedMembers = false,
                    loading = false,
                    errorMessage = null,
                    hasMore = false
                )
            }
            ClubLoadPageRequestedResult -> {
                if (state.loading) {
                    state.copy(changedMembers = false)
                } else {
                    loadPageRelay.accept(Unit)
                    state.copy(changedMembers = false)
                }
            }
            is LastVisibleItemChangedResult -> {
                state.copy(
                    lastVisiblePosition = result.position,
                    changedMembers = false
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun loadNextPage() {
        if (previousState.hasMore) {
            clubViewActionsRelay.accept(ClubViewLoadAction)
        }
    }

    fun updateLastVisibleItem(position: Int) {
        if (previousState.lastVisiblePosition != position) {
            clubViewActionsRelay.accept(LastVisibleItemChangedAction(position))
        }
    }

}