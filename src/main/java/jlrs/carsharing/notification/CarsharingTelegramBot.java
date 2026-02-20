package jlrs.carsharing.notification;

import java.time.LocalDate;
import java.util.List;
import jlrs.carsharing.dto.rental.RentalCreatedEvent;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.service.RentalService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
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
    private static final String ACTIVE_RENTALS = "active_rentals";
    private static final String NOT_ACTIVE_RENTALS = "notActive_rentals";

    private final TelegramClient telegramClient;
    private final NotificationService notificationService;
    private final RentalService rentalService;
    private final Long adminChatId;
    private final String botToken;

    public CarsharingTelegramBot(
            NotificationService notificationService,
            RentalService rentalService,
            @Value("${telegram.admin.chatId}") Long adminChatId,
            @Value("${telegram.token}") String botToken
    ) {
        this.notificationService = notificationService;
        this.rentalService = rentalService;
        this.adminChatId = adminChatId;
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

    public void sendMessage(Long chatId, String messageText) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .text(messageText)
                .chatId(chatId)
                .parseMode("Markdown")
                .build();
        telegramClient.execute(message);
    }

    /*
    this method will send notification every day at 10:00 AM about overdue rentals.
    if there will not be overdue rentals, will be sent simple text message, else list of rentals.
    */
    @Scheduled(cron = "0 0 10 * * *")
    public void checkAndNotifyOverdueRentals() throws TelegramApiException {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<RentalResponse> overdueRentals = rentalService.getOverdueRentalsByDate(tomorrow);

        if (overdueRentals.isEmpty()) {
            sendMessage(adminChatId, "🔔 No rentals overdue today!");
            return;
        }

        for (RentalResponse rentalResponse : overdueRentals) {
            String message = notificationService.formatOverdueMessage(rentalResponse);
            sendMessage(adminChatId, message);
        }

    }

    /*
    when rental will be created, instantly sends notification about this to admin
    */
    @EventListener
    private void handleRentalCreatedEvent(RentalCreatedEvent event) throws TelegramApiException {
        String message = "🚀 *New Rental Created!*\n\n"
                + notificationService.formatSingleRental(event.rental());
        sendMessage(adminChatId, message);
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

    /*
    welcome message. also hosts menu that appear when user types /start
     */
    private void sendMainMenu(Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .text(
                        " Welcome to Carsharing Application Bot."
                                + " Here you can get info about rentals."
                                + " (rentals that is still active or not)."
                                + " Also you will receive notification about"
                                + " every new rental that was created."
                )
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

    /*
    main method that provides sending messages about current rentals situation
     */
    private void handleCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        var data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        switch (data) {
            case ACTIVE_RENTALS -> sendMessage(
                    chatId,
                    notificationService.formatRentalsList(true)
            );
            case NOT_ACTIVE_RENTALS -> sendMessage(
                    chatId,
                    notificationService.formatRentalsList(false)
            );
            default -> sendMessage(chatId, "Unknown command!");
        }
    }

}
