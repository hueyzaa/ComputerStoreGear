package model;

import model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmailVerificationToken {
    private UUID tokenID;
    private UUID userID;
    private UUID token;
    private String tokenType;
    private LocalDateTime expiryDate;
    private boolean isUsed;
    private LocalDateTime createdDate;
    private LocalDateTime usedDate;
    private User user;

    public EmailVerificationToken() {
    }

    public EmailVerificationToken(UUID tokenID, UUID userID, UUID token, String tokenType, LocalDateTime expiryDate,
                                  boolean isUsed, LocalDateTime createdDate, LocalDateTime usedDate) {
        this.tokenID = tokenID;
        this.userID = userID;
        this.token = token;
        this.tokenType = tokenType;
        this.expiryDate = expiryDate;
        this.isUsed = isUsed;
        this.createdDate = createdDate;
        this.usedDate = usedDate;
    }

    public UUID getTokenID() {
        return tokenID;
    }

    public void setTokenID(UUID tokenID) {
        this.tokenID = tokenID;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(LocalDateTime usedDate) {
        this.usedDate = usedDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}