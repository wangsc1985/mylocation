package com.wangsc.mylocation.models;

import java.util.UUID;

/**
 * Created by Administrator on 2017/6/28.
 */

public class Location {

    public UUID id;
    public UUID userId;

    public int locationType; //定位类型
    public double longitude; //经    度
    public double latitude; //纬    度
    public String provider; //提供者

    public float accuracy; //精    度
    public float speed; //速    度
    public float bearing; //角    度

    public int satellites; //星    数
    public String country; //国    家
    public String province; //省
    public String city; //市
    public String cityCode; //城市编码
    public String district; //区
    public String adCode; //区域 码
    public String address; //地    址
    public String poiName; //兴趣点

    public long time; // 定位时间
    public String summary;
}
