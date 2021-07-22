package kr.co.androidplayground.viewmodel_example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import kr.co.androidplayground.R
import kr.co.androidplayground.databinding.ActivityViewModelBinding

class ViewModelActivity : AppCompatActivity() {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityViewModelBinding>(this, R.layout.activity_view_model)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: SampleViewModel by viewModels()
        binding.lifecycleOwner = this
        binding.model = model

        binding.button.setOnClickListener {
            model.count.value?.let {
                model.count.postValue(it + 1)
            }
        }
    }
}