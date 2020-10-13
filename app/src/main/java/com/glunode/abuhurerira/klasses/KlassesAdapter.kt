package com.glunode.abuhurerira.klasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.glunode.abuhurerira.R
import com.glunode.abuhurerira.databinding.ItemKlassBinding
import com.glunode.api.Klass
import timber.log.Timber

class KlassesAdapter(private val klassClickListener: KlassClickListener) :
    ListAdapter<Klass, KlassesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent, klassClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemKlassBinding,
        private val klassClickListener: KlassClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Klass) {
            binding.klass = model
            binding.listener = klassClickListener
            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup, klassClickListener: KlassClickListener): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<ItemKlassBinding>(
                    inflater,
                    R.layout.item_klass,
                    parent,
                    false
                )
                return ViewHolder(binding, klassClickListener)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Klass>() {

        override fun areItemsTheSame(oldItem: Klass, newItem: Klass) = oldItem.uid == newItem.uid

        override fun areContentsTheSame(oldItem: Klass, newItem: Klass) = oldItem == newItem
    }

    interface KlassClickListener {

        fun onKlassClick(klass: Klass)

        fun onKlassOverflowClick(klass: Klass, root: View)
    }
}
