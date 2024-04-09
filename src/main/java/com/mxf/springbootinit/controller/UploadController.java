package com.mxf.springbootinit.controller;


import com.mxf.springbootinit.common.BaseResponse;
import com.mxf.springbootinit.common.ResultUtils;
import com.mxf.springbootinit.utils.AliOSSUtils;
//import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
public class UploadController {

    @Autowired
    private AliOSSUtils aliOSSUtils;

//    @PostMapping("/upload")
//    public Result upload(String name, Integer age, MultipartFile image){
//        log.info("上传文件，参数:{},{},{}", name, age, image.getOriginalFilename());
//        return Result.success();

    @PostMapping("/src/knowledgeBase")
    public BaseResponse<String> uolpad(MultipartFile image) throws IOException {
        log.info("上传文件，参数:{}", image.getOriginalFilename());
        String url = aliOSSUtils.upload(image);
        log.info("上传成功，文件路径:{}", url);
        return ResultUtils.success("Document uploaded successfully.");
    }
}
