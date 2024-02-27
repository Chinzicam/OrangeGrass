package com.czc.Controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.czc.Config.annotation.CostTime;
import com.czc.Constant.HttpResonse;
import com.czc.Entity.FriendApplyRecord;
import com.czc.Entity.UserFriendRecord;
import com.czc.Service.FriendApplyService;
import com.czc.Service.UserFriendService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

import static com.czc.Constant.ApplyConstant.APPLY_PASS;

@RestController
@RequestMapping("/api/apply")
public class ApplyController {

    @Autowired
    private FriendApplyService friendApplyService;
    @Autowired
    private UserFriendService userFriendService;


    @CostTime
    @GetMapping("/friend")
    public HttpResonse getFriendApplyRecord(@Param("userId") String userId) {
        return HttpResonse.success().setMsg("查询成功")
                .setData(friendApplyService.getFriendApplyVO("",userId));
    }

    @CostTime
    @GetMapping("/friend/my")
    public HttpResonse getMyFriendApplyRecord(@Param("userId") String userId) {
        return HttpResonse.success().setMsg("查询成功")
                .setData(friendApplyService.getFriendApplyVO(userId,""));
    }

    @CostTime
    @PutMapping("/friend")
    public HttpResonse modifyFriendApplyRecord(@Param("id") String id,
                                               @Param("result") int result) {
        if (friendApplyService.update(new UpdateWrapper<FriendApplyRecord>()
                .eq("friend_apply_record_id",id)
                .set("apply_result",result))) {
            /**
             * 添加好友
             */
            if (result == APPLY_PASS) {
                Date date = new Date();
                FriendApplyRecord friendApplyRecord = friendApplyService.getById(id);
                UserFriendRecord record1 = new UserFriendRecord();
                record1.setUserId(friendApplyRecord.getFromUserId());
                record1.setFriendId(friendApplyRecord.getTargetUserId());
                record1.setCreateTime(date);
                UserFriendRecord record2 = new UserFriendRecord();
                record2.setUserId(friendApplyRecord.getTargetUserId());
                record2.setFriendId(friendApplyRecord.getFromUserId());
                record2.setCreateTime(date);
                if (!userFriendService.save(record1)
                        || !userFriendService.save(record2)) {
                    return HttpResonse.fail().setMsg("接受失败");
                }
            }
            return HttpResonse.success().setMsg(result == 2 ? "拒绝成功" : "接受成功");
        }
        return HttpResonse.fail().setMsg(result == 2 ? "拒绝失败" : "接受失败");
    }

    @CostTime
    @PostMapping("/friend")
    public HttpResonse addFriendApplyRecord(@Param("fromUserId") String fromUserId,
                                            @Param("targetUserId") String targetUserId,
                                            @Param("applyReason") String applyReason) {
        if (userFriendService
                .getOne(new QueryWrapper<UserFriendRecord>()
                        .eq("user_id",fromUserId)
                        .eq("friend_id",targetUserId)) != null) {
            return HttpResonse.fail().setMsg("你已经是该用户的好友");
        }
        if (friendApplyService
                .getOne(new QueryWrapper<FriendApplyRecord>()
                        .eq("from_user_id",fromUserId)
                        .eq("target_user_id",targetUserId)
                        .eq("apply_result",0)) != null) {
            return HttpResonse.fail().setMsg("你已经发送过申请，重复发送");
        }
        FriendApplyRecord record = new FriendApplyRecord();
        record.setFriendApplyRecordId(UUID.randomUUID()
                .toString().replaceAll("-",""));
        record.setApplyReason(applyReason);
        record.setFromUserId(fromUserId);
        record.setTargetUserId(targetUserId);
        record.setApplyTime(new Date());
        if (friendApplyService.save(record)) {
            return HttpResonse.success().setMsg("添加申请记录成功");
        }
        return HttpResonse.fail().setMsg("添加申请记录失败");
    }
}
