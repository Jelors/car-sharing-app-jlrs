package jlrs.carsharing.notification;

import java.util.List;
import java.util.stream.Collectors;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.service.RentalService;
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
    private final RentalService rentalService;
    private final String botToken;

    private static final String ACTIVE_RENTALS = "active_rentals";
    private static final String NOT_ACTIVE_RENTALS = "notActive_rentals";

    public CarsharingTelegramBot(
            RentalService rentalService, @Value("${telegram.token}") String botToken
    ) {
        this.rentalService = rentalService;
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
                sendMessage(chatId, "I don't understand you!");
            }

        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
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
                .callbackData(ACTIVE_RENTALS)
                .build();

        var receiveNotActiveRentalsButton = InlineKeyboardButton.builder()
                .text("Receive rentals that is not active")
                .callbackData(NOT_ACTIVE_RENTALS)
                .build();

        List<InlineKeyboardRow> keyboardRows = List.of(
                new InlineKeyboardRow(receiveActiveRentalsButton),
                new InlineKeyboardRow(receiveNotActiveRentalsButton)
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);

        message.setReplyMarkup(markup);

        telegramClient.execute(message);

    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        var data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        switch (data) {
            case ACTIVE_RENTALS -> sendMessage(chatId, formatRentalsList(true));
            case NOT_ACTIVE_RENTALS -> sendMessage(chatId, formatRentalsList(false));
            default -> sendMessage(chatId, "Unknown command!");
        }
    }

    private void sendMessage(Long chatId, String messageText) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .text(messageText)
                .chatId(chatId)
                .parseMode("Markdown")
                .build();
        telegramClient.execute(message);
    }

    private String formatRentalsList(Boolean isActive) {
        List<RentalResponse> rentals = rentalService.getRentalsByUserIdAndIsActive(null, isActive);
        String type = (isActive != null && isActive) ? "Active" : "All/Not Active";

        if (rentals.isEmpty()) {
            return "📋 *List of (" + type + ") rentals is empty!*";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("📋 *List of rentals (").append(type).append(")*\n\n");

        String items = rentals.stream()
                .map(this::formatSingleRental)
                .collect(Collectors.joining("\n---\n"));

        stringBuilder.append(items);
        return stringBuilder.toString();
    }

    private String formatSingleRental(RentalResponse rentalResponse) {
        return String.format(
                "🆔 *Rental ID:* %d\n"
                        + "🚗 *Car ID:* %d\n"
                        + "\uD83D\uDC64 *User ID:* %d\n"
                        + "📅 *Actual return date:* %s",
                rentalResponse.getId(),
                rentalResponse.getCarId(),
                rentalResponse.getUserId(),
                rentalResponse.getRentalDate()
        );
    }
}
