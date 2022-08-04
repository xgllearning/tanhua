package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Question;

public interface QuestionApi {
    /**
     * 通过questionApi查询陌生人问题，根据userId
     * @param userId
     * @return
     */
    Question findByUserId(Long userId);

    /**
     * 设置陌生人问题-保存问题
     * @param question
     */
    void save(Question question);

    /**
     * 设置陌生人问题-更新问题
     * @param question
     */
    void update(Question question);
}
