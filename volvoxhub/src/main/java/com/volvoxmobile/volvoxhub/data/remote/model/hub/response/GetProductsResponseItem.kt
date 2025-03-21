package com.volvoxmobile.volvoxhub.data.remote.model.hub.response


import com.google.gson.annotations.SerializedName

typealias GetProductsResponse = List<GetProductsResponseItem>

data class GetProductsResponseItem(
    @SerializedName("app_id")
    val appId: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("duration")
    val duration: Int?,
    @SerializedName("grace_period")
    val gracePeriod: Int?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("import_type")
    val importType: String?,
    @SerializedName("initial_bonus")
    val initialBonus: Int?,
    @SerializedName("is_consumable")
    val isConsumable: Boolean?,
    @SerializedName("renewal_bonus")
    val renewalBonus: Int?,
    @SerializedName("revenue_cat_id")
    val revenueCatId: String?,
    @SerializedName("store_identifier")
    val storeIdentifier: String?,
    @SerializedName("trial_duration")
    val trialDuration: Int?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)