package com.cbc.newsdemo.data.models

data class Category(
    val id: Int?,
    val name: String?,
    val path: String?,
    val type: String?,
    val slug: String?,
    val priority: Int?,
    val priorityWhenInLined: Int?,
    val metadata: MetaData?,
    val image: String?,
    val bannerImage: String?
)
