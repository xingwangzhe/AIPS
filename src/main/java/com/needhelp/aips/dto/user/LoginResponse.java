package com.needhelp.aips.dto.user;

public class LoginResponse {
    private String token;
    private int expireIn;
    private UserInfo userInfo;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public int getExpireIn() { return expireIn; }
    public void setExpireIn(int expireIn) { this.expireIn = expireIn; }
    public UserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }

    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String phone;
        private String avatarUrl;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }
}
