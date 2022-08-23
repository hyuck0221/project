package com.hyuck.model.board

import org.springframework.data.repository.CrudRepository

interface BoardRepository: CrudRepository<Board, Long> {
    fun findBycrudid(userId:Long): Board
}