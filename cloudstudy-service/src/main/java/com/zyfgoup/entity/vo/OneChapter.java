package com.zyfgoup.entity.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OneChapter {

    private String id;

    private String title;

    private List<TwoVideo> children = new ArrayList<>();

}
