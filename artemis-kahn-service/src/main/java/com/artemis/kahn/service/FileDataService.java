package com.artemis.kahn.service;

import com.artemis.kahn.core.bean.Harvest;
import com.artemis.kahn.dao.mongo.entity.FileData;
import com.artemis.kahn.dao.mongo.repo.FileDataDao;
import com.mongodb.BasicDBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class FileDataService {

    public static final Logger LOG = LoggerFactory.getLogger(FileDataService.class);

    @Autowired
    private FileDataDao fileDataDao;

    private FileDataService() {

    }

    /**
     * file_data是capped mongo
     *
     * @param url
     * @return
     */
    public FileData findByUrl(String url) {
        // 获取最新的一条数据
        List<FileData> list = fileDataDao.findAll(new BasicDBObject("md5", FileData.md5(url)), new BasicDBObject("c_date", -1), 1);
        if (list.size() <= 0) {
            return null;
        }

        FileData fileData = list.get(0);
        if (fileData != null) {
            try {
                fileData.setContent(unzip(fileData.getContent()));
            } catch (IOException e) {
                LOG.error("", e);
                return null;
            }
        }

        return fileData;
    }

    public void saveFileData(String url, byte[] content) {
        if (url == null || content == null || content.length == 0) {
            return;
        }

        FileData fileData = new FileData();
        fileData.setMd5(FileData.md5(url));
        fileData.setUrl(url);
        fileData.setContent(content);
        fileData.setCreationDate(new Date());

        try {
            fileData.setContent(zip(fileData.getContent()));
        } catch (IOException e) {
            LOG.error("", e);
        }

        fileDataDao.insertList(Arrays.asList(fileData));
    }

    public void saveFileData(List<Harvest> harvests) {
        if (harvests.isEmpty()) {
            return;
        }
        List<FileData> fileDataList = new ArrayList<FileData>();
        for (Harvest harvest : harvests) {
            if (harvest.getUrl() == null || harvest.getContent() == null || harvest.getContent().length == 0) {
                continue;
            }
            FileData fileData = new FileData();
            fileData.setMd5(FileData.md5(harvest.getUrl()));
            fileData.setUrl(harvest.getUrl());
            fileData.setContent(harvest.getContent());
            fileData.setCreationDate(new Date());
            try {
                fileData.setContent(zip(fileData.getContent()));
            } catch (IOException e) {
                LOG.error("", e);
            }
            fileDataList.add(fileData);
        }
        fileDataDao.insertList(fileDataList);
    }

//	/**
//	 * 检查是否有缓存页面
//	 *
//	 * @param url
//	 * @param jobId
//	 * @return
//	 */
//	public boolean isExpired(String url, String jobId) {
//		FileData fileData = findByUrl(url);
//		if (fileData == null) {
//			return true;
//		}
//
//		Job job = jobService.getJobById(jobId);
//		if (job == null) {
//			return true;
//		}
//
//		Page page = taskService.matchPage(jobId, url);
//		if (page == null) {
//			return true;
//		}
//
//		// 不缓存
//		if (page.getExpires() == 0) {
//			return true;
//		}
//
//		// 永不过期
//		if (page.getExpires() == -1) {
//			return false;
//		}
//
//		if ((System.currentTimeMillis() - fileData.getCreationDate().getTime()) > page.getExpires() * 60 * 1000) {
//			return true;
//		}
//
//		return false;
//	}

    /**
     * 压缩
     *
     * @param content
     * @return
     * @throws IOException
     */
    private byte[] zip(byte[] content) throws IOException {
        long begin = System.currentTimeMillis();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream outputStream = new GZIPOutputStream(bos);
            outputStream.write(content);
            outputStream.finish();
            return bos.toByteArray();
        } finally {
            performance(begin, "zip", "len:" + content.length);
        }
    }

    /**
     * 解压
     *
     * @param content
     * @return
     * @throws IOException
     */
    private byte[] unzip(byte[] content) throws IOException {
        long begin = System.currentTimeMillis();
        try {
            byte[] buff = new byte[10240];
            ByteArrayInputStream bis = new ByteArrayInputStream(content);
            GZIPInputStream inputStream = new GZIPInputStream(bis);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (true) {
                int len = inputStream.read(buff);
                if (len > 0) {
                    bos.write(buff, 0, len);
                } else {
                    break;
                }
            }
            return bos.toByteArray();
        } finally {
            performance(begin, "unzip", "len:" + content.length);
        }
    }

    private void performance(long beginMills, String operation, String msg) {
        long diffMills = System.currentTimeMillis() - beginMills;
        StringBuilder builder = new StringBuilder();
        builder.append(" operation:").append(operation);
        builder.append("\ttime:").append(diffMills);
//        if (diffMills > 500) {
//            LOG.performance(Level.ERROR, builder.toString());
//        } else if (diffMills > 100) {
//            LOG.performance(Level.WARN, builder.toString());
//        } else {
//            LOG.performance(Level.DEBUG, builder.toString());
//        }
    }

}
