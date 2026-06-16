package pl.edu.ur.teachly.ui.components.other.dialog

import android.util.Patterns
import java.time.LocalTime
import kotlin.math.roundToLong

object DialogValidation {
    const val MAX_NAME_LENGTH = 50
    const val MAX_EMAIL_LENGTH = 100
    const val MAX_BIO_LENGTH = 2000
    const val MAX_NOTES_LENGTH = 500
    const val MAX_LABEL_LENGTH = 100
    const val MIN_HOURLY_RATE = 1.0
    const val MAX_HOURLY_RATE = 1000.0
    const val MAX_LESSON_AMOUNT = 1000.0

    private val DECIMAL_INPUT = Regex("^\\d{0,4}(\\.\\d{0,2})?$")

    fun filterDecimalInput(value: String): String? {
        val normalized = value.replace(',', '.')
        return if (normalized.isEmpty() || normalized.matches(DECIMAL_INPUT)) normalized else null
    }

    fun parseDecimal(value: String): Double? = value.replace(',', '.').toDoubleOrNull()

    fun formatEditableDecimal(value: Double): String {
        val rounded = (value * 100).roundToLong() / 100.0
        return "%.2f".format(rounded)
    }

    fun firstNameError(value: String): String? = when {
        value.length > MAX_NAME_LENGTH -> "Imię nie może przekraczać $MAX_NAME_LENGTH znaków"
        value.isNotBlank() && value.trim().isBlank() -> "Imię nie może być puste"
        else -> null
    }

    fun lastNameError(value: String): String? = when {
        value.length > MAX_NAME_LENGTH -> "Nazwisko nie może przekraczać $MAX_NAME_LENGTH znaków"
        value.isNotBlank() && value.trim().isBlank() -> "Nazwisko nie może być puste"
        else -> null
    }

    fun emailError(value: String): String? = when {
        value.length > MAX_EMAIL_LENGTH -> "Email nie może przekraczać $MAX_EMAIL_LENGTH znaków"
        value.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches() ->
            "Niepoprawny format adresu email"
        else -> null
    }

    fun phoneError(digits: String): String? = if (digits.isNotEmpty() && digits.length != 9) {
        "Numer telefonu musi składać się z 9 cyfr"
    } else {
        null
    }

    fun isAdminUserFormValid(firstName: String, lastName: String, email: String, phone: String): Boolean =
        firstName.trim().isNotBlank() &&
            lastName.trim().isNotBlank() &&
            email.trim().isNotBlank() &&
            Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
            phone.length == 9 &&
            firstName.length <= MAX_NAME_LENGTH &&
            lastName.length <= MAX_NAME_LENGTH &&
            email.length <= MAX_EMAIL_LENGTH

    fun hourlyRateError(value: String): String? {
        val rate = parseDecimal(value)
        return when {
            value.isBlank() -> null
            rate == null -> "Podaj prawidłową stawkę godzinową"
            rate < MIN_HOURLY_RATE -> "Stawka musi wynosić co najmniej 1 PLN"
            rate > MAX_HOURLY_RATE -> "Stawka nie może być większa niż 1 000 PLN"
            else -> null
        }
    }

    fun isHourlyRateValid(value: String): Boolean {
        val rate = parseDecimal(value) ?: return false
        return rate in MIN_HOURLY_RATE..MAX_HOURLY_RATE
    }

    fun lessonFormatError(offersOnline: Boolean, offersInPerson: Boolean): String? =
        if (!offersOnline && !offersInPerson) {
            "Wybierz co najmniej jedną formę zajęć (online lub stacjonarnie)"
        } else {
            null
        }

    fun lessonAmountError(value: String): String? {
        val amount = parseDecimal(value)
        return when {
            value.isBlank() -> null
            amount == null -> "Podaj prawidłową kwotę"
            amount <= 0.0 -> "Kwota musi być większa niż 0"
            amount > MAX_LESSON_AMOUNT -> "Kwota nie może być większa niż 1 000 PLN"
            else -> null
        }
    }

    fun isLessonAmountValid(value: String): Boolean {
        val amount = parseDecimal(value) ?: return false
        return amount > 0.0 && amount <= MAX_LESSON_AMOUNT
    }

    fun timeRangeError(from: String, to: String): String? {
        val timeFrom = runCatching { LocalTime.parse(from) }.getOrNull() ?: return null
        val timeTo = runCatching { LocalTime.parse(to) }.getOrNull() ?: return null
        return if (!timeTo.isAfter(timeFrom)) {
            "Godzina zakończenia musi być późniejsza niż godzina rozpoczęcia"
        } else {
            null
        }
    }

    fun isTimeRangeValid(from: String, to: String): Boolean = timeRangeError(from, to) == null

    fun labelNameError(value: String, fieldName: String): String? = when {
        value.length > MAX_LABEL_LENGTH -> "$fieldName nie może przekraczać $MAX_LABEL_LENGTH znaków"
        value.isNotBlank() && value.trim().isBlank() -> "$fieldName nie może być puste"
        else -> null
    }

    fun isLabelNameValid(value: String): Boolean = value.trim().isNotBlank() && value.length <= MAX_LABEL_LENGTH
}
