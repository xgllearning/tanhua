package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Settings;

public interface SettingsApi {
    /**
     * 通过settingsApi查询通知设置，根据userId
     * @param userId
     * @return
     */
    Settings findByUserId(Long userId);
}
