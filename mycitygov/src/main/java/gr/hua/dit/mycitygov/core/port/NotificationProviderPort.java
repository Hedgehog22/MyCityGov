package gr.hua.dit.mycitygov.core.port;

public interface NotificationProviderPort {
    boolean sendNotification(String recipient, String message);
}

