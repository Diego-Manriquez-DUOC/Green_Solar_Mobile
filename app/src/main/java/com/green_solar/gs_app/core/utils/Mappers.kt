package com.green_solar.gs_app.core.utils

import com.green_solar.gs_app.data.remote.dto.UserDto
import com.green_solar.gs_app.domain.model.User

/**
 * Agrega la funcion toDomain() a UserDto para poder mapearlo a User.
 * Por ahora el mapeo es igual a el modelo User, el cual es igual al que provee la API.
 */
fun UserDto.toDomain(): User {
    return User(
        user_id = this.user_id,
        name = this.name,
        email = this.email,
        img_url = this.img_url
    )
}
