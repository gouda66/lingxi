package com.lingxi.isi.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.*;

import java.io.File;
import java.util.UUID;

/**
 * OSS 分片上传工具类
 * 实现大文件的分片上传功能，支持断点续传
 * 线程安全，支持多用户并发上传
 */
public class OssUploadUtils {

    /**
     * 上传文件到阿里云 OSS
     * 
     * @param bucket Bucket 名称
     * @param objectKey OSS 上的文件路径
     * @param uploadFilePath 本地文件路径
     * @return 上传是否成功
     */
    public static boolean uploadFile(String bucket, String objectKey, String uploadFilePath) {
        // Endpoint 以华东 1（杭州）为例，其它 Region 请按实际情况填写
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        // 填写 Bucket 所在地域。以华东 1（杭州）为例，Region 填写为 cn-hangzhou
        String region = "cn-beijing";
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");

        if (accessKeyId == null || accessKeySecret == null) {
            throw new IllegalStateException("请设置环境变量 OSS_ACCESS_KEY_ID 和 OSS_ACCESS_KEY_SECRET");
        }
    
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量 OSS_ACCESS_KEY_ID 和 OSS_ACCESS_KEY_SECRET
        DefaultCredentialProvider provider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
            
        // 创建 OSSClient 实例
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
                
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(provider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();
    
        try {
            // 检查文件是否已存在，如果存在则生成新文件名
            String finalObjectKey = getUniqueObjectKey(ossClient, bucket, objectKey);
                
            // 生成唯一的 checkpoint 文件名，避免并发冲突
            // 使用 UUID 确保每个上传任务都有独立的进度记录文件
            String checkpointDir = System.getProperty("java.io.tmpdir") + File.separator + "oss-upload";
            File checkpointDirFile = new File(checkpointDir);
            if (!checkpointDirFile.exists()) {
                checkpointDirFile.mkdirs();
            }
            String uniqueId = UUID.randomUUID().toString().replace("-", "");
            String fileName = new File(uploadFilePath).getName();
            String checkpointFile = checkpointDir + File.separator + fileName + "." + uniqueId + ".ucp";
            
            ObjectMetadata meta = new ObjectMetadata();
            // 指定上传的内容类型
            // meta.setContentType("image/jpeg");
    
            // 文件上传时设置访问权限 ACL
            // meta.setObjectAcl(CannedAccessControlList.Private);
    
            // 通过 UploadFileRequest 设置多个参数
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucket, finalObjectKey);
    
            // 通过 UploadFileRequest 设置单个参数
            // 填写本地文件的完整路径
            uploadFileRequest.setUploadFile(uploadFilePath);
                
            // 指定上传并发线程数，默认值为 1
            uploadFileRequest.setTaskNum(5);
                
            // 指定上传的分片大小，单位为字节，取值范围为 100 KB~5 GB。默认值为 100 KB
            uploadFileRequest.setPartSize(1024 * 1024); // 1MB
                
            // 开启断点续传，默认关闭
            uploadFileRequest.setEnableCheckpoint(true);
                
            // 记录本地分片上传结果的文件。上传过程中的进度信息会保存在该文件中
            // 如果某一分片上传失败，再次上传时会根据文件中记录的点继续上传
            // 上传完成后，该文件会被删除
            // 如果未设置该值，默认与待上传的本地文件同路径，名称为${uploadFile}.ucp
            uploadFileRequest.setCheckpointFile(checkpointFile);
                
            // 文件的元数据
            uploadFileRequest.setObjectMetadata(meta);
                
            // 设置上传回调，参数为 Callback 类型
            // uploadFileRequest.setCallback("yourCallbackEvent");
    
            // 断点续传上传
            ossClient.uploadFile(uploadFileRequest);
                
            System.out.println("文件上传成功！OSS 路径：" + finalObjectKey);
            return true;

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return false;
        } catch (Throwable ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            ce.printStackTrace();
            return false;
        } finally {
            // 关闭 OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    /**
     * 获取唯一的 objectKey，如果文件已存在则生成新文件名
     * 
     * @param ossClient OSS 客户端
     * @param bucket Bucket 名称
     * @param objectKey 原始 objectKey
     * @return 唯一的 objectKey
     */
    private static String getUniqueObjectKey(OSS ossClient, String bucket, String objectKey) {
        // 分离目录和文件名
        int lastSlashIndex = objectKey.lastIndexOf('/');
        String directory = lastSlashIndex >= 0 ? objectKey.substring(0, lastSlashIndex + 1) : "";
        String fileName = lastSlashIndex >= 0 ? objectKey.substring(lastSlashIndex + 1) : objectKey;
        
        // 分离文件名和扩展名
        int dotIndex = fileName.lastIndexOf('.');
        String nameWithoutExtension = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
        String extension = dotIndex > 0 ? fileName.substring(dotIndex) : "";
        
        // 检查文件是否存在
        if (ossClient.doesObjectExist(bucket, objectKey)) {
            System.out.println("文件已存在：" + objectKey + ", 生成新文件名...");
            
            // 生成带时间戳的新文件名
            String timestamp = String.valueOf(System.currentTimeMillis());
            String newFileName = nameWithoutExtension + "_" + timestamp + extension;
            String newObjectKey = directory + newFileName;
            
            System.out.println("新文件名：" + newObjectKey);
            return newObjectKey;
        }
        
        return objectKey;
    }

    /**
     * 从阿里云 OSS 下载文件到本地
     * 
     * @param bucket Bucket 名称
     * @param objectKey OSS 上的文件路径（不包含 Bucket 名称）
     * @param localFilePath 本地文件完整路径
     * @return 下载是否成功
     */
    public static boolean downloadFile(String bucket, String objectKey, String localFilePath) {
        // Endpoint 以华东 1（北京）为例，其它 Region 请按实际情况填写
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        // 填写 Bucket 所在地域。以北京为例，Region 填写为 cn-beijing
        String region = "cn-beijing";
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");

        if (accessKeyId == null || accessKeySecret == null) {
            throw new IllegalStateException("请设置环境变量 OSS_ACCESS_KEY_ID 和 OSS_ACCESS_KEY_SECRET");
        }
        // 创建凭证提供者
        DefaultCredentialProvider provider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);

        // 创建 OSSClient 实例
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(provider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            // 下载 Object 到本地文件，并保存到指定的本地路径中
            // 如果指定的本地文件存在会覆盖，不存在则新建
            ossClient.getObject(new GetObjectRequest(bucket, objectKey), new File(localFilePath));
            System.out.println("文件下载成功！本地路径：" + localFilePath);
            return true;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return false;
        } catch (Throwable ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            ce.printStackTrace();
            return false;
        } finally {
            // 关闭 OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

}
