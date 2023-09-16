package com.prac.monolithic.awsmsamonolithicprac.controller

import com.prac.monolithic.awsmsamonolithicprac.dto.Credentials
import com.prac.monolithic.awsmsamonolithicprac.entity.User
import com.prac.monolithic.awsmsamonolithicprac.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    /**
     * POST - 새로운 사용자를 등록합니다. Request body 에는 email, name, password 가 포함되어야 합니다.
     * */
    @PostMapping("/register")
    fun registerUser(
        @RequestBody user: User
    ): ResponseEntity<User> {
        val registeredUser = userService.registerUser(user)

        return ResponseEntity.ok(registeredUser)
    }

    /**
     * POST - 사용자 로그인을 수행합니다. Request body 에는 email 과 password 가 포함되어야 합니다.
     * */
    @PostMapping("/login")
    fun loginUser(
        @RequestBody credentials: Credentials
    ): ResponseEntity<User> {
        val user = userService.loginUser(credentials)

        return ResponseEntity.ok(user)
    }

    /**
     * GET - 주어진 ID로 가진 사용자 정보를 검색합니다.
     * */
    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId: Long
    ): ResponseEntity<User> {
        val user = userService.findById(userId)

        return ResponseEntity.ok(user)
    }
}