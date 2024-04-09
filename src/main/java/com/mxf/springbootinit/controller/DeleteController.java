package com.mxf.springbootinit.controller;

import com.mxf.springbootinit.common.BaseResponse;
import com.mxf.springbootinit.common.ResultUtils;
import com.mxf.springbootinit.utils.AliOSSUtils;
//import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class DeleteController {

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @DeleteMapping("/src/knowledgeBase")
    public BaseResponse<String> delete(@RequestParam(value="targetID",required=true) String id) throws IOException {
        log.info("删除文件，参数:{}", id);
        String url = aliOSSUtils.deleteFile(id);
        log.info("删除成功，文件:{}", id);
        return ResultUtils.success("Document or folder deleted successfully.");
    }
}
