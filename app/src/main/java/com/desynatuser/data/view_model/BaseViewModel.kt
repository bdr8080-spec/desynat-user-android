package com.desynatuser.data.view_model

import androidx.lifecycle.ViewModel
import com.desynatuser.data.repo.MainRepository
import com.desynatuser.model.ValidationResult
import com.desynatuser.validation.FieldValidator
import javax.inject.Inject

class BaseViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val progress = repository.progress
    val error = repository.error


    fun validateFields(
        email: String?,
        password: String?,
        phone: String?
    ): ValidationResult {
        val errors = mutableMapOf<String, String>()

        if (!FieldValidator.isEmailValid(email)) {
            errors["email"] = "Invalid email format"
        }

        if (!FieldValidator.isPasswordValid(password)) {
            errors["password"] = "Password must be at least 6 characters"
        }

        if (!FieldValidator.isPhoneValid(phone)) {
            errors["phone"] = "Phone number must be 10 digits"
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

}
