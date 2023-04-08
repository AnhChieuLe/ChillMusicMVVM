package com.example.chillmusic.adapter

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView

abstract class LifecycleAdapter<T : LifecycleAdapter.LifecycleViewHolder> : RecyclerView.Adapter<T>(){
    abstract class LifecycleViewHolder(view: View) : RecyclerView.ViewHolder(view), LifecycleOwner {
        private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
        override val lifecycle: Lifecycle get() = lifecycleRegistry

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun markAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun markDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }
    }

    override fun onViewAttachedToWindow(holder: T) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: T) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}