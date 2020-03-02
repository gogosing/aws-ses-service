package me.gogosing.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesAsyncClient

/**
 * Created by JinBum Jeong on 2020/03/02.
 */
@Configuration
class AwsSesConfiguration(
    @Value("\${aws.access-key-id:}") val accessKeyId: String,
    @Value("\${aws.secret-access-key:}") val secretAccessKey: String
) {

    /**
     * AWS SES Client 설정.
     * @see <a href="https://docs.aws.amazon.com/ko_kr/ses/latest/DeveloperGuide/regions.html#region-mail-from"
     *      target="_top">AWS SES Supported Regions</a>
     */
    @Bean
    fun sesAsyncClient(): SesAsyncClient {
        return SesAsyncClient.builder()
            .credentialsProvider { AwsBasicCredentials.create(accessKeyId, secretAccessKey) }
            .region(Region.US_WEST_2)
            .build()
    }
}