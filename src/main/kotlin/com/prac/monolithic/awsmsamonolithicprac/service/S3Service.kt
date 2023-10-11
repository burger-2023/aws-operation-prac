package com.prac.monolithic.awsmsamonolithicprac.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudfront.CloudFrontClient
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest
import software.amazon.awssdk.services.cloudfront.model.InvalidationBatch
import software.amazon.awssdk.services.cloudfront.model.Paths
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.regex.Pattern


@Service
class S3Service(
    @Value("\${cloud.aws.region.static}") private val region: String,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String,
    @Value("\${cloud.aws.cloud-front.domain-name}") private val cloudFrontDomainName: String,
    @Value("\${cloud.aws.cloud-front.distribution-id}") private val cloudFrontDistributionId: String,
) {


    private val awsCredentials = DefaultCredentialsProvider.create()

    private val s3: S3Client = S3Client.builder()
        .credentialsProvider(awsCredentials)
        .region(Region.of(region)) // Set your region
        .build()

    private val cloudFront: CloudFrontClient = CloudFrontClient.builder()
        .credentialsProvider(awsCredentials)
        .region(Region.of(region))
        .build()

    fun uploadFile(key: String, bytes: ByteArray): String {
        createInvalidation(key)
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

    fun createInvalidation(key: String) {
        val encodedPaths = encodeNonAsciiAndSpecialChars("/$key")
        val invalidationPaths = Paths.builder()
            .quantity(1)
            .items(encodedPaths)
            .build()
        val invalidationBatch = InvalidationBatch.builder()
            .paths(invalidationPaths)
            .callerReference(LocalDateTime.now().toString())
            .build()
        val invalidationRequest = CreateInvalidationRequest.builder()
            .distributionId(cloudFrontDistributionId)
            .invalidationBatch(invalidationBatch)
            .build()
        cloudFront.createInvalidation(invalidationRequest)
    }

    private fun encodeNonAsciiAndSpecialChars(input: String): String {
        val pattern = Pattern.compile("[^\\x00-\\x7F]|[%#]")
        val matcher = pattern.matcher(input)
        val stringBuffer = StringBuffer()

        while (matcher.find()) {
            val replacement = URLEncoder.encode(matcher.group(), StandardCharsets.UTF_8.toString())
            matcher.appendReplacement(stringBuffer, replacement)
        }

        matcher.appendTail(stringBuffer)
        return stringBuffer.toString()
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

    fun convertToCloudFrontUrl(key: String?): String? {
        return if (key == null) null else "${cloudFrontDomainName}/$key"
    }

    private fun extractKeyFromS3Url(s3Url: String): String? {
        val regex = "https://s3.*\\.amazonaws\\.com/${bucket}/(.*?)$".toRegex()
        val matchResult = regex.find(s3Url)
        return matchResult?.groups?.get(1)?.value
    }
}