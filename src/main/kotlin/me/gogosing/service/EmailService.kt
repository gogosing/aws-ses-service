package me.gogosing.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ses.SesAsyncClient
import software.amazon.awssdk.services.ses.model.Body
import software.amazon.awssdk.services.ses.model.Content
import software.amazon.awssdk.services.ses.model.Message
import software.amazon.awssdk.services.ses.model.Destination
import software.amazon.awssdk.services.ses.model.SendEmailRequest

/**
 * Created by JinBum Jeong on 2020/03/02.
 */
interface EmailService {

    /**
     * email message 전송.
     * @param receiver 수신자 정보.
     * @param title 전송 message title.
     * @param content 전송할 message.
     */
    fun sendMessage(receiver: String, title: String, content: String)
}

@Service
class AwsSesServiceImpl(
    @Value("\${email.from:}")
    private val from: String,
    private val sesAsyncClient: SesAsyncClient
) : EmailService {

    private val logger = LoggerFactory.getLogger(AwsSesServiceImpl::class.java)

    /**
     * @see <a href="https://docs.aws.amazon.com/ko_kr/ses/latest/DeveloperGuide/examples-send-using-sdk.html"
     *      target="_top">examples-send-using-sdk.html</a>
     */
    override fun sendMessage(receiver: String, title: String, content: String) {
        val destination = Destination.builder().toAddresses(receiver)
        try {
            sesAsyncClient.sendEmail(
                SendEmailRequest.builder()
                    .destination(destination.build())
                    .message(buildMessage(title, content))
                    .source(from)
                    .build()
            )
        } catch (exception: UnsupportedOperationException) {
            logger.error("EMAIL 메시지 전송 중 오류가 발생하였습니다.", exception)
        }
    }

    /**
     * 이메일 메세지 정보 생성.
     * 최대 메시지 크기(첨부파일 포함) : 메시지당 10MB(base64 인코딩 후)
     * @see <a href="https://docs.aws.amazon.com/ko_kr/ses/latest/DeveloperGuide/limits.html" target="_top">limits</a>
     * @param title 전송 이메일 제목.
     * @param htmlContent 전송 이메일 본문.
     * @return 생성된 이메일 메시지.
     */
    private fun buildMessage(title: String, htmlContent: String): Message {
        val subject = Content.builder()
            .charset(Charsets.UTF_8.toString()).data(title).build()
        val content = Content.builder()
            .charset(Charsets.UTF_8.toString()).data(htmlContent).build()
        return Message.builder()
            .subject(subject)
            .body(Body.builder().html(content).build())
            .build()
    }
}
