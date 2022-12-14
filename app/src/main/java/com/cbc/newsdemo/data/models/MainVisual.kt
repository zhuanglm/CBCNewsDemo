package com.cbc.newsdemo.data.models

data class MainVisual(
    val id: Int?,
    val sourceId: String?,
    val type: String?,
    val url: String?,
    val width: Int?,
    val height: Int?,
    val title: String?,
    val description: String?,
    val credit: String?,
    val altText: String?,
    val createdAt: Long?,
    val modifiedAt: Long?,
    val readableCreatedAt: String?,
    val readableModifiedAt: String?,
    val useOriginalImage: Boolean?,
    val version: String?
)
