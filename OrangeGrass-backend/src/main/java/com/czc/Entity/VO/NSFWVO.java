package com.czc.Entity.VO;

import lombok.Data;

import java.util.List;

@Data
public class NSFWVO {
    private int code;
    private int status;
    private List<String> maybeNSFW;
    private List<String> superNSFW;
    private List<nsfwData> data;
}

@Data
class nsfwData {
    private String second;
    private String probability;
}
