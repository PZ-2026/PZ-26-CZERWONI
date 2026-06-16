package pl.edu.ur.teachly.ui.profile.views

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import java.io.File
import org.koin.androidx.compose.koinViewModel
import pl.edu.ur.teachly.R
import pl.edu.ur.teachly.ui.components.auth.AuthTextField
import pl.edu.ur.teachly.ui.components.other.AppHeader
import pl.edu.ur.teachly.ui.components.other.ErrorBanner
import pl.edu.ur.teachly.ui.components.other.HeaderBackground
import pl.edu.ur.teachly.ui.components.other.InitialsAvatar
import pl.edu.ur.teachly.ui.components.other.hasCustomAvatar
import pl.edu.ur.teachly.ui.components.other.uriToFile
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.dialog.AppConfirmDialog
import pl.edu.ur.teachly.ui.profile.viewmodels.ProfileViewModel
import pl.edu.ur.teachly.ui.theme.AvatarColors
import pl.edu.ur.teachly.ui.theme.headerGradientColors

@Composable
fun ProfileEditScreen(onBack: () -> Unit, onSave: (Boolean) -> Unit, viewModel: ProfileViewModel = koinViewModel()) {
    val editState by viewModel.editState.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.startEditing() }

    LaunchedEffect(editState.isSaved) {
        if (editState.isSaved) {
            onSave(editState.requiresRelogin)
            viewModel.resetEditState()
        }
    }

    var showPickerDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraFile by remember { mutableStateOf<File?>(null) }

    val cropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent
            if (croppedUri != null) {
                val file = uriToFile(context, croppedUri)
                if (file != null && file.exists()) {
                    if (file.length() > 5 * 1024 * 1024) {
                        Toast.makeText(
                            context,
                            "Plik jest za duży. Maksymalny rozmiar to 5 MB.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@rememberLauncherForActivityResult
                    }
                    val extension = file.extension.lowercase()
                    if (extension != "jpg" && extension != "jpeg" && extension != "png") {
                        Toast.makeText(
                            context,
                            "Niedozwolony format pliku. Dozwolone są tylko JPG i PNG.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@rememberLauncherForActivityResult
                    }

                    viewModel.setPendingAvatar(file)
                    Toast.makeText(
                        context,
                        "Zdjęcie profilowe zostało wybrane (zapisz zmiany, aby zatwierdzić)",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(context, "Nie udało się odczytać pliku obrazu", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val error = result.error
            error?.printStackTrace()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val cropOptions = CropImageContractOptions(
                uri = uri,
                cropImageOptions = CropImageOptions(
                    guidelines = CropImageView.Guidelines.ON,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    fixAspectRatio = true,
                    cropShape = CropImageView.CropShape.RECTANGLE
                )
            )
            cropLauncher.launch(cropOptions)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            val cropOptions = CropImageContractOptions(
                uri = tempCameraUri,
                cropImageOptions = CropImageOptions(
                    guidelines = CropImageView.Guidelines.ON,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    fixAspectRatio = true,
                    cropShape = CropImageView.CropShape.RECTANGLE
                )
            )
            cropLauncher.launch(cropOptions)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                val file = File.createTempFile("avatar_capture_", ".jpg", context.cacheDir)
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                tempCameraFile = file
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(
                context,
                "Uprawnienie do aparatu jest wymagane, aby zrobić zdjęcie",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        var showConfirmBackDialog by remember { mutableStateOf(false) }

        val isDirty = editState.firstName.trim() != profile.firstName.trim() ||
            editState.lastName.trim() != profile.lastName.trim() ||
            editState.email.trim() != profile.email.trim() ||
            editState.phoneNumber.filter { it.isDigit() } != (profile.phoneNumber ?: "").filter { it.isDigit() } ||
            editState.password.isNotEmpty() ||
            editState.pendingAvatarFile != null ||
            editState.pendingDeleteAvatar

        if (showConfirmBackDialog) {
            AppConfirmDialog(
                title = "Niezapisane zmiany",
                message = "Masz niezapisane zmiany. Czy na pewno chcesz wyjść bez zapisywania?",
                confirmText = "Tak, wyjdź",
                onDismiss = { showConfirmBackDialog = false },
                onConfirm = {
                    showConfirmBackDialog = false
                    onBack()
                },
                destructive = true
            )
        }

        AppHeader(
            title = "Edytuj profil",
            subtitle = "Zmień swoje dane",
            background = HeaderBackground.Diagonal(headerGradientColors()),
            onBack = {
                if (isDirty) {
                    showConfirmBackDialog = true
                } else {
                    onBack()
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            val initials =
                stringResource(
                    R.string.initials,
                    editState.firstName.firstOrNull() ?: "",
                    editState.lastName.firstOrNull() ?: ""
                )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                InitialsAvatar(
                    initials = initials,
                    avatarColor = AvatarColors[0],
                    avatarUrl =
                    editState.localAvatarUrl ?: if (editState.pendingDeleteAvatar) null else profile.avatarUrl,
                    size = 96.dp,
                    isEditable = profile.role != pl.edu.ur.teachly.data.model.UserRole.ADMIN,
                    onEditClick = { showPickerDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AuthTextField(
                label = stringResource(R.string.field_first_name),
                value = editState.firstName,
                onValueChange = viewModel::onFirstNameChange,
                placeholder = stringResource(R.string.first_name_placeholder),
                capitalize = true
            )
            AuthTextField(
                label = stringResource(R.string.field_last_name),
                value = editState.lastName,
                onValueChange = viewModel::onLastNameChange,
                placeholder = stringResource(R.string.last_name_placeholder),
                capitalize = true
            )
            AuthTextField(
                label = stringResource(R.string.email),
                value = editState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "twój@email.com"
            )
            AuthTextField(
                label = stringResource(R.string.phone),
                value = editState.phoneNumber,
                onValueChange = viewModel::onPhoneNumberChange,
                placeholder = "123 456 789",
                visualTransformation = pl.edu.ur.teachly.ui.components.other.PhoneVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )
            pl.edu.ur.teachly.ui.components.other.PasswordTextField(
                label = "Nowe hasło (opcjonalnie)",
                value = editState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Pozostaw puste by nie zmieniać"
            )
            if (editState.error != null) {
                ErrorBanner(message = editState.error!!)
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(R.string.profile_edit_save),
                onClick = { viewModel.saveProfile() },
                isLoading = editState.isLoading,
                modifier = Modifier.padding(bottom = 32.dp, top = 24.dp)
            )
        }
    }

    if (showPickerDialog) {
        val currentAvatarUrl =
            editState.localAvatarUrl ?: if (editState.pendingDeleteAvatar) null else profile.avatarUrl
        val hasCustomAvatar = hasCustomAvatar(currentAvatarUrl)

        AvatarPickerDialog(
            hasCustomAvatar = hasCustomAvatar,
            onDismiss = { showPickerDialog = false },
            onTakePhoto = {
                showPickerDialog = false
                val permission = Manifest.permission.CAMERA
                if (ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        val file = File.createTempFile("avatar_capture_", ".jpg", context.cacheDir)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )
                        tempCameraFile = file
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    permissionLauncher.launch(permission)
                }
            },
            onPickFromGallery = {
                showPickerDialog = false
                galleryLauncher.launch("image/*")
            },
            onDeleteAvatar = {
                showPickerDialog = false
                viewModel.setPendingDeleteAvatar()
                Toast.makeText(
                    context,
                    "Zdjęcie oznaczone do usunięcia (zapisz zmiany, aby zatwierdzić)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}
