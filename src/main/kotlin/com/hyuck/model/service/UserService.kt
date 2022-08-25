package com.hyuck.model.service

import com.hyuck.dtos.Message
import com.hyuck.dtos.user.LoginDTO
import com.hyuck.dtos.user.RegisterDTO
import com.hyuck.model.user.User
import com.hyuck.model.user.UserRepository
import io.jsonwebtoken.Jwts
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Service
class UserService(private val repository: UserRepository, private val jwtService: JwtService) {

    fun save(user: User): User{  // 유저 데이터 저장 (회원가입)
        return repository.save(user)
    }

    fun findByUserId(userId: String): User?{  // 유저 데이터 검색 (중복체크 and 로그인 시 계정 유무 체크)
        return repository.findByUserId(userId)
    }

    fun findById(id: Long): User{
        return repository.getById(id)
    }

    fun crypto(ss: String): String {
        val sha = MessageDigest.getInstance("SHA-256")
        val hexa = sha.digest(ss.toByteArray())
        val crypto_str = hexa.fold("", { str, it -> str + "%02x".format(it) })
        return crypto_str
    }

    fun register(body: RegisterDTO): ResponseEntity<Any>{
        if(findByUserId(body.userId) == null){
            val user = User()
            val cryptopass = crypto(body.password)
            user.userId = body.userId
            user.userId = body.userId
            user.password = cryptopass
            user.nick = body.nick
            return ResponseEntity.ok(save(user))
        } else {
            return ResponseEntity.badRequest().body(Message("User Already Registered!"))
        }
    }

    fun login(body: LoginDTO, response: HttpServletResponse): ResponseEntity<Any>{
        val user = findByUserId(body.userId) ?: return ResponseEntity.badRequest().body(Message("User Not Found!"))
        if(user.password == crypto(body.password)){
            jwtService.createCookie(user.id, response)
            return ResponseEntity.ok(Message("login Success!"))
        } else {
            return ResponseEntity.badRequest().body(Message("Invalid password!"))
        }
    }

    fun getuser(jwt: String): ResponseEntity<Any>{
        val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
        return ResponseEntity.ok(findById(body.issuer.toLong()))
    }

    fun logout(response: HttpServletResponse): ResponseEntity<Any>{
        var cookie = Cookie("jwt","")
        cookie.maxAge = 0
        response.addCookie(cookie)
        return ResponseEntity.ok(Message("LOGOUT success"))
    }


    fun getAll(): MutableIterable<User> = repository.findAll()

    fun getById(id: Long): User = repository.findByIdOrNull(id) ?:  //id로 검색해서 레코드를 가져오거나
        throw ResponseStatusException(HttpStatus.NOT_FOUND)             //특정 id가 존재하지 않으면 예외 발생

    fun create(user: User): User = repository.save(user)

    fun remove(id: Long){
        if(repository.existsById(id)) repository.deleteById(id)  //해당 아이디를 가진 레코드 제거
        else throw ResponseStatusException(HttpStatus.NOT_FOUND)  //아이디가 없다면 예외 발생
    }

    fun update(id: Long, user: User): User {
        return if(repository.existsById(id)){
            user.id = id
            repository.save(user)
        } else throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}