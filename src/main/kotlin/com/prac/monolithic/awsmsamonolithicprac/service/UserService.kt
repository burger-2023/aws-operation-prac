package com.prac.monolithic.awsmsamonolithicprac.service

import com.prac.monolithic.awsmsamonolithicprac.config.DataSourceUtils
import com.prac.monolithic.awsmsamonolithicprac.dto.Credentials
import com.prac.monolithic.awsmsamonolithicprac.entity.User
import com.prac.monolithic.awsmsamonolithicprac.error.InvalidCredentialsException
import com.prac.monolithic.awsmsamonolithicprac.error.UserAlreadyExistsException
import com.prac.monolithic.awsmsamonolithicprac.error.UserNotFoundException
import com.prac.monolithic.awsmsamonolithicprac.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val dataSourceUtils: DataSourceUtils
) {


    @Transactional
    fun registerUser(user: User): User {
        dataSourceUtils.getDatabaseUrl()
        val isExist = userRepository.findByEmail(user.email)
        if (isExist != null) throw UserAlreadyExistsException("User already exist with email: ${user.email}")
        return userRepository.save(user)
    }

    fun loginUser(credentials: Credentials): User? {
        val user = userRepository.findByEmail(credentials.email) ?: throw UserNotFoundException("User not found with email: ${credentials.email}")
        if (user.password != credentials.password) throw InvalidCredentialsException("Invalid credentials")

        return user
    }

    fun findById(id: Long): User? {
        dataSourceUtils.getDatabaseUrl()

        return userRepository.findById(id).orElseThrow { UserNotFoundException("User not found with id: $id") }
    }

    fun testRdsProxyPerformance() {
        runBlocking {
            val ids = listOf(1, 2, 3, 4, 5L)
            repeat(100) {
                ids.forEach { id ->
                    userRepository.findById(id)
                }
            }
        }
    }
}