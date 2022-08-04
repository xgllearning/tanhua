package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.dubbo.mappers.QuestionMapper;
import com.tanhua.model.domain.Question;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class QuestionApiImpl implements QuestionApi{

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question findByUserId(Long userId) {
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Question::getUserId,userId);
        Question question = questionMapper.selectOne(queryWrapper);
        return question;
    }
    /**
     * 设置陌生人问题-保存问题
     * @param question
     */
    @Override
    public void save(Question question) {
        questionMapper.insert(question);
    }

    /**
     * 设置陌生人问题-更新问题
     * @param question
     */
    @Override
    public void update(Question question) {
        questionMapper.updateById(question);
    }
}
