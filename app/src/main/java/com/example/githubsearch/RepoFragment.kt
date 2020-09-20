package com.example.githubsearch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.githubsearch.databinding.FragmentRepoBinding
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class RepoFragment : Fragment() {

    private lateinit var ownerName: String
    private lateinit var repoName: String

    private lateinit var fragmentRepoBinding: FragmentRepoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ownerName = it.getString("ownerName") + ""
            repoName = it.getString("repoName") + ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRepoBinding = FragmentRepoBinding.inflate(layoutInflater)
        fetchData(ownerName, repoName)

        return fragmentRepoBinding.root
    }

    private fun fetchData(ownerName: String, repoName: String) {
        val url = "https://api.github.com/repos/$ownerName/$repoName"
        val request = Request.Builder().url(url).build();
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context, "Unable to load, check mobile data", Toast.LENGTH_LONG)
                    .show()
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                val gson = GsonBuilder().create()
                val gsonResponse = gson.fromJson(res, Repository::class.java)

                Handler(Looper.getMainLooper()).post {
                    fragmentRepoBinding.textViewRepoCreated.text = gsonResponse.created_at
                    fragmentRepoBinding.textViewRepositoryName.text = gsonResponse.name
                    fragmentRepoBinding.textViewRepoUpdate.text = gsonResponse.updated_at
                    fragmentRepoBinding.textViewRepoForks.text = gsonResponse.forks_count
                    fragmentRepoBinding.textViewRepoLang.text = gsonResponse.language
                    fragmentRepoBinding.buttonRepoOpenOwner.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("user", gsonResponse.owner.login)
                        Navigation.findNavController(fragmentRepoBinding.root).navigate(R.id.userInfoFragment, bundle)
                    }
                }
            }
        })
    }

}
