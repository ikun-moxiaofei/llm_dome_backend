package com.mxf.springbootinit.controller;

import cn.hutool.core.util.IdUtil;
import com.mxf.springbootinit.common.BaseResponse;
import com.mxf.springbootinit.common.ErrorCode;
import com.mxf.springbootinit.common.ResultUtils;
import com.mxf.springbootinit.dialoguemodel.ConversationService;

import com.mxf.springbootinit.dialoguemodel.Talk;
import com.mxf.springbootinit.dialoguemodel.dto.dialogue.conversation.*;
import com.mxf.springbootinit.dialoguemodel.Message;
import com.mxf.springbootinit.exception.BusinessException;
import com.mxf.springbootinit.model.entity.User;
import com.mxf.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
public class DialogueController {

    @Resource
    private ConversationService conversationService;
    @Resource
    private UserService userService;

    /**
     * 保存对话
     *
     * @param conversationRequest
     * @return
     */
    @PostMapping("/dialogue/user")
    public BaseResponse<ConversationResponse> saveConversation(@RequestBody ConversationRequest conversationRequest, HttpServletRequest request) {
        if (conversationRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long conversationId = conversationRequest.getConversationId();
        Message tempMessage = conversationRequest.getTemp_message();
        Long userId = conversationRequest.getUserId();
        User byId = userService.getById(userId);
        if(byId == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (tempMessage == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 调用服务层保存对话
        Message mes = conversationService.addMessageToTalk(userId, conversationId , tempMessage);
        // 构建响应对象
        ConversationResponse conversationResponse = new ConversationResponse();
        conversationResponse.setConversationId(conversationId);
        conversationResponse.setTemp_message(mes); // 假设ConversationResponse中的字段名为tempMessage

        return ResultUtils.success(conversationResponse);
    }

    @PostMapping("/dialogue/new")
    public BaseResponse<NewResponse> newConversation(HttpServletRequest request) {
        final User loginUser = userService.getLoginUser(request);
        Long systemId = loginUser.getId();
        if(systemId != null){
            Long id = IdUtil.getSnowflake().nextId();
            NewResponse newResponse = new NewResponse();
            newResponse.setId(id);
            newResponse.setRole("assistant");
            newResponse.setContent("请问有什么可以帮助你的");
            return ResultUtils.success(newResponse);
        }
        throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
    }
    @PostMapping("/dialogue/new/name")
    public BaseResponse<NewResponse> newConversationByName(HttpServletRequest request, @RequestBody NewConversationRequest newConversationRequest) {
//        String newName = newConversationRequest.getNewName();
//        final User loginUser = userService.getLoginUser(request);
//        Long systemId = loginUser.getId();

        String userId = newConversationRequest.getUserId();
        Long l = Long.parseLong(userId);
        String newName = newConversationRequest.getNewName();
        Long id = IdUtil.getSnowflake().nextId();
        NewResponse newResponse = new NewResponse();
        newResponse.setId(id);
        newResponse.setRole("assistant");
        newResponse.setContent("请问有什么可以帮助你的");
        Message tempMessage = new Message();
        tempMessage.setRole("assistant");
        tempMessage.setContent("请问有什么可以帮助你的");
        conversationService.addMessageToTalk(l, id , tempMessage);
        conversationService.updateConversationName(l, id , newName);
        return ResultUtils.success(newResponse);
    }

    @PostMapping("/dialogue/get/list")
    public ResponseEntity<List<Talk>> getUserConversations(@RequestBody GetListConversationRequest getListConversationRequest) {
        try {
            String userId = getListConversationRequest.getUserId();
            Long l = Long.parseLong(userId);
            List<Talk> conversations = conversationService.getUserConversations(l);
            if (conversations != null) {
                return new ResponseEntity<>(conversations, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dialogue/get/my/list")
    public ResponseEntity<List<Talk>> getMyConversations(HttpServletRequest request) {
        try {
            final User loginUser = userService.getLoginUser(request);
            Long systemId = loginUser.getId();
            List<Talk> conversations = conversationService.getUserConversations(systemId);
            if (conversations != null) {
                return new ResponseEntity<>(conversations, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/dialogue/updateName")
    public BaseResponse<String> updateConversationName(@RequestBody NameConversationRequest nameConversationRequest) {
        String userId = nameConversationRequest.getUserId();
        String conversationId = nameConversationRequest.getConversationId();
        String newName = nameConversationRequest.getNewName();

        Long userId_l = Long.parseLong(userId);
        Long conversationId_l = Long.parseLong(conversationId);
        boolean isUpdated = conversationService.updateConversationName(userId_l, conversationId_l, newName);

        if(isUpdated) {
            return ResultUtils.success("Conversation name updated successfully.");
        } else {
            return ResultUtils.success("Failed to update conversation name.");
        }
    }

    @PostMapping("/dialogue/delete")
    public BaseResponse<String> deleteConversation(HttpServletRequest request,@RequestBody DeleteConversationRequest deleteConversationRequest) {
//        final User loginUser = userService.getLoginUser(request);
//        Long systemId = loginUser.getId();
        String userId = deleteConversationRequest.getUserId();
        Long userId_l = Long.parseLong(userId);
        String conversationId = deleteConversationRequest.getConversationId();
        Long conversationId_l = Long.parseLong(conversationId);
        boolean isDelete = conversationService.deleteConversation(userId_l, conversationId_l);
        if(isDelete) {
            return ResultUtils.success("OK");
        } else {
            return ResultUtils.success("error");
        }

    }
    @GetMapping("/get/dialogue")
    public ResponseEntity<List<Message>> getMessages(@RequestParam("userId") Long userId, @RequestParam("conversationId") Long conversationId) {
        List<Message> messages = conversationService.getMessages(userId, conversationId);
        if (messages != null) {
            return ResponseEntity.ok(messages);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
