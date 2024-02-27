package com.czc.Constant;

import static com.czc.Constant.FileConstant.UPLOAD_PATH;

public class NSFW {
    public static final int NSFW_FILE_TYPE_IMAGE = 1;
    public static final int NSFW_FILE_TYPE_VIDEO = 2;
    public static final int NSFW_TYPE_HIGH_PROBABILITY = 1;
    public static final int NSFW_TYPE_MEDIUM_PROBABILITY = 2;
    public static final int NSFW_BAN = 1;
    public static final int NSFW_NOT_BAN = 0;
    public static final double NSFW_BAN_SCORE = 0.6;
    public static final String NSFW_IMG_PATH = UPLOAD_PATH +  "ban/ban.jpeg";
    public static final String NSFW_IMG_CHECK_ENABLE = "图片上传时鉴黄";
    public static final String NSFW_VIDEO_CHECK_ENABLE = "视频上传时鉴黄";
}
