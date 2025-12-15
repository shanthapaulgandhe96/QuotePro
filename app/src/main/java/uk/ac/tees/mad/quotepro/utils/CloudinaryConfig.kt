package uk.ac.tees.mad.quotepro.utils

object CloudinaryConfig {
    // Your Cloudinary credentials
    const val CLOUD_NAME = "ddr0ojyzf"
    const val API_KEY = "985663646288312"
    const val API_SECRET = "dfJVbXR9jBoYyVtswAveuj2fKvA"

    // IMPORTANT: Upload preset for unsigned uploads
    // Make sure this matches the preset name in your Cloudinary dashboard
    const val UPLOAD_PRESET = "quotepro_unsigned"

    // Folder structure
    const val LOGOS_FOLDER = "quotepro/logos"
    const val SIGNATURES_FOLDER = "quotepro/signatures"

    // Transformation options (applied server-side)
    object Transformations {
        const val LOGO_WIDTH = 500
        const val LOGO_HEIGHT = 500
        const val SIGNATURE_WIDTH = 300
        const val SIGNATURE_HEIGHT = 150
        const val QUALITY = "auto"
        const val FORMAT = "auto"
    }
}