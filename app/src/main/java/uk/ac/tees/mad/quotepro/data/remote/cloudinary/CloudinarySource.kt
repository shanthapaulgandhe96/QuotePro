package uk.ac.tees.mad.quotepro.data.remote.cloudinary

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import uk.ac.tees.mad.quotepro.utils.CloudinaryConfig
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinarySource @Inject constructor(
    private val context: Context
) {

    init {
        initializeCloudinary()
    }

    private fun initializeCloudinary() {
        try {
            // Initialize MediaManager with your credentials
            val config = mapOf(
                "cloud_name" to CloudinaryConfig.CLOUD_NAME,
                "api_key" to CloudinaryConfig.API_KEY,
                "api_secret" to CloudinaryConfig.API_SECRET
            )
            MediaManager.init(context, config)
        } catch (e: Exception) {
            // MediaManager already initialized
        }
    }

    suspend fun uploadLogo(userId: String, imageUri: Uri): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val publicId = "logos/${userId}_${System.currentTimeMillis()}"

                val requestId = MediaManager.get().upload(imageUri)
                    .option("folder", CloudinaryConfig.LOGOS_FOLDER)
                    .option("public_id", publicId)
                    .option("transformation", mapOf(
                        "width" to CloudinaryConfig.Transformations.LOGO_WIDTH,
                        "height" to CloudinaryConfig.Transformations.LOGO_HEIGHT,
                        "crop" to "limit",
                        "quality" to CloudinaryConfig.Transformations.QUALITY,
                        "fetch_format" to CloudinaryConfig.Transformations.FORMAT
                    ))
                    .option("resource_type", "image")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {
                            // Upload started
                        }

                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            // Progress update
                        }

                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            val secureUrl = resultData["secure_url"] as? String
                            if (secureUrl != null) {
                                continuation.resume(Result.success(secureUrl))
                            } else {
                                continuation.resume(Result.failure(Exception("No URL in response")))
                            }
                        }

                        override fun onError(requestId: String, error: ErrorInfo) {
                            continuation.resume(
                                Result.failure(Exception(error.description ?: "Upload failed"))
                            )
                        }

                        override fun onReschedule(requestId: String, error: ErrorInfo) {
                            // Will retry
                        }
                    })
                    .dispatch()

                continuation.invokeOnCancellation {
                    MediaManager.get().cancelRequest(requestId)
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }

    suspend fun uploadSignature(userId: String, imageUri: Uri): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val publicId = "signatures/${userId}_${System.currentTimeMillis()}"

                val requestId = MediaManager.get().upload(imageUri)
                    .option("folder", CloudinaryConfig.SIGNATURES_FOLDER)
                    .option("public_id", publicId)
                    .option("transformation", mapOf(
                        "width" to CloudinaryConfig.Transformations.SIGNATURE_WIDTH,
                        "height" to CloudinaryConfig.Transformations.SIGNATURE_HEIGHT,
                        "crop" to "limit",
                        "quality" to CloudinaryConfig.Transformations.QUALITY,
                        "fetch_format" to CloudinaryConfig.Transformations.FORMAT
                    ))
                    .option("resource_type", "image")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {
                            // Upload started
                        }

                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            // Progress update
                        }

                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            val secureUrl = resultData["secure_url"] as? String
                            if (secureUrl != null) {
                                continuation.resume(Result.success(secureUrl))
                            } else {
                                continuation.resume(Result.failure(Exception("No URL in response")))
                            }
                        }

                        override fun onError(requestId: String, error: ErrorInfo) {
                            continuation.resume(
                                Result.failure(Exception(error.description ?: "Upload failed"))
                            )
                        }

                        override fun onReschedule(requestId: String, error: ErrorInfo) {
                            // Will retry
                        }
                    })
                    .dispatch()

                continuation.invokeOnCancellation {
                    MediaManager.get().cancelRequest(requestId)
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }

    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            // Extract public_id from Cloudinary URL
            val publicId = extractPublicIdFromUrl(imageUrl)

            if (publicId != null) {
                // Note: Deletion requires admin/backend API call
                // For client-side, we typically don't delete (let Cloudinary handle cleanup)
                // Or implement backend API for secure deletion
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid Cloudinary URL"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractPublicIdFromUrl(url: String): String? {
        return try {
            // Cloudinary URL format: https://res.cloudinary.com/{cloud_name}/{resource_type}/{type}/{version}/{public_id}.{format}
            val parts = url.split("/")
            val indexOfUpload = parts.indexOf("upload")
            if (indexOfUpload != -1 && indexOfUpload + 2 < parts.size) {
                // Get public_id (everything after version, without extension)
                val publicIdWithExt = parts.subList(indexOfUpload + 2, parts.size).joinToString("/")
                publicIdWithExt.substringBeforeLast(".")
            } else null
        } catch (e: Exception) {
            null
        }
    }
}