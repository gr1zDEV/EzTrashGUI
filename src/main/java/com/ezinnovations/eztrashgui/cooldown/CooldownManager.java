package com.ezinnovations.eztrashgui.cooldown;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public long getRemaining(UUID playerId, long cooldownSeconds) {
        long now = System.currentTimeMillis();
        long next = cooldowns.getOrDefault(playerId, 0L);
        long remainingMs = next - now;
        if (remainingMs <= 0) {
            return 0;
        }
        long seconds = remainingMs / 1000;
        return remainingMs % 1000 == 0 ? seconds : seconds + 1;
    }

    public void apply(UUID playerId, long cooldownSeconds) {
        cooldowns.put(playerId, System.currentTimeMillis() + (cooldownSeconds * 1000));
    }
}
