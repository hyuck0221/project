package com.hyuck.model.user

import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> {
    fun findByUserId(userId: String): User?

    fun getById(id: Long): User
}