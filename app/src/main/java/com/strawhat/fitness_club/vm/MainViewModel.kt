package com.strawhat.fitness_club.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.PublishRelay
import com.strawhat.fitness_club.services.ClubRepository
import com.strawhat.fitness_club.view.ClubInfoViewModel
import com.strawhat.fitness_club.view.ClubMemberViewModel
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

    val viewStateRelay = BehaviorRelay.create<ClubViewState>()

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var clubRepository: ClubRepository

    fun initSubscriptions() {
        val load = ObservableTransformer<ClubViewLoadAction, ClubViewResult> { event ->
            return@ObservableTransformer event.flatMap { action ->
                clubRepository.getClubInfo()
                    .subscribeOn(Schedulers.io())
                    .zipWith(
                        clubRepository.getMembersInfo(1)
                            .subscribeOn(Schedulers.io()),
                        BiFunction(fun(
                            t1: ClubInfoViewModel,
                            t2: List<ClubMemberViewModel>
                        ): ClubViewResult {
                            return ClubViewLoadSuccessResult(t1, t2)
                        })
                    )
                    .onErrorReturn { ClubViewLoadErrorResult(it) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .startWithItem(ClubLoadingResult)
            }
        }

        val clubUI = ObservableTransformer<ClubViewAction, ClubViewResult> { event ->
            return@ObservableTransformer event.publish { shared ->
                return@publish Observable.mergeArray(
                    shared.ofType(ClubViewLoadAction::class.java).compose(load)
                )
            }

        }

        disposable.add(clubViewActionsRelay
            .startWithItem(ClubViewLoadAction)
            .compose(clubUI)
            .observeOn(AndroidSchedulers.mainThread())
            .scan(ClubViewState(), BiFunction { state, result ->
                return@BiFunction reduce(state, result)
            })
            .subscribeBy(
                onNext = {
                    viewStateRelay.accept(it)
                },
                onError = {
                    throw OnErrorNotImplementedException(it)
                }
            ))
    }

    private fun reduce(state: ClubViewState, result: ClubViewResult): ClubViewState {
        return when (result) {
            is ClubViewLoadSuccessResult -> state.copy(
                info = result.clubInfo,
                members = result.members,
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
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}