package com.hyuck.controller

import com.hyuck.dtos.Message
import com.hyuck.dtos.board.CreateDTO
import com.hyuck.dtos.board.UpdateDTO
import com.hyuck.model.board.Board
import com.hyuck.model.service.BoardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/users/boards")
@RestController
class BoardRestController(private val service: BoardService) {

    @GetMapping("/{id}")
    fun GetBoard(@PathVariable id: Long, @CookieValue("jwt") jwt: String?): ResponseEntity<Any>{
        val board: Board? = service.findById(id)
        if(jwt == null){  // 토큰 검사
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        val userdataid = service.getTokenUserData(jwt)
        if (board == null){  // 게시판 유무 검사
            return ResponseEntity.badRequest().body(Message("Not Found Board Id $id"))
        } else if (!board.look && board.userid != userdataid){  // 자신만 보기 체크
            return ResponseEntity.badRequest().body(Message("Only Writer Can Check!"))
        } else {  // 문제없음
            return ResponseEntity.ok(board)
        }
    }

    @PostMapping
    fun SaveBoard(@RequestBody body: CreateDTO, @CookieValue("jwt") jwt: String?): ResponseEntity<Any>{
        if(jwt == null){
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        } else {
            val userdataid = service.getTokenUserData(jwt)
            val board = Board()
            board.title = body.title
            board.des = body.des
            board.userid = userdataid
            board.look = body.look
            return ResponseEntity.ok(service.save(board))
        }
    }

    @PutMapping("/{id}")
    fun UpdateBoard(@PathVariable id: Long, @RequestBody body: UpdateDTO, @CookieValue("jwt") jwt: String?): ResponseEntity<Any>{
        val board: Board? = service.findById(id)
        if(jwt == null){
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        val userdataid = service.getTokenUserData(jwt)
        if (board == null){  // 게시판 유무 검사
            return ResponseEntity.badRequest().body(Message("Not Found Board Id $id"))
        } else if (board.userid == userdataid){  // 본인 글인지 체크
            board.title = body.title
            board.des = body.des
            board.look = body.look
            return ResponseEntity.ok(service.update(id,board))
        } else {  // 본인 글이 아님
            return ResponseEntity.badRequest().body(Message("Only Writer Can Edit!"))
        }
    }

    @DeleteMapping("/{id}")
    fun DeleteBoard(@PathVariable id: Long, @CookieValue("jwt") jwt: String?): ResponseEntity<Any>{
        val board: Board? = service.findById(id)
        if(jwt == null){
            return ResponseEntity.status(401).body(Message("Unauthenticated"))
        }
        val userdataid = service.getTokenUserData(jwt)
        if (board == null){  // 게시판 유무 검사
            return ResponseEntity.badRequest().body(Message("Not Found Board Id $id"))
        } else if (board.userid != userdataid){  // 본인 글인지 체크
            return ResponseEntity.badRequest().body(Message("Only Writer Can Delete!"))
        } else {  // 본인 글일 때
            service.remove(id)
            return ResponseEntity.ok(Message("delete success!"))
        }
    }
}