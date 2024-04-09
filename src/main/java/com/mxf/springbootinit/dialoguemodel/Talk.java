package com.mxf.springbootinit.dialoguemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Talk {

    private Long conversationId;

    private String conversationName;

    private List<Message> msgList;

}
