package com.prac.monolithic.awsmsamonolithicprac.service

import com.prac.monolithic.awsmsamonolithicprac.dto.Credentials
import com.prac.monolithic.awsmsamonolithicprac.entity.User
import com.prac.monolithic.awsmsamonolithicprac.error.InvalidCredentialsException
import com.prac.monolithic.awsmsamonolithicprac.error.UserAlreadyExistsException
import com.prac.monolithic.awsmsamonolithicprac.error.UserNotFoundException
import com.prac.monolithic.awsmsamonolithicprac.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile


@Service
class UserService(
    private val userRepository: UserRepository,
    private val s3Service: S3Service
) {

    @Transactional
    fun registerUser(user: User): User {
        val isExist = userRepository.findByEmail(user.email)
        if (isExist != null) throw UserAlreadyExistsException("User already exist with email: ${user.email}")

        return userRepository.save(user)
    }

    @Transactional
    fun registerUserImage(image: MultipartFile, userId: Long): String {
        // 이미지 키값 생성
        val key = "user/$userId/${image.originalFilename?.replace(" ", "_")}"

        // S3에 이미지 업로드
        val imageUrl = s3Service.uploadFile(key, image.bytes)

        // imageUrl 저장
        userRepository.updateUserImageUrl(userId, imageUrl)

        return imageUrl
    }


    @Transactional(readOnly = true)
    fun findByEmail(credentials: Credentials): User {
        val user = userRepository.findByEmail(credentials.email)
            ?: throw UserNotFoundException("User not found with email: ${credentials.email}")
        if (user.password != credentials.password) throw InvalidCredentialsException("Invalid credentials")

        return user
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): User {
        return userRepository.findById(id).orElseThrow { UserNotFoundException("User not found with id: $id") }
    }

    @Transactional
    fun deleteUserImage(userId: Long) {
        val user = findById(userId)

        s3Service.deleteFile(user.imageUrl ?: return)
        // imageUrl to null
        userRepository.updateUserImageUrlToNull(userId)
    }

}