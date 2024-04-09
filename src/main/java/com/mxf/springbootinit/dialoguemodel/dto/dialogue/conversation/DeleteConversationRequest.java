package com.mxf.springbootinit.dialoguemodel.dto.dialogue.conversation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteConversationRequest {

    private String userId;

    private String conversationId;


}
