package uk.ac.tees.mad.quotepro.domain.usecase.auth

import uk.ac.tees.mad.quotepro.domain.repo.FirebaseAuthRepo
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: FirebaseAuthRepo,
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.resetPassword(email)
    }
}