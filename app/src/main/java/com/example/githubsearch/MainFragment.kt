package com.example.githubsearch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubsearch.databinding.FragmentMainBinding
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_main.*
import okhttp3.*
import java.io.IOException

class MainFragment : Fragment() {

    private lateinit var mainBinding: FragmentMainBinding
    private var isLoading = false
    private lateinit var results: Result
    private lateinit var searchForValue: String
    private lateinit var adapter: CustomSearchAdapter
    private var page = 1
    private val pageSize = 20

    val baseUrl = "https://api.github.com/search/repositories?q="

    private fun userAvatarClickListener(searchResult: SearchResult) {
        val bundle = Bundle()
        bundle.putString("user", searchResult.owner.login)
        Navigation.findNavController(mainBinding.root).navigate(R.id.userInfoFragment, bundle)
    }

    private fun repoClickListener(searchResult: SearchResult) {
        val bundle = Bundle()
        bundle.putString("ownerName", searchResult.owner.login)
        bundle.putString("repoName", searchResult.name)
        Navigation.findNavController(mainBinding.root).navigate(R.id.repoFragment, bundle)
    }

    private fun fetchData(toSearch: String?, page: Int, sort: String) {
        isLoading = true
        mainBinding.progressBarMain.visibility = VISIBLE

        val urlWithPages = baseUrl + toSearch?.replace(" ", "+") + "&per_page=$pageSize&page=$page"
        val sortBy = (sort.split("-")[0]).toLowerCase()
        val sortDirection = if ("asc".equals(sort.split("-")[1])) { "asc" } else { "desc"  }
        val finalUrl = "$urlWithPages&sort=$sortBy&order=$sortDirection"

        val request = Request.Builder().url(finalUrl).build();
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context, "Unable to load, check mobile data", Toast.LENGTH_LONG)
                    .show()
                isLoading = false
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response?.body?.string()
                val gson = GsonBuilder().create()
                val newResults = gson.fromJson(res, Result::class.java)

                if (::results.isInitialized && !results.items.isEmpty()) {
                    results.items.addAll(newResults.items)
                    results.total_count = newResults.total_count
                    Handler(Looper.getMainLooper()).post {
                        mainBinding.progressBarMain.visibility = INVISIBLE
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    results = newResults
                    Handler(Looper.getMainLooper()).post {
                        adapter = CustomSearchAdapter(
                            results,
                            { searchResult -> userAvatarClickListener(searchResult) },
                            { searchResult -> repoClickListener(searchResult) })
                        recycleView.adapter = adapter
                        recycleView.layoutManager = LinearLayoutManager(context)
                        mainBinding.progressBarMain.visibility = INVISIBLE
                    }
                }
                isLoading = false
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainBinding = FragmentMainBinding.inflate(layoutInflater)

        if (::results.isInitialized)
            results.items.clear()

        mainBinding.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (::results.isInitialized && ::searchForValue.isInitialized) {
                    results.items.clear()
                    if (!isLoading) {
                        fetchData(
                            searchForValue,
                            page,
                            parent?.getItemAtPosition(position).toString()
                        )
                    }
                }
            }
        }


        mainBinding.recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (::results.isInitialized) {
                    if (!recyclerView.canScrollVertically(1)) {
                        if (results.items.size < results.total_count)
//                            if (!isLoading)
                            fetchData(searchForValue, ++page, spinner.selectedItem.toString())
                    } else if (!recyclerView.canScrollVertically(0)) {
                        if (results.items.size > 0)
//                            if (!isLoading)
                            fetchData(searchForValue, --page, spinner.selectedItem.toString())
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        mainBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    searchForValue = p0
                    page = 1
                }
                if (::results.isInitialized)
                    results.items.clear()
                if (!isLoading)
                    fetchData(p0, page, spinner.selectedItem.toString())
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })

        return mainBinding.root;
    }

}
