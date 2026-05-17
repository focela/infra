package com.focela.platform.system.repository.redis.oauth2;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.common.utils.json.JsonUtils;
import com.focela.platform.system.entity.oauth2.OAuth2AccessTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.focela.platform.system.constants.RedisKeyConstants.OAUTH2_ACCESS_TOKEN;

/**
 * RedisDAO for {@link OAuth2AccessTokenEntity}
 */
@Repository
@RequiredArgsConstructor
public class OAuth2AccessTokenRedisRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public OAuth2AccessTokenEntity get(String accessToken) {
        String redisKey = formatKey(accessToken);
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), OAuth2AccessTokenEntity.class);
    }

    public void set(OAuth2AccessTokenEntity accessTokenDO) {
        String redisKey = formatKey(accessTokenDO.getAccessToken());
        // clean redundant fields to avoid caching
        accessTokenDO.setUpdater(null).setUpdateTime(null).setCreateTime(null).setCreator(null).setDeleted(null);
        long time = LocalDateTimeUtil.between(LocalDateTime.now(), accessTokenDO.getExpiresTime(), ChronoUnit.SECONDS);
        if (time > 0) {
            stringRedisTemplate.opsForValue().set(redisKey, JsonUtils.toJsonString(accessTokenDO), time, TimeUnit.SECONDS);
        }
    }

    public void delete(String accessToken) {
        String redisKey = formatKey(accessToken);
        stringRedisTemplate.delete(redisKey);
    }

    public void deleteList(Collection<String> accessTokens) {
        List<String> redisKeys = CollectionUtils.convertList(accessTokens, OAuth2AccessTokenRedisRepository::formatKey);
        stringRedisTemplate.delete(redisKeys);
    }

    private static String formatKey(String accessToken) {
        return String.format(OAUTH2_ACCESS_TOKEN, accessToken);
    }

}
