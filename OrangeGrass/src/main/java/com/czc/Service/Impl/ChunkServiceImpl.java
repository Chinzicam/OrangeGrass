package com.czc.Service.Impl;

import com.czc.Entity.PO.ChunkPO;
import com.czc.Mapper.ChunkMapper;
import com.czc.Service.ChunkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkServiceImpl implements ChunkService {
    @Autowired
    private ChunkMapper chunkMapper;

    @Override
    public Integer saveChunk(MultipartFile chunk, String md5, Integer index, Long chunkSize, String FileName ,String path) {
        File file = new File(path);
        if (!file .exists() && !file .isDirectory())
        {
            System.out.println("创建"+path);
            file.setWritable(true, false);
            file.mkdirs();
        } else {
        }
        String resultFileName = path + "/" + FileName;


        try (RandomAccessFile randomAccessFile = new RandomAccessFile(resultFileName, "rw")) {

            // 偏移量
            long offset = chunkSize * (index - 1);
            // 定位到该分片的偏移量
            randomAccessFile.seek(offset);
            // 写入
            randomAccessFile.write(chunk.getBytes());

            ChunkPO chunkPO = new ChunkPO(md5,index);
            return chunkMapper.insertChunk(chunkPO);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }



    }

    @Override
    public List<Integer> selectChunkListByMd5(String md5) {
        List<ChunkPO> chunkPOList = chunkMapper.selectChunkListByMd5(md5);
        List<Integer> indexList = new ArrayList<>();
        for (ChunkPO chunkPO : chunkPOList) {
            indexList.add(chunkPO.getIndex());
        }
        return indexList;
    }

    @Override
    public void deleteChunkByMd5(String md5) {
        chunkMapper.deleteChunkByMd5(md5);
    }

    @Override
    public byte[] getChunk(Integer index, Integer chunkSize, String resultFileName,long offset) {
        File resultFile = new File(resultFileName);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(resultFileName, "r")) {
            // 定位到该分片的偏移量
            randomAccessFile.seek(offset);
            //读取
            byte[] buffer = new byte[chunkSize];
            randomAccessFile.read(buffer);
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }
}
