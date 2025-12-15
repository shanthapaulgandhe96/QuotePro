package uk.ac.tees.mad.quotepro.data.remote.cloudinary

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import uk.ac.tees.mad.quotepro.utils.CloudinaryConfig
import javax.inject.Inject
import kotlin.coroutines.resume

class CloudinarySource @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val TAG = "CloudinarySource"
        private var isInitialized = false
    }

    init {
        initializeCloudinary()
    }

    private fun initializeCloudinary() {
        if (isInitialized) {
            Log.d(TAG, "Cloudinary already initialized")
            return
        }

        try {
            val config = HashMap<String, String>()
            config["cloud_name"] = CloudinaryConfig.CLOUD_NAME
            config["api_key"] = CloudinaryConfig.API_KEY
            config["api_secret"] = CloudinaryConfig.API_SECRET
            config["secure"] = "true"

            MediaManager.init(context, config)
            isInitialized = true
            Log.d(TAG, "Cloudinary initialized successfully")
        } catch (e: IllegalStateException) {
            // Already initialized
            isInitialized = true
            Log.d(TAG, "Cloudinary was already initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Cloudinary", e)
        }
    }

    suspend fun uploadLogo(userId: String, imageUri: Uri): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                Log.d(TAG, "Starting logo upload for URI: $imageUri")

                val timestamp = System.currentTimeMillis()
                val publicId = "logos/${userId}_$timestamp"

                val requestId = MediaManager.get().upload(imageUri)
                    .unsigned(CloudinaryConfig.UPLOAD_PRESET)
                    .option("folder", CloudinaryConfig.LOGOS_FOLDER)
                    .option("public_id", publicId)
                    .option("resource_type", "auto")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {
                            Log.d(TAG, "Upload started: $requestId")
                        }

                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            val progress = (bytes * 100 / totalBytes).toInt()
                            Log.d(TAG, "Upload progress: $progress%")
                        }

                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            Log.d(TAG, "Upload success: $resultData")
                            val secureUrl = resultData["secure_url"] as? String
                            if (secureUrl != null) {
                                Log.d(TAG, "Secure URL: $secureUrl")
                                continuation.resume(Result.success(secureUrl))
                            } else {
                                val error = "No secure_url in response"
                                Log.e(TAG, error)
                                continuation.resume(Result.failure(Exception(error)))
                            }
                        }

                        override fun onError(requestId: String, error: ErrorInfo) {
                            val errorMsg = "Upload failed: ${error.description} (Code: ${error.code})"
                            Log.e(TAG, errorMsg)
                            continuation.resume(Result.failure(Exception(errorMsg)))
                        }

                        override fun onReschedule(requestId: String, error: ErrorInfo) {
                            Log.d(TAG, "Upload rescheduled: ${error.description}")
                        }
                    })
                    .dispatch()

                continuation.invokeOnCancellation {
                    Log.d(TAG, "Upload cancelled: $requestId")
                    try {
                        MediaManager.get().cancelRequest(requestId)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error cancelling request", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting upload", e)
                continuation.resume(Result.failure(e))
            }
        }
    }

    suspend fun uploadSignature(userId: String, imageUri: Uri): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                Log.d(TAG, "Starting signature upload for URI: $imageUri")

                val timestamp = System.currentTimeMillis()
                val publicId = "signatures/${userId}_$timestamp"

                val requestId = MediaManager.get().upload(imageUri)
                    .unsigned(CloudinaryConfig.UPLOAD_PRESET)
                    .option("folder", CloudinaryConfig.SIGNATURES_FOLDER)
                    .option("public_id", publicId)
                    .option("resource_type", "auto")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {
                            Log.d(TAG, "Upload started: $requestId")
                        }

                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            val progress = (bytes * 100 / totalBytes).toInt()
                            Log.d(TAG, "Upload progress: $progress%")
                        }

                        override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                            Log.d(TAG, "Upload success: $resultData")
                            val secureUrl = resultData["secure_url"] as? String
                            if (secureUrl != null) {
                                Log.d(TAG, "Secure URL: $secureUrl")
                                continuation.resume(Result.success(secureUrl))
                            } else {
                                val error = "No secure_url in response"
                                Log.e(TAG, error)
                                continuation.resume(Result.failure(Exception(error)))
                            }
                        }

                        override fun onError(requestId: String, error: ErrorInfo) {
                            val errorMsg = "Upload failed: ${error.description} (Code: ${error.code})"
                            Log.e(TAG, errorMsg)
                            continuation.resume(Result.failure(Exception(errorMsg)))
                        }

                        override fun onReschedule(requestId: String, error: ErrorInfo) {
                            Log.d(TAG, "Upload rescheduled: ${error.description}")
                        }
                    })
                    .dispatch()

                continuation.invokeOnCancellation {
                    Log.d(TAG, "Upload cancelled: $requestId")
                    try {
                        MediaManager.get().cancelRequest(requestId)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error cancelling request", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting upload", e)
                continuation.resume(Result.failure(e))
            }
        }
    }

    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            val publicId = extractPublicIdFromUrl(imageUrl)
            if (publicId != null) {
                Log.d(TAG, "Image deletion requested for: $publicId")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid Cloudinary URL"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image", e)
            Result.failure(e)
        }
    }

    private fun extractPublicIdFromUrl(url: String): String? {
        return try {
            val parts = url.split("/")
            val indexOfUpload = parts.indexOf("upload")
            if (indexOfUpload != -1 && indexOfUpload + 2 < parts.size) {
                val publicIdWithExt = parts.subList(indexOfUpload + 2, parts.size).joinToString("/")
                publicIdWithExt.substringBeforeLast(".")
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting public ID", e)
            null
        }
    }
}