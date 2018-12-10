package com.zkml.terminal.service.selfterminal.util;
/**
 * @Author: likun
 * @Date: Created in  2018/12/10 16:35
 * @Description:
 */
public class MessageIdUtil {
    public static final String MESSAGE_HEADER = "564B";// 终端通用应答
    public static final String GENERAL_RESPONSE = "0001";// 终端通用应答
    public static final String REPORT_ON_TIME = "0200";// 终端定时上报
    public static final String QUERY_STATE = "8102";// 查询终端状态
    public static final String SETTING_PARAMETERS = "8103";// 设置终端参数
    public static final String QUERY_PARAMETERS = "8104";//查询终端参数
    public static final String REPLY_QUERY_PARAMETERS = "0104";// 查询终端参数应答
    public static final String TERMINAL_CONTROL = "8105";// 终端控制
    public static final String VEHICLE_VIN_REQUEST = "8106";// 车辆VIN请求
    public static final String VEHICLE_DETECTION_REPORT = "8107";// 车辆检测报告发送
}
