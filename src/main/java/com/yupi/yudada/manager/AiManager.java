package com.yupi.yudada.manager;

import com.yupi.yudada.common.ErrorCode;
import com.yupi.yudada.exception.BusinessException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *  调用AI
 */
@Component
public class AiManager {

    @Resource
    private ClientV4 clientV4;

    // 稳定随机数
    private static final float STABLE_TEMPERATURE = 0.05f;

    // 不稳定随机数
    private static final float UNSTABLE_TEMPERATURE = 0.99f;

    /**
     * 不稳定-同步请求
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncUnstableAiRequest(String systemMessage, String userMessage) {
        return doAiRequest(systemMessage, userMessage, Boolean.FALSE, UNSTABLE_TEMPERATURE);
    }
    /**
     * 稳定-同步请求
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncStableRequest(String systemMessage, String userMessage) {
        return doAiRequest(systemMessage, userMessage, Boolean.FALSE, STABLE_TEMPERATURE);
    }

    /**
     * 同步请求
     */
    public String doSyncAiRequest(String systemMessage, String userMessage, Float temperature) {
        return doAiRequest(systemMessage, userMessage, Boolean.FALSE, temperature);
    }

    /**
     * 简化晓兮
     * @param systemMessage
     * @param userMessage
     * @param stream
     * @param temperature
     * @return
     */
    public String doAiRequest (String systemMessage, String userMessage, Boolean stream, Float temperature) {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        chatMessageList.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessageList.add(userChatMessage);
        return doAiRequest(chatMessageList, stream, temperature);
    }

    /**
     * 通用AI请求
     * @param messages
     * @param stream
     * @param temperature
     * @return
     */
    public String doAiRequest(List<ChatMessage> messages, Boolean stream, Float temperature) {
        // 构建AI请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getData().getChoices().get(0).toString();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }

    }

}