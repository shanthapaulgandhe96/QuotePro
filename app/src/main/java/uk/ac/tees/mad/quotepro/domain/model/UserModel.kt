package uk.ac.tees.mad.quotepro.domain.model

data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val companyName: String = "",
    val preferredCurrency: String = "USD",
    val createdAt: Long = System.currentTimeMillis()
)