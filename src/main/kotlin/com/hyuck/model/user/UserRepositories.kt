package com.hyuck.model.user

import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> {
    fun findByUserId(userId: String): User?
    // null이 가능하게 만들어 함수 실행 시 데이터가 null이면 해당 유저가 없음을 뜻함

}