package Main;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final EmailService emailService;

    public ContactMessageService(ContactMessageRepository contactMessageRepository,
                                 EmailService emailService) {
        this.contactMessageRepository = contactMessageRepository;
        this.emailService = emailService;
    }

    public void saveMessage(ContactMessage message) {
        contactMessageRepository.save(message);
    }


    public ContactMessage getMessageById(Long id) {
        return contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    public ContactMessage findById(Long id) {
        return contactMessageRepository.findById(id).orElse(null);
    }

    public void deleteMessageById(Long id) {
        contactMessageRepository.deleteById(id);
    }

    public List<ContactMessage> findAllMessages() {
        return contactMessageRepository.findAll();
    }

    public void replyToMessage(Long id, String replyText) {
        ContactMessage message = getMessageById(id);

        message.setAdminReply(replyText);
        message.setAnswered(true);
        message.setAnsweredAt(LocalDateTime.now());

        contactMessageRepository.save(message);

        String subject = "Reply to your message: " + message.getSubject();

        String body = """
                Hello %s,

                Thank you for contacting Kompetens 5.0.

                Your message:
                %s

                Reply from administrator:
                %s

                Best regards,
                Kompetens 5.0
                """.formatted(
                message.getName(),
                message.getMessage(),
                replyText
        );

        emailService.sendEmail(message.getEmail(), subject, body);
    }


}