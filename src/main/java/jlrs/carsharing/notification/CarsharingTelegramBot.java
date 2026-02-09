package jlrs.carsharing.notification;

import java.util.List;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class CarsharingTelegramBot implements SpringLongPollingBot, LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final String botToken;

    public CarsharingTelegramBot(
            @Value("${telegram.token}") String botToken
    ) {
        this.botToken = botToken;
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(List<Update> list) {
        list.forEach(this::handleUpdate);
    }

    @SneakyThrows
    private void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendMainMenu(chatId);
            } else {
                SendMessage message = SendMessage.builder()
                        .text("If you want to start work with this bot you better enter /start command")
                        .chatId(chatId)
                        .build();

                telegramClient.execute(message);
            }

        }
    }

    private void sendMainMenu(Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .text(" Welcome to Carsharing Application Bot."
                        + " Here you can get info about rentals."
                        + " (rentals that is still active or not)."
                        + " Also you will receive notification about every new rental that was created.")
                .chatId(chatId)
                .build();

        var receiveActiveRentalsButton = InlineKeyboardButton.builder()
                .text("Receive rentals that is still active")
                .callbackData("active_rentals")
                .build();

        var receiveNotActiveRentalsButton = InlineKeyboardButton.builder()
                .text("Receive rentals that is not active")
                .callbackData("notActive_rentals")
                .build();

        List<InlineKeyboardRow> keyboardRows = List.of(
                new InlineKeyboardRow(receiveActiveRentalsButton),
                new InlineKeyboardRow(receiveNotActiveRentalsButton)
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);

        message.setReplyMarkup(markup);

        telegramClient.execute(message);

    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        switch (data) {

        }
    }

}
