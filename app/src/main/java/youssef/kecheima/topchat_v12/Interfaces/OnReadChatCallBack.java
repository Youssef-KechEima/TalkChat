package youssef.kecheima.topchat_v12.Interfaces;

import java.util.List;

import youssef.kecheima.topchat_v12.Model.Chat;

public interface OnReadChatCallBack {
    void onReadSuccess(List<Chat> chatList);
    void onReadFailed();
}
