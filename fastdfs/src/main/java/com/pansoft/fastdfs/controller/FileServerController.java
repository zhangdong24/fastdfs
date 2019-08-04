package com.pansoft.fastdfs.controller;

import com.pansoft.fastdfs.model.FileSystem;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/filesystem")
public class FileServerController {

    @Value("${pansoft-fastdfs.upload_location}")
    private String upload_location;

    @PostMapping("/upload")
    public FileSystem uploadFile(@RequestParam("file")  MultipartFile file) {

        //从application.yml上取临时目录值



        //首先将文件存在web服务器上（本机）然后再去调用fastDFS的client将本机的文件上传到fastDFS服务器上
        FileSystem fileSystem = new FileSystem();
        String originalFilename = file.getOriginalFilename();
        //取文件的扩展名
        String extention = originalFilename.substring(originalFilename.lastIndexOf("."));
        //定义一个file,使用file存储上传的文件
        String fileNameNew = UUID.randomUUID()+extention;
        File file1 = new File(upload_location+fileNameNew);

        try {
            file.transferTo(file1);
            //获取新上传文件的物理路径
            String newFilePath = file1.getAbsolutePath();
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);

            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);

            //文件元信息
            NameValuePair[] metaList = new NameValuePair[1];
            metaList[0] = new NameValuePair("fileName", originalFilename);
            //执行上传,将上传成功的存放在web服务器上（本机）上的文件上传的fastDFS
            String fileId = client.upload_file1(newFilePath, null, metaList);
            System.out.println("upload success. file id is: " + fileId);

            fileSystem.setFileId(fileId);
            fileSystem.setFileName(originalFilename);
            fileSystem.setFilePath(fileId);
            //通过调用service和dao将文件信息可以放在数据库中


//            int i = 0;
//            while (i++ < 10) {
//                byte[] result = client.download_file1(fileId);
//                System.out.println(i + ", download result is: " + result.length);
//            }

            trackerServer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fileSystem;
    }
}
