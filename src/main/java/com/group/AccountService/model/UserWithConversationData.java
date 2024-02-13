package com.group.AccountService.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithConversationData extends User {
    private Message lastMessage;
    private Integer unreadCount;

    public UserWithConversationData(User user) {
        this.setId(user.getId());
        this.setProfile(user.getProfile());
        this.lastMessage = null;
        this.unreadCount = 0;
    }
}