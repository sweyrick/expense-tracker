package com.expensetracker.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlinx.serialization.SerialName

@Serializable
data class UserRegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val registrationCode: String
)

@Serializable
data class UserLoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class UserResponse(
    @kotlinx.serialization.SerialName("id")
    val id: String,
    val username: String,
    val email: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    @kotlinx.serialization.SerialName("createdAt")
    val createdAt: OffsetDateTime
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse
)

@Serializable
data class IncomeRequest(
    val amount: Double,
    val description: String,
    val isRecurring: Boolean = false,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate? = null
)

@Serializable
data class IncomeResponse(
    @kotlinx.serialization.SerialName("id")
    val id: String,
    val amount: Double,
    val description: String,
    val isRecurring: Boolean,
    @Serializable(with = LocalDateSerializer::class)
    @kotlinx.serialization.SerialName("startDate")
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    @kotlinx.serialization.SerialName("endDate")
    val endDate: LocalDate? = null,
    val userId: String,
    val username: String
)

@Serializable
enum class ExpenseCategory {
    @SerialName("Groceries") GROCERIES,
    @SerialName("Rent") RENT,
    @SerialName("Utilities") UTILITIES,
    @SerialName("Transportation") TRANSPORTATION,
    @SerialName("Insurance") INSURANCE,
    @SerialName("Medical") MEDICAL,
    @SerialName("Entertainment") ENTERTAINMENT,
    @SerialName("Dining Out") DINING_OUT,
    @SerialName("Education") EDUCATION,
    @SerialName("Childcare") CHILDCARE,
    @SerialName("Kids Expenses") KIDS_EXPENSES,
    @SerialName("Clothing") CLOTHING,
    @SerialName("Personal Care") PERSONAL_CARE,
    @SerialName("Gifts") GIFTS,
    @SerialName("Donations") DONATIONS,
    @SerialName("Subscriptions") SUBSCRIPTIONS,
    @SerialName("Internet") INTERNET,
    @SerialName("Phone") PHONE,
    @SerialName("Home Maintenance") HOME_MAINTENANCE,
    @SerialName("Pets") PETS,
    @SerialName("Travel") TRAVEL,
    @SerialName("Taxes") TAXES,
    @SerialName("Loan Payments") LOAN_PAYMENTS,
    @SerialName("Savings") SAVINGS,
    @SerialName("Investments") INVESTMENTS,
    @SerialName("Misc") MISC
}

@Serializable
data class ExpenseRequest(
    val amount: Double,
    val category: ExpenseCategory,
    val description: String,
    val isRecurring: Boolean = false,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate? = null
)

@Serializable
data class ExpenseResponse(
    @kotlinx.serialization.SerialName("id")
    val id: String,
    val amount: Double,
    val category: ExpenseCategory,
    val description: String,
    val isRecurring: Boolean,
    @Serializable(with = LocalDateSerializer::class)
    @kotlinx.serialization.SerialName("startDate")
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    @kotlinx.serialization.SerialName("endDate")
    val endDate: LocalDate? = null,
    val userId: String,
    val username: String
)

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}

object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OffsetDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.parse(decoder.decodeString())
    }
}

data class UserPrincipal(val userId: String)
