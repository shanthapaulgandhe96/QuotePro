package uk.ac.tees.mad.quotepro.domain.model

import java.util.Currency
import java.util.UUID

data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val companyName: String = "",
    val currency: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
