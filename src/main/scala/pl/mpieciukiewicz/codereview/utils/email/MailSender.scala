package pl.mpieciukiewicz.codereview.utils.email

import scala.concurrent.Future
import java.util.Properties
import javax.mail._
import pl.mpieciukiewicz.codereview.utils.TemplateUtil
import javax.mail.internet.{MimeMessage, InternetAddress}
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * @author Marcin Pieciukiewicz
 */

abstract class MailSender {
  def sendMail(subject: String, template: String, address: String, params: Map[String, String])
}

class SslMailSender(smtpUser: String,smtpPassword:String,smtpHost:String, smtpPort: Int, emailFrom: String) extends MailSender {

  def sendMail(subject: String, template: String, address: String, params: Map[String, String]):Unit = Future {
      val props = new Properties

     // props.put("mail.smtp.user", smtpUser)

      props.put("mail.smtp.host", smtpHost)
      props.put("mail.smtp.port", smtpPort.toString)

      props.put("mail.smtp.starttls.enable", "true")
      //    props.put("mail.smtp.debug", "true");

      props.put("mail.smtp.auth", "true")

      props.put("mail.smtp.socketFactory.port", smtpPort.toString)
      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
      props.put("mail.smtp.socketFactory.fallback", "false")

      val session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
        override def getPasswordAuthentication: PasswordAuthentication = {
          new PasswordAuthentication(smtpUser, smtpPassword)
        }
      })

      //    session.setDebug(true)

      try {

        val message: Message = new MimeMessage(session)
        message.setFrom(new InternetAddress(emailFrom))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address).asInstanceOf[Array[Address]])
        message.setSubject(subject)
        message.setContent(TemplateUtil.fillTemplate(template, params), "text/html; charset=UTF-8")

        val transport: Transport = session.getTransport("smtps")
        transport.connect(smtpHost, smtpPort, smtpUser, smtpPassword)
        transport.sendMessage(message, message.getAllRecipients)
        transport.close()

      } catch {
        case ex: MessagingException => ex.printStackTrace()
      }
    }



}

