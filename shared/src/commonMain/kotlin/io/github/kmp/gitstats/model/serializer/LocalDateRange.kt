package io.github.kmp.gitstats.model.serializer

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * It uses a custom serializer to handle kotlinx.serialization of [LocalDate], which is not natively supported.
 */
@Serializable
data class LocalDateRange(
    @Serializable(with = LocalDateSerializer::class) val first: LocalDate,

    @Serializable(with = LocalDateSerializer::class) val last: LocalDate
)