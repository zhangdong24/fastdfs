package com.pansoft.fastdfs;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 在此文件下通过fastDFS的client代码访问tracker和storage
 * 中间走的是socket协议
 */
public class TestFastDFS {

    @Test
    public void TestUpload(){

        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);

            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);

            //文件元信息
            NameValuePair[] metaList = new NameValuePair[1];
            metaList[0] = new NameValuePair("fileName", "1.jpg");
            //执行上传
            String fileId = client.upload_file1("C:\\Users\\zhangdong\\Desktop\\1.jpg", "jpg", metaList);
            System.out.println("upload success. file id is: " + fileId);

            int i = 0;
            while (i++ < 10) {
                byte[] result = client.download_file1(fileId);
                System.out.println(i + ", download result is: " + result.length);
            }

            trackerServer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //查询文件
    @Test
    public void TestQuery(){
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);

            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);

            FileInfo group1 = client.query_file_info("group1", "M00/00/00/wKgohF0Uj_-AeB1kAAEKgZCiFiI325.jpg");
            FileInfo fileInfo = client.query_file_info1("group1/M00/00/00/wKgohF0Uj_-AeB1kAAEKgZCiFiI325.jpg");
            System.out.println(group1);
            System.out.println(fileInfo);
            NameValuePair[] metadata1 = client.get_metadata1("group1/M00/00/00/wKgohF0Uj_-AeB1kAAEKgZCiFiI325.jpg");
            trackerServer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //下载文件
    @Test
    public void TestdownLoad(){
        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);

            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);

            byte[] bytes = client.download_file1("group1/M00/00/00/wKgohF0Uj_-AeB1kAAEKgZCiFiI325.jpg");
            File file = new File("d:/a.png");
            FileOutputStream  fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            trackerServer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
