package com.zyoutube.feature.video.dao.redis.Impl;

import com.zyoutube.feature.video.dao.redis.ViewDirtyDao;

import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ViewDirtySetCache implements ViewDirtyDao {
    private static final String SET = "video:view:dirty:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void markDirty(Long videoId) {
        stringRedisTemplate.opsForSet().add(SET, videoId.toString());
    }

    @Override
    public Set<Long> getDirtyVideoIds() {
        Set<String> members = stringRedisTemplate.opsForSet().members(SET);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Long> result = new HashSet<>(members.size());
        for (String member : members) {
            try {
                result.add(Long.parseLong(member));
            } catch (NumberFormatException ignored) {
                // ignore malformed member
            }
        }
        return result;
    }

    @Override
    public boolean isDirty(Long videoId) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(SET, videoId.toString()));
    }

    @Override
    public void clearDirty(Long videoId) {
        stringRedisTemplate.opsForSet().remove(SET, videoId.toString());
    }

    @Override
    public Set<Long> getDirtyVideoIds() {
        Set<String> members = stringRedisTemplate.opsForSet().members(SET);
        if (members == null) {
            return Collections.emptySet();
        }
        return members.stream()
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }
}

