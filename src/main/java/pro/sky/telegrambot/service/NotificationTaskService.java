package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationTaskService {

    private final NotificationTaskRepo notificationTaskRepo;

    @Autowired
    public NotificationTaskService(NotificationTaskRepo notificationTaskRepo) {
        this.notificationTaskRepo = notificationTaskRepo;
    }

    public List<NotificationTask> findByDate(LocalDateTime localDateTime) {
        return notificationTaskRepo.findAllByLocalDateTime(localDateTime);
    }

    public NotificationTask saveTask(NotificationTask notificationTask) {
        return notificationTaskRepo.save(notificationTask);
    }

    public void deleteTask(NotificationTask notificationTask) {
        notificationTaskRepo.delete(notificationTask);
    }

}
