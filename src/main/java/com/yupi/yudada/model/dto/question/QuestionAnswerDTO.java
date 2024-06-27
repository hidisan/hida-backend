package com.yupi.yudada.model.dto.question;

import lombok.Data;

/**
 * 封装题目答案
 */
@Data
public class QuestionAnswerDTO {
    /**
     * 题目
     */
    private String title;

    /**
     * 题目答案
     */
    private String userAnswer;
}
