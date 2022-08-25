package com.hyuck.model.service

import com.hyuck.dtos.Message
import com.hyuck.dtos.board.CreateDTO
import com.hyuck.dtos.board.UpdateDTO
import com.hyuck.model.board.Board
import com.hyuck.model.board.BoardRepository
import io.jsonwebtoken.Jwts
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
        else throw ResponseStatusException(HttpStatus.NOT_FOUND) //에러코드 수정
    }

    fun getboard(id: Long, jwt: String): ResponseEntity<Any>{
        val board: Board? = findById(id)
        val userdataid = getTokenUserData(jwt)
        if (board == null){
            return ResponseEntity.badRequest().body(Message("Not Found Board Id $id"))
        } else if (!board.look && board.userid != userdataid){  //402
            return ResponseEntity.badRequest().body(Message("Only Writer Can Check!"))
        } else {
            return ResponseEntity.ok(board)
        }
    }

    fun saveboard(body: CreateDTO, jwt: String): ResponseEntity<Any>{
        val userdataid = getTokenUserData(jwt)
        val board = Board()
        board.title = body.title
        board.des = body.des
        board.userid = userdataid
        board.look = body.look
        return ResponseEntity.ok(save(board))
    }

    fun updateboard(id:Long, body: UpdateDTO, jwt: String): ResponseEntity<Any>{
        val board: Board? = findById(id)
        val userdataid = getTokenUserData(jwt)
        if (board == null){
            return ResponseEntity.badRequest().body(Message("Not Found Board Id $id"))
        } else if (board.userid == userdataid){
            board.title = body.title
            board.des = body.des
            board.look = body.look
            return ResponseEntity.ok(update(id,board))
        } else {
            return ResponseEntity.badRequest().body(Message("Only Writer Can Edit!"))
        }
    }

    fun deleteboard(id: Long, jwt: String): ResponseEntity<Any>{
        val board: Board? = findById(id)
        val userdataid = getTokenUserData(jwt)
        if (board == null){  // 게시판 유무 검사
            return ResponseEntity.badRequest().body(Message("Not Found Board Id $id"))
        } else if (board.userid != userdataid){  // 본인 글인지 체크
            return ResponseEntity.badRequest().body(Message("Only Writer Can Delete!"))
        } else {  // 본인 글일 때
            remove(id)
            return ResponseEntity.ok(Message("delete success!"))
        }
    }
}