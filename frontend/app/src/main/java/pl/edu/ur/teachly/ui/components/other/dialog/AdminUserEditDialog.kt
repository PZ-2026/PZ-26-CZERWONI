package pl.edu.ur.teachly.ui.components.other.dialog

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import java.io.File
import pl.edu.ur.teachly.data.model.AdminUserUpdateRequest
import pl.edu.ur.teachly.data.model.UserResponse
import pl.edu.ur.teachly.data.model.UserRole
import pl.edu.ur.teachly.ui.components.other.InitialsAvatar
import pl.edu.ur.teachly.ui.components.other.PhoneVisualTransformation
import pl.edu.ur.teachly.ui.components.other.PrimaryButton
import pl.edu.ur.teachly.ui.components.other.authTextFieldColors
import pl.edu.ur.teachly.ui.theme.AvatarColors

@Composable
fun AdminUserEditDialog(
    user: UserResponse,
    onDismiss: () -> Unit,
    onSave: (AdminUserUpdateRequest, File?, Boolean) -> Unit
) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phoneNumber?.filter { it.isDigit() } ?: "") }
    var role by remember { mutableStateOf(user.role ?: UserRole.STUDENT) }

    var pendingAvatarFile by remember { mutableStateOf<File?>(null) }
    var pendingDeleteAvatar by remember { mutableStateOf(false) }
    var localAvatarUrl by remember { mutableStateOf<String?>(null) }

    var showPickerDialog by remember { mutableStateOf(false) }

    var tempCameraFile by remember { mutableStateOf<File?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher do przycinania zdjęć
    val cropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val croppedUri = result.uriContent
            if (croppedUri != null) {
                val file = uriToFile(context, croppedUri)
                if (file != null && file.exists()) {
                    // Walidacja rozmiaru pliku (max 5 MB)
                    if (file.length() > 5 * 1024 * 1024) {
                        Toast.makeText(
                            context,
                            "Plik jest za duży. Maksymalny rozmiar to 5 MB.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@rememberLauncherForActivityResult
                    }
                    // Walidacja formatu pliku
                    val extension = file.extension.lowercase()
                    if (extension != "jpg" && extension != "jpeg" && extension != "png") {
                        Toast.makeText(
                            context,
                            "Niedozwolony format pliku. Dozwolone są tylko JPG i PNG.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@rememberLauncherForActivityResult
                    }

                    pendingAvatarFile = file
                    pendingDeleteAvatar = false
                    localAvatarUrl = file.absolutePath
                } else {
                    Toast.makeText(context, "Nie udało się odczytać pliku obrazu", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val error = result.error
            error?.printStackTrace()
        }
    }

    // Launcher do wyboru obrazu z galerii
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

    // Launcher do robienia zdjęć aparatem
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

    // Launcher do uprawnień aparatu
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
                "Uprawnienie do aparatu jest wymagane do zrobienia zdjęcia",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val firstNameError = DialogValidation.firstNameError(firstName)
    val lastNameError = DialogValidation.lastNameError(lastName)
    val emailError = DialogValidation.emailError(email)
    val phoneError = DialogValidation.phoneError(phone)
    val isValid = DialogValidation.isAdminUserFormValid(firstName, lastName, email, phone)

    AppFormDialog(
        title = "Edytuj użytkownika",
        subtitle = "#${user.id} · ${user.firstName} ${user.lastName}",
        onDismiss = onDismiss,
        onConfirm = {
            onSave(
                AdminUserUpdateRequest(
                    firstName.trim(),
                    lastName.trim(),
                    email.trim(),
                    phone.trim(),
                    role
                ),
                pendingAvatarFile,
                pendingDeleteAvatar
            )
        },
        confirmEnabled = isValid
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val initials = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}"
            InitialsAvatar(
                initials = initials,
                avatarColor = AvatarColors[0],
                avatarUrl = localAvatarUrl ?: if (pendingDeleteAvatar) null else user.avatarUrl,
                size = 88.dp,
                isEditable = user.role != UserRole.ADMIN,
                onEditClick = { showPickerDialog = true }
            )
        }

        DialogSectionCard(title = "Dane kontaktowe") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { if (it.length <= DialogValidation.MAX_NAME_LENGTH) firstName = it },
                    label = { Text("Imię") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = DialogFieldShape,
                    colors = authTextFieldColors(),
                    isError = firstNameError != null,
                    supportingText = firstNameError?.let { error -> { Text(error) } }
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { if (it.length <= DialogValidation.MAX_NAME_LENGTH) lastName = it },
                    label = { Text("Nazwisko") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = DialogFieldShape,
                    colors = authTextFieldColors(),
                    isError = lastNameError != null,
                    supportingText = lastNameError?.let { error -> { Text(error) } }
                )
            }
            OutlinedTextField(
                value = email,
                onValueChange = { if (it.length <= DialogValidation.MAX_EMAIL_LENGTH) email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = DialogFieldShape,
                colors = authTextFieldColors(),
                isError = emailError != null,
                supportingText = emailError?.let { error -> { Text(error) } }
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { value ->
                    val digits = value.filter { it.isDigit() }
                    if (digits.length <= 9) phone = digits
                },
                label = { Text("Telefon") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = DialogFieldShape,
                colors = authTextFieldColors(),
                visualTransformation = PhoneVisualTransformation(),
                isError = phoneError != null,
                supportingText = phoneError?.let { error -> { Text(error) } }
            )
        }

        DialogSectionCard(title = "Rola") {
            DialogChipRow(
                entries = UserRole.entries,
                selected = role,
                onSelect = { role = it },
                label = { it.label }
            )
        }
    }

    if (showPickerDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showPickerDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 400.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Zdjęcie profilowe",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Wybierz źródło zdjęcia",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    PrimaryButton(
                        text = "Zrób zdjęcie",
                        onClick = {
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
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                permissionLauncher.launch(permission)
                            }
                        }
                    )
                    PrimaryButton(
                        text = "Wybierz z galerii",
                        onClick = {
                            showPickerDialog = false
                            galleryLauncher.launch("image/*")
                        }
                    )
                    val currentAvatarUrl = localAvatarUrl ?: if (pendingDeleteAvatar) null else user.avatarUrl
                    val hasCustomAvatar = !currentAvatarUrl.isNullOrBlank() &&
                        !currentAvatarUrl.equals("null", ignoreCase = true) &&
                        !currentAvatarUrl.contains("/null", ignoreCase = true) &&
                        !currentAvatarUrl.endsWith("/uploads/avatars/", ignoreCase = true) &&
                        currentAvatarUrl.contains("/")
                    if (hasCustomAvatar) {
                        PrimaryButton(
                            text = "Usuń zdjęcie",
                            onClick = {
                                showPickerDialog = false
                                pendingAvatarFile = null
                                pendingDeleteAvatar = true
                                localAvatarUrl = null
                            }
                        )
                    }
                    TextButton(
                        onClick = { showPickerDialog = false }
                    ) {
                        Text("Anuluj", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

private fun uriToFile(context: android.content.Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("avatar_upload_", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.use { input ->
                input.copyTo(outputStream)
            }
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
