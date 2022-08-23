package com.hyuck.model.service

import com.hyuck.model.board.Board
import com.hyuck.model.board.BoardRepository
import io.jsonwebtoken.Jwts
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class BoardService(private val repository: BoardRepository) {
    fun save(board: Board): Board {  // 게시판 데이터 저장
        return repository.save(board)
    }

    fun findById(id: Long): Board? {  // 게시판 id로 찾기
        return repository.findByIdOrNull(id)
    }

    fun getTokenUserData(token: String): Long {
         return Jwts.parser().setSigningKey("secret").parseClaimsJws(token).body.issuer.toLong()
    }

    fun update(id: Long, board: Board): Board {
        return if(repository.existsById(id)){
            board.id = id
            repository.save(board)
        } else throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    fun remove(id: Long){
        if(repository.existsById(id)) repository.deleteById(id)
        else throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}