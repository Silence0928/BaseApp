package com.lib_common.webservice.api;

public class WebMethodApi {

    // 登录
    public static final String loginMethod = "Login";
    // 扫描采集
    public static final String scannerMethod = "Inbound_Get";
    // 入库采集保存
    public static final String inboundSaveMethod = "Inbound_Save";
    // 入库审核获取数据
    public static final String inboundAuditGetMethod = "InboundCheck_Get";
    // 入库审核保存
    public static final String inboundAuditSaveMethod = "InboundCheck_Save";
    // 退库采集
    public static final String returnMethod = "Return_Get";
    // 退库采集保存
    public static final String returnSaveMethod = "Return_Save";
    // 入库审核获取数据
    public static final String returnAuditGetMethod = "ReturnCheck_Get";
    // 入库审核保存
    public static final String returnAuditSaveMethod = "ReturnCheck_Save";
    // 在库查询
    public static final String queryLibraryMethod = "Instock_Get";
    // 在库调整查询
    public static final String queryAdjustmentLibraryMethod = "InstockChange_Get";
    // 在库调整保存
    public static final String saveAdjustmentLibraryMethod = "InstockChange_Save";
    // 出货准备查询
    public static final String queryShipmentPrepareMethod = "OutDocBind_Get";
    // 出货准备保存
    public static final String saveShipmentPrepareMethod = "OutDocBind_Save";
    // 移库查询
    public static final String queryMoveDataMethod = "Move_Get";
    // 移库保存
    public static final String saveMoveDataMethod = "Move_Save";
    // 移库审核查询
    public static final String queryMoveAuditDataMethod = "MoveCheck_Get";
    // 移库审核保存
    public static final String saveMoveAuditDataMethod = "MoveCheck_Save";
}
