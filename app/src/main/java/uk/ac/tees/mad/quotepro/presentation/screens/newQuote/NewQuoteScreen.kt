package uk.ac.tees.mad.quotepro.presentation.screens.newQuote

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.components.CameraOptionsBottomSheet
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.components.ClientDetailsSection
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.components.CurrencyPickerDialog
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.components.QuotePreviewDialog
import uk.ac.tees.mad.quotepro.presentation.screens.newQuote.components.ServiceItemRow
import uk.ac.tees.mad.quotepro.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewQuoteScreen(
    navController: NavController,
    quoteId: String? = null,
    viewModel: NewQuoteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Camera and Gallery Launchers
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentCameraTarget by remember { mutableStateOf<CameraTarget?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            when (currentCameraTarget) {
                CameraTarget.LOGO -> viewModel.onEvent(
                    NewQuoteUiEvent.LogoCaptured(cameraImageUri.toString())
                )
                CameraTarget.SIGNATURE -> viewModel.onEvent(
                    NewQuoteUiEvent.SignatureCaptured(cameraImageUri.toString())
                )
                null -> {}
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            when (currentCameraTarget) {
                CameraTarget.LOGO -> viewModel.onEvent(
                    NewQuoteUiEvent.LogoCaptured(it.toString())
                )
                CameraTarget.SIGNATURE -> viewModel.onEvent(
                    NewQuoteUiEvent.SignatureCaptured(it.toString())
                )
                null -> {}
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraImageUri = createImageUri(context)
            cameraLauncher.launch(cameraImageUri!!)
        } else {
            context.showToast("Camera permission denied")
        }
    }

    // Collect effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NewQuoteUiEffect.ShowToast -> context.showToast(effect.message)
                is NewQuoteUiEffect.ShowError -> context.showToast(effect.error)
                is NewQuoteUiEffect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (quoteId != null) "Edit Quote" else "New Quote"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (state.isOfflineMode) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Client Details Section
                ClientDetailsSection(
                    clientName = state.clientName,
                    clientEmail = state.clientEmail,
                    clientAddress = state.clientAddress,
                    validationErrors = state.validationErrors,
                    onEvent = viewModel::onEvent
                )

                // Services Section
                ServicesSection(
                    services = state.services,
                    validationErrors = state.validationErrors,
                    onEvent = viewModel::onEvent
                )

                // Currency Selection
                CurrencySection(
                    selectedCurrency = state.selectedCurrency,
                    currencySymbol = state.currencySymbol,
                    totalAmount = state.totalAmount,
                    onCurrencyClick = {
                        viewModel.onEvent(NewQuoteUiEvent.ShowCurrencyPicker)
                    }
                )

                // Logo and Signature Section
                AttachmentsSection(
                    logoUri = state.logoUri,
                    signatureUri = state.signatureUri,
                    onAddLogo = {
                        currentCameraTarget = CameraTarget.LOGO
                        viewModel.onEvent(NewQuoteUiEvent.ShowCameraOption)
                    },
                    onAddSignature = {
                        currentCameraTarget = CameraTarget.SIGNATURE
                        viewModel.onEvent(NewQuoteUiEvent.ShowCameraOption)
                    }
                )

                // Save Button
                Button(
                    onClick = { viewModel.onEvent(NewQuoteUiEvent.SaveQuoteClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (quoteId != null) "Update Quote" else "Save Quote")
                }
            }

            // Loading Overlay
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Currency Picker Dialog
        if (state.showCurrencyPicker) {
            CurrencyPickerDialog(
                currencies = state.availableCurrencies,
                selectedCurrency = state.selectedCurrency,
                onCurrencySelected = { currency ->
                    viewModel.onEvent(NewQuoteUiEvent.CurrencySelected(currency.code))
                },
                onDismiss = { viewModel.onEvent(NewQuoteUiEvent.DismissDialog) }
            )
        }

        // Camera Options Bottom Sheet
        if (state.showCameraOptions) {
            CameraOptionsBottomSheet(
                onCameraClick = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onGalleryClick = {
                    galleryLauncher.launch("image/*")
                },
                onDismiss = { viewModel.onEvent(NewQuoteUiEvent.DismissDialog) }
            )
        }

        // Preview Dialog
        if (state.showPreviewDialog) {
            QuotePreviewDialog(
                state = state,
                onConfirm = { viewModel.confirmSaveQuote() },
                onDismiss = { viewModel.onEvent(NewQuoteUiEvent.DismissDialog) }
            )
        }
    }
}

@Composable
private fun ServicesSection(
    services: List<ServiceItemUi>,
    validationErrors: ValidationErrors,
    onEvent: (NewQuoteUiEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Services",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = { onEvent(NewQuoteUiEvent.AddServiceItem) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Service",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            services.forEachIndexed { index, service ->
                ServiceItemRow(
                    service = service,
                    index = index,
                    canRemove = services.size > 1,
                    validationError = validationErrors.services[service.id],
                    onEvent = onEvent
                )

                if (index < services.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun CurrencySection(
    selectedCurrency: String,
    currencySymbol: String,
    totalAmount: Double,
    onCurrencyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CurrencyExchange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Currency & Total",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onCurrencyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AttachMoney, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("$selectedCurrency ($currencySymbol)")
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$currencySymbol${String.format("%.2f", totalAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentsSection(
    logoUri: String?,
    signatureUri: String?,
    onAddLogo: () -> Unit,
    onAddSignature: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Attachments",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAddLogo,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (logoUri != null) Icons.Default.CheckCircle else Icons.Default.Image,
                        contentDescription = null,
                        tint = if (logoUri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (logoUri != null) "Logo Added" else "Add Logo")
                }

                OutlinedButton(
                    onClick = onAddSignature,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (signatureUri != null) Icons.Default.CheckCircle else Icons.Default.Edit,
                        contentDescription = null,
                        tint = if (signatureUri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (signatureUri != null) "Sign Added" else "Add Sign")
                }
            }
        }
    }
}

// Helper function to create image URI
private fun createImageUri(context: android.content.Context): Uri {
    val imageFile = java.io.File(
        context.cacheDir,
        "quote_image_${System.currentTimeMillis()}.jpg"
    )
    return androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}