package com.hyuck.controller

import com.hyuck.dtos.user.LoginDTO
import com.hyuck.dtos.Message
import com.hyuck.dtos.user.RegisterDTO
import com.hyuck.model.service.Auth
import com.hyuck.model.service.JwtService
import com.hyuck.model.user.User
import com.hyuck.model.service.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.MessageDigest
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RequestMapping("users")
@RestController
class UserRestController(private val service: UserService){

    @Auth
    @PostMapping("register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<Any> {
        return service.register(body)
    }

    @Auth
    @PostMapping("login")
    fun login(@RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any>{
        return service.login(body, response)
    }

    @GetMapping("user")
    fun user(@CookieValue("jwt") jwt: String): ResponseEntity<Any>{
        return service.getuser(jwt)
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any>{
        return service.logout(response)
    }



    //@GetMapping
    //fun getAllUser() = service.getAll()
    //  /users 주소로 GET 할 시 유저 정보 모두를 불러옴

    //@GetMapping("/{id}")
    //fun getUser(@PathVariable id: Long) = service.getById(id)
    //  /users/아이디 주소로 GET 할 시 해당 유저 정보 불러옴
    //  유저 정보가 없으면 에러 (에러 구분 가능)

    //@PostMapping
    //@ResponseStatus(HttpStatus.CREATED)
    //fun saveUser(@RequestBody user: User): User = service.create(user)
    //  /users 주소로 POST 할 시 유저 정보 저장

    //@DeleteMapping("/{id}")
    //@ResponseStatus(HttpStatus.NO_CONTENT)
    //fun deleteUser(@PathVariable id:Long) = service.remove(id)
    //  /user/아이디 주소로 DELETE 할 시 해당 유저 삭제
    //  유저 정보가 없으면 에러 (에러 구분 가능)

   // @PutMapping("/{id}")
    //fun updateUser(@PathVariable id: Long, @RequestBody user: User) = service.update(id, user)
    //  /user/아이디 주소로 PUT 할 시 유저 업데이트
    //  유저 정보가 없으면 에러 (에러 구분 가능)
}