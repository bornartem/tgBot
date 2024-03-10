package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final static Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    @Autowired
    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String message = update.message().text();
            long chatId = update.message().chat().id();
            Matcher matcher = PATTERN.matcher(message);
            if (message != null && message.equals("/start")) {
                SendMessage sendMessage = new SendMessage(chatId,
                        "Welcome! "
                                + update.message().chat().firstName()
                                + " Enter: 01.01.2024 00:00 to do something");
                telegramBot.execute(sendMessage);
            } else if (matcher.matches()) {
                String text = matcher.group(3);
                LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(1),
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

                NotificationTask notificationTask = new NotificationTask();
                notificationTask.setChatId(chatId);
                notificationTask.setText(text);
                notificationTask.setLocalDateTime(localDateTime);

                notificationTaskService.saveTask(notificationTask);
                SendMessage sendMessage = new SendMessage(chatId, " Напоминание успешно добавлено");
                telegramBot.execute(sendMessage);
            } else {
                SendMessage sendMessage = new SendMessage(chatId, " Не правильно" + "Чтобы все работало, нужно написать в формате "
                        + "\"01.01.2022 00:00 покормить тигру\"");
                telegramBot.execute(sendMessage);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}