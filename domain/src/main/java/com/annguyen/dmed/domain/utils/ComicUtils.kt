package com.annguyen.dmed.domain.utils

import com.annguyen.dmed.domain.model.Comic

fun Comic.getBackgroundUrl() = "${imageUrl}/background.$imageUrlExtension".ensureHttps()
fun Comic.getOriginalUrl() = "${imageUrl}/clean.$imageUrlExtension".ensureHttps()
fun Comic.getThumbnailUrl() = "${imageUrl}/standard_fantastic.$imageUrlExtension".ensureHttps()
private fun String.ensureHttps() =
    if (contains("http://")) replace("http://", "https://") else this