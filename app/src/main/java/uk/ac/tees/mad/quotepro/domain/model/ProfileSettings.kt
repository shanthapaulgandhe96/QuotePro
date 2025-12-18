package uk.ac.tees.mad.quotepro.domain.model

data class ProfileSettings(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val companyName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val taxId: String = "",
    val profileImageUrl: String = ""
)