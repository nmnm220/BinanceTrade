package bin.trade.telegrambot;

import bin.trade.datahandler.TelegramDataHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TradeDataTelegramBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return null;
    }
    public void sendTradeInfo(long chatId) {

    }
}
