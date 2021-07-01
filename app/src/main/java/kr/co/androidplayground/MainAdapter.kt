package kr.co.androidplayground

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.co.androidplayground.viewmodel_example.ViewModelActivity

class MainAdapter(private val list: Array<String>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(android.R.id.text1)

        fun bind(text: String) {
            textView.text = text

            itemView.setOnClickListener {
                when (text) {
                    "ViewModel" -> Intent(it.context, ViewModelActivity::class.java).also { intent -> it.context.startActivity(intent) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}