package com.cbc.newsdemo.data.models

data class ContextualHeadline(
    val contextId: Int?,
    val headline: String?,
    val contextualLineupSlug: String?,
    val pubQueueId: String?,
    val headlineType: String?
)
