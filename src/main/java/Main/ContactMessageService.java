package Main;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    public ContactMessage saveMessage(ContactMessage message) {
        return contactMessageRepository.save(message);
    }

    public List<ContactMessage> findAllMessages() {
        return contactMessageRepository.findAll();
    }
    public ContactMessage findById(Long id) {
        return contactMessageRepository.findById(id).orElse(null);
    }
    public void deleteMessageById(Long id) {
        contactMessageRepository.deleteById(id);
    }
}