package com.example.githubsearch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.githubsearch.databinding.FragmentUserInfoBinding
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_info.*
import okhttp3.*
import java.io.IOException

class UserInfoFragment : Fragment() {

    private lateinit var name: String
    private lateinit var fragmentUserInfoBinder: FragmentUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("user") + ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserInfoBinder = FragmentUserInfoBinding.inflate(layoutInflater)
        fetchJson(name)
        return fragmentUserInfoBinder.root
    }

    private fun fetchJson(name: String?) {
        val url = "https://api.github.com/users/$name"
        val request = Request.Builder().url(url).build();
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context, "Unable to load, check mobile data", Toast.LENGTH_LONG)
                    .show()
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                val res = response?.body?.string()
                val gson = GsonBuilder().create()
                val gsonResponse = gson.fromJson(res, OwnerExtended::class.java)

                Handler(Looper.getMainLooper()).post {
                    fragmentUserInfoBinder.textViewUserName.text = gsonResponse.login
                    fragmentUserInfoBinder.textViewUserRepoLink.text = gsonResponse.public_repos.toString()
                    fragmentUserInfoBinder.textViewUserAccType.text = gsonResponse.type
                    fragmentUserInfoBinder.textViewUserScore.text = gsonResponse.created_at

                    fragmentUserInfoBinder.buttonUserBrowser.setOnClickListener {
                        val url = gsonResponse.html_url
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
                fetchAvatar(gsonResponse.avatar_url, image_view_user)
            }
        })
    }

    private fun fetchAvatar(url: String, imageCointainer: ImageView) {
        Handler(Looper.getMainLooper()).post {
            val image = url
            Picasso.get()
                .load(image)
                .into(imageCointainer)
        }
    }
}
