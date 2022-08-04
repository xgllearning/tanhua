package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Settings;

public interface SettingsApi {
    /**
     * 通过settingsApi查询通知设置，根据userId
     * @param userId
     * @return
     */
    Settings findByUserId(Long userId);

    /**
     * 通知设置--保存操作
     * @param settings
     */
    void save(Settings settings);
    /**
     * 通知设置--更新操作
     * @param settings
     */
    void update(Settings settings);
}
