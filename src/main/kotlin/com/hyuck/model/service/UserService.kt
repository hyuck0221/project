package com.hyuck.model.service

import com.hyuck.model.user.User
import com.hyuck.model.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(private val repository: UserRepository) {

    fun save(user: User): User{  // 유저 데이터 저장 (회원가입)
        return repository.save(user)
    }

    fun findByUserId(userId: String): User?{  // 유저 데이터 검색 (중복체크 and 로그인 시 계정 유무 체크)
        return repository.findByUserId(userId)
    }

    fun findById(id: Long): User{
        return repository.getById(id)
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