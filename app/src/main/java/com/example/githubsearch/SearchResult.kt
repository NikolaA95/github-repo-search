package com.example.githubsearch

data class Result(val items:MutableList< SearchResult>, var total_count: Int)

data class SearchResult(val watchers_count: String, val name: String, val forks_count: Int, val open_issues_count: Int, val owner: Owner)

data class Owner(val login:String, val url:String, val avatar_url:String, val repos_url:String)

data class OwnerExtended(val login:String, val html_url:String, val avatar_url:String, val public_repos:Int, val type:String, val created_at:String) {}

data class Repository(val name:String, val created_at:String, val updated_at:String, val language:String, val forks_count:String, val owner: Owner){}


