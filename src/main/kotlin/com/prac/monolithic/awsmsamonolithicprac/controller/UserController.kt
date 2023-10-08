package com.prac.monolithic.awsmsamonolithicprac.controller

import com.prac.monolithic.awsmsamonolithicprac.dto.Credentials
import com.prac.monolithic.awsmsamonolithicprac.entity.User
import com.prac.monolithic.awsmsamonolithicprac.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    /**
     * 새로운 사용자를 등록합니다. Request body에는 email, name, password가 포함되어야 합니다.
     * */
    @PostMapping("/register")
    fun registerUser(
        @RequestBody user: User
    ): ResponseEntity<User> {
        val registeredUser = userService.registerUser(user)

        return ResponseEntity.ok(registeredUser)
    }

    @PostMapping("/{userId}/image/register")
    fun registerUserImage(
        @RequestParam image: MultipartFile,
        @PathVariable userId: Long
    ): ResponseEntity<String> {
        val imageUrl = userService.registerUserImage(image, userId)

        return ResponseEntity.ok(imageUrl)
    }

    /**
     * 사용자 로그인을 수행합니다. Request body에는 email과 password가 포함되어야 합니다.
     * */
    @PostMapping("/login")
    fun loginUser(
        @RequestBody credentials: Credentials
    ): ResponseEntity<User> {
        val user = userService.findByEmail(credentials)

        return ResponseEntity.ok(user)
    }

    /**
     * 주어진 ID를 가진 사용자 정보를 검색합니다.
     * */
    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId: Long
    ): ResponseEntity<User> {
        val user = userService.findById(userId)

        return ResponseEntity.ok(user)
    }

    @DeleteMapping("/{userId}/image/delete")
    fun deleteUserImage(
        @PathVariable userId: Long
    ) {
        userService.deleteUserImage(userId)
    }
}