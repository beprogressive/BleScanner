package com.example.myapplication.data.api.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserResponse(

	@SerialName("roles")
	val roles: List<String?>? = null,

	@SerialName("profile")
	val profile: Profile? = null,

	@SerialName("name")
	val name: String? = null,

	@SerialName("id")
	val id: Int? = null,

	@SerialName("isActive")
	val isActive: Boolean? = null,

	@SerialName("email")
	val email: String? = null
)

@Serializable
data class Profile(

	@SerialName("avatarUrl")
	val avatarUrl: String? = null,

	@SerialName("bio")
	val bio: String? = null,

	@SerialName("age")
	val age: Int? = null
)
