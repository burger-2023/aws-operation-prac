package com.prac.monolithic.awsmsamonolithicprac.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest


@Service
class S3Service(
    @Value("\${cloud.aws.region.static}") private val region: String,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) {


    private val awsCredentials = DefaultCredentialsProvider.create()

    private val s3: S3Client = S3Client.builder()
        .credentialsProvider(awsCredentials)
        .region(Region.of(region)) // Set your region
        .build()

    fun uploadFile(key: String, bytes: ByteArray): String {
        s3.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/jpeg")
                .build(),
            RequestBody.fromBytes(bytes)
        )
        return "https://s3.${region}.amazonaws.com/${bucket}/${key}"
    }

    fun deleteFile(url: String) {
        val key = extractKeyFromS3Url(url)
        s3.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
        )
    }

    private fun extractKeyFromS3Url(s3Url: String): String? {
        val regex = "https://s3.*\\.amazonaws\\.com/${bucket}/(.*?)$".toRegex()
        val matchResult = regex.find(s3Url)
        return matchResult?.groups?.get(1)?.value
    }
}