package com.cbc.newsdemo.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey var id: Int?= null,
    val title: String?,
    val description: String?,
    val source: String?,
    val sourceId: String?,
    val version: String?,
    val publishedAt: Long?,
    val readablePublishedAt: String?,
    val updatedAt: Long?,
    val readableUpdatedAt: String?,
    val type: String?,
    val active: Boolean?,
    val draft: Boolean?,
    val embedTypes: String?,
    val typeAttributes: TypeAttributes?,
    val images: Image?,
    val language: String?
) : Serializable