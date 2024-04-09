package com.mxf.springbootinit.dialoguemodel;


import cn.hutool.core.util.IdUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;


@Service
public class ConversationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Message addMessageToTalk(Long systemId, Long conversationId, Message newMessage) {
        try {
            // 检查对应的系统用户是否存在
            Query systemQuery = new Query(Criteria.where("userId").is(systemId));
            boolean systemExists = mongoTemplate.exists(systemQuery, System.class);

            if (!systemExists) {
                // 如果系统用户不存在，则创建一个新的用户和对话
                Talk newTalk = new Talk(conversationId, "New Conversation", Collections.singletonList(newMessage));
                Long id = IdUtil.getSnowflake().nextId();
                System newUserSystem = new System(id, systemId, Collections.singletonList(newTalk));
                mongoTemplate.save(newUserSystem);
                return newMessage;
            }

            // 构建查询条件，寻找匹配的系统用户和对话ID
            Query conversationQuery = new Query(Criteria.where("userId").is(systemId)
                    .and("talkList.conversationId").is(conversationId));
            boolean conversationExists = mongoTemplate.exists(conversationQuery, System.class);

            if (conversationExists) {
                // 对话已存在，向对应的msgList添加消息
                Update update = new Update().push("talkList.$.msgList", newMessage);
                mongoTemplate.updateFirst(conversationQuery, update, System.class);
            } else {
                // 对话不存在，创建新的对话并添加到用户的talkList中
                Talk newTalk = new Talk(conversationId, "New Conversation", Collections.singletonList(newMessage));
                Update update = new Update().push("talkList", newTalk);
                mongoTemplate.updateFirst(systemQuery, update, System.class);
            }
            return newMessage;
        } catch (DataAccessException e) {
            // 处理异常，记录或返回错误信息
            e.printStackTrace(); // 实际项目中应使用更合适的日志记录方式
            return new Message("error", "Failed to add message to talk: " + e.getMessage());
        }
    }


    public Message testInsert(Long systemId, Long conversationId, Message newMessage) {
        // 创建一个简单的文档对象
        System userSystem = new System();
        userSystem.setUserId(12345L);
        userSystem.setTalkList(Arrays.asList(new Talk(67890L, "Test Conversation", Arrays.asList(new Message("role", "content")))));

        // 插入文档到数据库
        mongoTemplate.save(userSystem, "system");

        // 进行验证，例如验证插入的文档是否存在
        assert mongoTemplate.findById(12345L, System.class, "system") != null;
        return new Message("role", "content");
    }


    public List<Talk> getUserConversations(Long userId) {
        System userSystem = mongoTemplate.findOne(
                Query.query(Criteria.where("userId").is(userId)), System.class);

        if (userSystem != null) {
            return userSystem.getTalkList();
        } else {
            return null;
        }
    }

    public boolean updateConversationName(Long userId, Long conversationId, String newName) {
        // 创建查询条件，匹配用户ID和会话ID
        Query query = new Query(Criteria.where("userId").is(userId)
                .and("talkList.conversationId").is(conversationId));

        // 创建更新对象，设置新的会话名称
        Update update = new Update().set("talkList.$.conversationName", newName);

        // 执行更新操作
        System updatedSystem = mongoTemplate.findAndModify(query, update,
                new FindAndModifyOptions().returnNew(true), System.class);

        // 检查更新是否成功，根据需求返回适当的值
        return updatedSystem != null;
    }

    public boolean deleteConversation(Long userId, Long conversationId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        Update update = new Update().pull("talkList", Query.query(Criteria.where("conversationId").is(conversationId)));
        return mongoTemplate.updateFirst(query, update, System.class).getModifiedCount() > 0;
    }

    public List<Message> getMessages(Long userId, Long conversationId) {
        Query query = new Query(Criteria.where("userId").is(userId)
                .and("talkList.conversationId").is(conversationId));
        System userSystem = mongoTemplate.findOne(query, System.class);

        if (userSystem != null) {
            return userSystem.getTalkList().stream()
                    .filter(talk -> conversationId.equals(talk.getConversationId()))
                    .findFirst()
                    .map(Talk::getMsgList)
                    .orElse(null);
        }
        return null;
    }
}