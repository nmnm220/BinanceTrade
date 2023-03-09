package bin.trade.telegrambot;

import api.keys.TelegramApiKey;
import bin.trade.TradingBot;
import bin.trade.market.BinanceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TradeDataTelegramBot extends TelegramLongPollingBot {
    private final String API_KEY = TelegramApiKey.getAPI_KEY();
    private long chatId;
    private Logger logger = LoggerFactory.getLogger(TradeDataTelegramBot.class);
    @Override
    public void onUpdateReceived(Update update) {
        if (hasTextAndMessageIs(update, "/start")) {
            chatId = update.getMessage().getChatId();
            logger.info("Chat Id: " + chatId);
            sendTradeInfo("Trade bot started");
            TradingBot.tradeBotStart();
        }
        if (hasTextAndMessageIs(update,"/stop")) {
            logger.info("Chat Id: " + chatId);
            sendTradeInfo("Trade bot stopped");
            TradingBot.tradeBotStop();
        }
    }

    private boolean hasTextAndMessageIs(Update update, String text) {
        return update.getMessage().hasText() && update.getMessage().getText().equals(text);
    }

    @Override
    public String getBotUsername() {
        return "mytradingbotsinfo_bot";
    }
    @Override
    public String getBotToken() {
        return API_KEY;
    }

    public void sendTradeInfo(String textToSend) {
        SendMessage message = new SendMessage();
        message.setText(textToSend);
        message.setChatId(chatId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
