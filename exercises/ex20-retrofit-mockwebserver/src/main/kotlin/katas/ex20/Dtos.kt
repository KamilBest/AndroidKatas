package katas.ex20

// TODO: kotlinx.serialization annotations

data class AddressDto(val street: String, val city: String)

data class PharmacyDto(
    val id: String,
    val name: String,
    val address: AddressDto,
    val openingHours: String,
    val isOpen24h: Boolean,
    val rating: Double?,
)

data class PharmaciesPage(val items: List<PharmacyDto>, val page: Int, val totalPages: Int)

data class ReserveRequest(val pharmacyId: String, val items: List<String>)

data class ReserveResponse(val pickupCode: String)
