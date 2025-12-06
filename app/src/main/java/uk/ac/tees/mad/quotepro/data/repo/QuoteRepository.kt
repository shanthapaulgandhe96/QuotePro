package uk.ac.tees.mad.quotepro.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.quotepro.domain.model.Client
import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.model.Service
import uk.ac.tees.mad.quotepro.domain.repo.QuoteRepository
import java.util.UUID
import javax.inject.Inject

class QuoteRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : QuoteRepository {

    override suspend fun saveQuote(
        client: Client,
        service: String,
        rate: String,
        quantity: Double
    ): Result<Unit> = runCatching {

        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

        val serviceItem = Service(
            description = service,
            quantity = quantity,
            rate = rate.toDouble(),
            amount = rate.toDouble() * quantity
        )

        val newQuoteId = UUID.randomUUID().toString()

        val quote = Quote(
            id = newQuoteId,
            quoteNumber = "QT-${System.currentTimeMillis()}",
            userId = userId,
            client = client,
            services = listOf(serviceItem),
            totalAmount = serviceItem.amount
        )

        firestore
            .collection("quotes")
            .document(newQuoteId)
            .set(quote)
            .await()
    }
}
