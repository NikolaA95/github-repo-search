package com.example.githubsearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.githubsearch.databinding.SearechListElementBinding
import com.squareup.picasso.Picasso

class CustomSearchAdapter(
    private val dataList: Result,
    private val userClickListener: (SearchResult) -> Unit,
    private val repoClickListener: (SearchResult) -> Unit
) : RecyclerView.Adapter<CustomSearchAdapter.CustomSearchViewHolder>() {

    class CustomSearchViewHolder(serchListElementBinding: SearechListElementBinding) : RecyclerView.ViewHolder(serchListElementBinding.root) {
        var searchListElementBinding =serchListElementBinding

        fun bind(
            searchResult: SearchResult,
            buttonClickListener: (SearchResult) -> Unit,
            viewClickListener: (SearchResult) -> Unit
        ) {
           searchListElementBinding.viewAsListener.setOnClickListener { viewClickListener(searchResult) }
            searchListElementBinding.imageAvatarButton.setOnClickListener { buttonClickListener(searchResult) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomSearchViewHolder {
        val itemView = LayoutInflater.from(parent.context)
        var searchListElementBinding = SearechListElementBinding.inflate(itemView ,parent,false)
        return CustomSearchViewHolder(searchListElementBinding)
    }

    override fun getItemCount() = dataList.items.size

    override fun onBindViewHolder(holder: CustomSearchViewHolder, position: Int) {
        val currentItem = dataList.items[position]
        holder.bind(currentItem, userClickListener, repoClickListener)
        holder.searchListElementBinding.textViewRepositoryName.text = currentItem.name
        holder.searchListElementBinding.textViewUserName.text = currentItem.owner.login
        holder.searchListElementBinding.textViewForkNmb.text = currentItem.forks_count.toString()
        holder.searchListElementBinding.textViewIssuesMnb.text = currentItem.open_issues_count.toString()
        holder.searchListElementBinding.textViewWatcherNmb.text = currentItem.watchers_count.toString()

        val image = currentItem.owner.avatar_url
        Picasso.get()
            .load(image)
            .into(holder.searchListElementBinding.imageAvatarButton)
    }

}