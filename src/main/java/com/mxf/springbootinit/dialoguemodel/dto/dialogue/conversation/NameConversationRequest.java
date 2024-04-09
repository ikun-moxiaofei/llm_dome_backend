package com.mxf.springbootinit.dialoguemodel.dto.dialogue.conversation;

import com.mxf.springbootinit.dialoguemodel.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameConversationRequest {

    private String userId;

    private String conversationId;

    private String newName;

}
