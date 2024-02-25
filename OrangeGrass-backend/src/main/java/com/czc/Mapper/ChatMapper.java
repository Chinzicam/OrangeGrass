package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.Chat;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMapper extends BaseMapper<Chat> {

    public int selectUnreadMessageNumber(String userId);

    public List<Chat> selectMessages(String userId,String friendId);

    public int readMessages(String myId,String friendId);
}
