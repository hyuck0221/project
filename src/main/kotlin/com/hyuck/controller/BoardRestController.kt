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
    fun GetBoard(@PathVariable id: Long, @CookieValue("jwt") jwt: String): ResponseEntity<Any>{
        return service.getboard(id, jwt)
    }

    @PostMapping
    fun SaveBoard(@RequestBody body: CreateDTO, @CookieValue("jwt") jwt: String): ResponseEntity<Any>{
        return service.saveboard(body, jwt)
    }

    @PutMapping("/{id}")
    fun UpdateBoard(@PathVariable id: Long, @RequestBody body: UpdateDTO, @CookieValue("jwt") jwt: String): ResponseEntity<Any>{
        return service.updateboard(id, body, jwt)
    }

    @DeleteMapping("/{id}")
    fun DeleteBoard(@PathVariable id: Long, @CookieValue("jwt") jwt: String): ResponseEntity<Any>{
        return service.deleteboard(id, jwt)
    }
}