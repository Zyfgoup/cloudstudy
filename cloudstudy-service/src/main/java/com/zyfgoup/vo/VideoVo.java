package com.zyfgoup.vo;

import lombok.Data;

@Data
public class VideoVo {

    private String id;

    private String title;

    private Boolean isFree;

    private String videoSourceId; //视频id
}
