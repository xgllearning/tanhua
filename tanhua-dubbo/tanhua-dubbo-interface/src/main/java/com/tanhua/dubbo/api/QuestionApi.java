package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Question;

public interface QuestionApi {
    /**
     * 通过questionApi查询陌生人问题，根据userId
     * @param userId
     * @return
     */
    Question findByUserId(Long userId);
}
