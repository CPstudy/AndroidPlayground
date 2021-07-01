package kr.co.androidplayground.viewmodel_example

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SampleViewModel : ViewModel() {

    val count: MutableLiveData<Int> = MutableLiveData(0)
}