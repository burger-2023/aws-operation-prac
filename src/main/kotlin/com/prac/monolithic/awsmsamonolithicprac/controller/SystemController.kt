package com.prac.monolithic.awsmsamonolithicprac.controller

import com.sun.management.OperatingSystemMXBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.management.ManagementFactory
import java.net.InetAddress
import java.time.LocalDateTime

@RestController
class SystemController(
    @Value("\${cloud.aws.region.static}") private val region: String,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String,
    @Value("\${cloud.aws.cloud-front.domain-name}") private val cloudFrontDomainName: String,
    @Value("\${cloud.aws.cloud-front.distribution-id}") private val cloudFrontDistributionId: String,
) {

    @GetMapping("/health_check")
    @Transactional(readOnly = true)
    fun healthCheck() = ResponseEntity.ok(
        mapOf(
            "status" to "OK",
            "message" to "System is healthy",
            "ipAddress" to InetAddress.getLocalHost().hostAddress,
            "timestamp" to LocalDateTime.now(),
            "cpuUsage" to getCurrentCpuUsage(),
            "branch" to "5_2_monolithic_s3_with_cloudfront",
            "aws_region" to region,
            "s3_bucket" to bucket,
            "cloud_front_domain_name" to cloudFrontDomainName,
            "cloud_front_distribution_id" to cloudFrontDistributionId
        )
    )


    fun getCurrentCpuUsage(): Double {
        val osBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
        return osBean.cpuLoad * 100
    }
}