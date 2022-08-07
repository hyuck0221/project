package com.hyuck

import org.springframework.data.repository.CrudRepository

interface UserRepository:CrudRepository<User,Long>{
    fun findByUserId(userId:String):User
}

interface BoardRepository: CrudRepository<Board, Long> {

}