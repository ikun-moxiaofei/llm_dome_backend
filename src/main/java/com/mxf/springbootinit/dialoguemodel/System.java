package com.mxf.springbootinit.dialoguemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "system") // 指定这个类对应于MongoDB中的哪个集合
public class System {
    @Id
    private Long ID;
    private Long userId;
    private List<Talk> talkList;
}
