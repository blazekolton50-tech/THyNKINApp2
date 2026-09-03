package com.patsy.app.auth

/** Pure, offline syntax checks. Availability, ownership and credentials still require the backend. */
object AuthValidation {
    private val usernamePattern = Regex("^[A-Za-z0-9](?:[A-Za-z0-9._]{1,28}[A-Za-z0-9])?$")
    private val emailPattern = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    fun username(raw: String): FieldValidation {
        val value = raw.trim()
        val issues = buildList {
            if (value.isEmpty()) add(ValidationIssue.Required)
            if (value.isNotEmpty() && value.length !in 3..30) {
                add(ValidationIssue.InvalidLength(minimum = 3, maximum = 30))
            }
            if (value.isNotEmpty() && !usernamePattern.matches(value)) {
                add(ValidationIssue.InvalidUsernameCharacters)
            }
        }
        return FieldValidation(normalizedValue = value, issues = issues)
    }

    fun email(raw: String): FieldValidation {
        val value = raw.trim().lowercase()
        val issues = buildList {
            if (value.isEmpty()) add(ValidationIssue.Required)
            if (value.length > 254) add(ValidationIssue.InvalidLength(maximum = 254))
            if (value.isNotEmpty() && !emailPattern.matches(value)) add(ValidationIssue.InvalidEmail)
        }
        return FieldValidation(normalizedValue = value, issues = issues)
    }

    fun password(password: CharSequence): PasswordValidation {
        val issues = buildList {
            if (password.isEmpty()) add(ValidationIssue.Required)
            if (password.length < 12) add(ValidationIssue.InvalidLength(minimum = 12))
            if (password.none(Char::isLowerCase)) add(ValidationIssue.PasswordNeedsLowercase)
            if (password.none(Char::isUpperCase)) add(ValidationIssue.PasswordNeedsUppercase)
            if (password.none(Char::isDigit)) add(ValidationIssue.PasswordNeedsDigit)
            if (password.none { !it.isLetterOrDigit() && !it.isWhitespace() }) {
                add(ValidationIssue.PasswordNeedsSymbol)
            }
            if (password.any(Char::isWhitespace)) add(ValidationIssue.PasswordContainsWhitespace)
        }
        return PasswordValidation(issues)
    }

    fun loginIdentifier(raw: String): LoginIdentifierValidation {
        val value = raw.trim()
        if (value.isEmpty()) {
            return LoginIdentifierValidation(null, listOf(ValidationIssue.Required))
        }

        return if ('@' in value) {
            val result = email(value)
            LoginIdentifierValidation(
                identifier = result.takeIf(FieldValidation::isValid)?.let {
                    LoginIdentifier.Email(it.normalizedValue)
                },
                issues = result.issues,
            )
        } else {
            val result = username(value)
            LoginIdentifierValidation(
                identifier = result.takeIf(FieldValidation::isValid)?.let {
                    LoginIdentifier.Username(it.normalizedValue)
                },
                issues = result.issues,
            )
        }
    }
}

data class FieldValidation(
    val normalizedValue: String,
    val issues: List<ValidationIssue>,
) {
    val isValid: Boolean get() = issues.isEmpty()
}

data class PasswordValidation(val issues: List<ValidationIssue>) {
    val isValid: Boolean get() = issues.isEmpty()
}

data class LoginIdentifierValidation(
    val identifier: LoginIdentifier?,
    val issues: List<ValidationIssue>,
) {
    val isValid: Boolean get() = identifier != null && issues.isEmpty()
}

sealed interface LoginIdentifier {
    val value: String

    data class Username(override val value: String) : LoginIdentifier
    data class Email(override val value: String) : LoginIdentifier
}

sealed interface ValidationIssue {
    data object Required : ValidationIssue
    data class InvalidLength(val minimum: Int? = null, val maximum: Int? = null) : ValidationIssue
    data object InvalidUsernameCharacters : ValidationIssue
    data object InvalidEmail : ValidationIssue
    data object PasswordNeedsLowercase : ValidationIssue
    data object PasswordNeedsUppercase : ValidationIssue
    data object PasswordNeedsDigit : ValidationIssue
    data object PasswordNeedsSymbol : ValidationIssue
    data object PasswordContainsWhitespace : ValidationIssue
}
