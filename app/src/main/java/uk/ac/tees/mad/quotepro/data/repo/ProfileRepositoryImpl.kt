package uk.ac.tees.mad.quotepro.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.quotepro.domain.model.ProfileSettings
import uk.ac.tees.mad.quotepro.domain.repo.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ProfileRepository {

    override fun getUserProfile(): Flow<Result<ProfileSettings>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            trySend(Result.failure(Exception("User not logged in")))
            close()
            return@callbackFlow
        }

        val docRef = firestore.collection("users").document(currentUser.uid)

        // Listen to real-time updates
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(ProfileSettings::class.java)
                profile?.let { trySend(Result.success(it)) }
            } else {
                // If doc doesn't exist, return basic info from Auth
                val basicProfile = ProfileSettings(
                    uid = currentUser.uid,
                    name = currentUser.displayName ?: "",
                    email = currentUser.email ?: ""
                )
                trySend(Result.success(basicProfile))
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateUserProfile(profile: ProfileSettings): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val finalProfile = profile.copy(uid = uid) // Ensure UID is correct
        firestore.collection("users").document(uid).set(finalProfile).await()
    }

    override suspend fun uploadProfileImage(imageUri: String): Result<String> {
        // Placeholder for Cloudinary logic if needed, or delegated to existing ImageRepo
        return Result.success("https://placeholder.com/image.jpg")
    }
}