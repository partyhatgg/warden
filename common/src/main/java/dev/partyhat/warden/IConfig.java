package dev.partyhat.warden;

public interface IConfig {
    Object getPrimitive(String path);

    <T> T getObject(String path, Class<T> type);
}
