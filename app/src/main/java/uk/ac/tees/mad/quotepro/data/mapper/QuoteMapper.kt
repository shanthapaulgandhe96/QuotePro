package uk.ac.tees.mad.quotepro.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uk.ac.tees.mad.quotepro.data.local.entity.QuoteEntity
import uk.ac.tees.mad.quotepro.domain.model.Client
import uk.ac.tees.mad.quotepro.domain.model.Quote
import uk.ac.tees.mad.quotepro.domain.model.QuoteStatus
import uk.ac.tees.mad.quotepro.domain.model.Service

object QuoteMapper {

    private val gson = Gson()

    fun toEntity(quote: Quote): QuoteEntity {
        return QuoteEntity(
            id = quote.id,
            quoteNumber = quote.quoteNumber,
            userId = quote.userId,
            clientJson = gson.toJson(quote.client),
            servicesJson = gson.toJson(quote.services),
            currency = quote.currency,
            currencySymbol = quote.currencySymbol,
            exchangeRate = quote.exchangeRate,
            totalAmount = quote.totalAmount,
            logoUri = quote.logoUri,
            signatureUri = quote.signatureUri,
            status = quote.status.name,
            createdAt = quote.createdAt,
            updatedAt = quote.updatedAt,
            dueDate = quote.dueDate,
            isSynced = false
        )
    }

    fun toDomain(entity: QuoteEntity): Quote {
        val clientType = object : TypeToken<Client>() {}.type
        val client: Client = gson.fromJson(entity.clientJson, clientType)

        val servicesType = object : TypeToken<List<Service>>() {}.type
        val services: List<Service> = gson.fromJson(entity.servicesJson, servicesType)

        return Quote(
            id = entity.id,
            quoteNumber = entity.quoteNumber,
            userId = entity.userId,
            client = client,
            services = services,
            currency = entity.currency,
            currencySymbol = entity.currencySymbol,
            exchangeRate = entity.exchangeRate,
            totalAmount = entity.totalAmount,
            logoUri = entity.logoUri,
            signatureUri = entity.signatureUri,
            status = QuoteStatus.valueOf(entity.status),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            dueDate = entity.dueDate
        )
    }
}