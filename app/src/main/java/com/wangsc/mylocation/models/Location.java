package com.wangsc.mylocation.models;

import java.util.UUID;

/**
 * Created by Administrator on 2017/6/28.
 */

public class Location {

    public UUID Id;
    public UUID UserId;

    public int LocationType; //定位类型
    public double Longitude; //经    度
    public double Latitude; //纬    度
    public float Accuracy; //精    度
    public String Provider; //提供者

    public float Speed; //速    度
    public float Bearing; //角    度

    public int Satellites; //星    数
    public String Country; //国    家
    public String Province; //省
    public String City; //市
    public String CityCode; //城市编码
    public String District; //区
    public String AdCode; //区域 码
    public String Address; //地    址
    public String PoiName; //兴趣点

    public long Time; // 定位时间
    public String Summary;
}
