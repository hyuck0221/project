package com.hyuck.controller

import com.hyuck.dtos.user.LoginDTO
import com.hyuck.dtos.Message
import com.hyuck.dtos.user.RegisterDTO
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

    fun crypto(ss: String): String {
        val sha = MessageDigest.getInstance("SHA-256")
        val hexa = sha.digest(ss.toByteArray())
        val crypto_str = hexa.fold("", { str, it -> str + "%02x".format(it) })
        return crypto_str
    }


    @PostMapping("register")
    fun register(@RequestBody body: RegisterDTO): ResponseEntity<Any> {
        val user = User()
        if(service.findByUserId(body.userId) == null){  // 유저 중복이 없을 때
            val cryptopass = crypto(body.password)
            user.userId = body.userId
            user.password = cryptopass
            user.nick = body.nick
            return ResponseEntity.ok(service.save(user))
        } else{                                         // 유저 중복이 있을 때
            return ResponseEntity.badRequest().body(Message("User Already Registered!"))
        }
    }

    @PostMapping("login")
    fun login(@RequestBody body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any>{
        val user = service.findByUserId(body.userId)
            ?: return ResponseEntity.badRequest().body(Message("User Not Found!"))
            // 해당 유저가 없을 때 유저가 없음을 출력

        if(user.password == crypto(body.password)){ // 비밀번호 여부 true:로그인
            val issuer = user.id.toString()  // 토큰이 발행되는 유저의 아이디 저장
            val jwt = Jwts.builder()  // JWT 생성 (아래는 부가데이터)
                .setIssuer(issuer)    // 발행되는 유저(유저 아이디)
                .setExpiration(Date(System.currentTimeMillis()+60*24*1000))
                //토큰 제한시간 (위 숫자는 1일)
                .signWith(SignatureAlgorithm.HS256, "secret").compact()

            val cookie = Cookie("jwt", jwt)
            cookie.isHttpOnly = true
            response.addCookie(cookie)

            return ResponseEntity.ok(Message("login Success!"))
        } else {
            return ResponseEntity.badRequest().body(Message("Invalid password!"))
        }
    }

    @GetMapping("user")
    fun user(@CookieValue("jwt") jwt: String?): ResponseEntity<Any>{
        if (jwt == null){  // 토큰을 부여받지 않았으면 아래 오류 리턴
            return ResponseEntity.status(401).body(Message("unauthenticated"))
        }
        val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body

        return ResponseEntity.ok(service.findById(body.issuer.toLong()))
        // 토큰을 받은 유저의 id를 이용해 해당 유저의 정보 return
    }

    @PostMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any>{
        var cookie = Cookie("jwt","")
        cookie.maxAge = 0
        // 토큰이 없는 쿠키를 만들어 로그아웃한 유저에게 부여

        response.addCookie(cookie)

        return ResponseEntity.ok(Message("LOGOUT success"))
    }



    @GetMapping
    fun getAllUser() = service.getAll()
    //  /users 주소로 GET 할 시 유저 정보 모두를 불러옴

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long) = service.getById(id)
    //  /users/아이디 주소로 GET 할 시 해당 유저 정보 불러옴
    //  유저 정보가 없으면 에러 (에러 구분 가능)

    //@PostMapping
    //@ResponseStatus(HttpStatus.CREATED)
    //fun saveUser(@RequestBody user: User): User = service.create(user)
    //  /users 주소로 POST 할 시 유저 정보 저장

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id:Long) = service.remove(id)
    //  /user/아이디 주소로 DELETE 할 시 해당 유저 삭제
    //  유저 정보가 없으면 에러 (에러 구분 가능)

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User) = service.update(id, user)
    //  /user/아이디 주소로 PUT 할 시 유저 업데이트
    //  유저 정보가 없으면 에러 (에러 구분 가능)
}