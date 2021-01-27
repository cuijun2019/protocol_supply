package com.etone.protocolsupply.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cuijun
 * @date 2018/12/28
 */
public class Constant {

    /**
     * 是否删除 -- 是
     */
    public static int DELETE_YES = 1;

    /**
     * 是否删除 -- 否
     */
    public static int DELETE_NO = 2;

    /**
     * 是否变更--是
     */
    public static int UPDATW_YES = 1;

    /**
     * 是否变更--否
     */
    public static int UPDATW_NO = 2;

    /**
     * 是否被引用--是
     */
    public static int BEUSE_YES = 1;

    /**
     * 是否被引用--否
     */
    public static int BEUSE_NO = 2;

    /**
     * 是否推荐供应商 -- 是
     */
    public static int RECOMMEND_SUPPLIER_YES = 1;

    /**
     * 是否推荐供应商 -- 否
     */
    public static int RECOMMEND_SUPPLIER_NO = 2;

    /**
     * 状态 -- 激活
     */
    public static int STATUS_ACTIVE = 1;
    public static final String STATUS_STRING_ACTIVE = "激活";

    /**
     * 状态 -- 冻结
     */
    public static int STATUS_FREEZE = 2;
    public static final String STATUS_STRING_FREEZE = "冻结";

    public static final Map<Integer, String> STATUS_MAP = new HashMap<Integer, String>();

    static {
        STATUS_MAP.put(STATUS_ACTIVE, STATUS_STRING_ACTIVE);
        STATUS_MAP.put(STATUS_FREEZE, STATUS_STRING_FREEZE);
    }

    /**
     * 拟稿
     */
    public static final int    NOTICE_MONEY            = 200000;
    /**
     * 拟稿
     */
    public static final int    STATE_DRAFT            = 1;
    public static final String STATE_STRING_DRAFT     = "拟稿";
    /**
     * 审批中
     */
    public static final int    STATE_APPROVING        = 2;
    public static final String STATE_STRING_APPROVING = "审批中";
    /**
     * 已审批
     */
    public static final int    STATE_APPROVED         = 3;
    public static final String STATE_STRING_APPROVED  = "审批完毕";
    /**
     * 待签收
     */
    public static final int    STATE_WAIT_SIGN         = 7;
    public static final String STATE_STRING_WAIT_SIGN  = "待签收";
    /**
     * 已签收
     */
    public static final int    STATE_SIGNED         = 8;
    public static final String STATE_STRING_SIGNED  = "已签收";

    public static final Map<Integer, String> REVIEW_STATUS_MAP = new HashMap<>();

    static {
        REVIEW_STATUS_MAP.put(STATE_DRAFT, STATE_STRING_DRAFT);
        REVIEW_STATUS_MAP.put(STATE_APPROVING, STATE_STRING_APPROVING);
        REVIEW_STATUS_MAP.put(STATE_APPROVED, STATE_STRING_APPROVED);
        REVIEW_STATUS_MAP.put(STATE_WAIT_SIGN, STATE_STRING_WAIT_SIGN);
        REVIEW_STATUS_MAP.put(STATE_SIGNED, STATE_STRING_SIGNED);
    }

    /**
     * 默认密码
     */
    public static String DEFAULT_PASSWORD = "$2a$10$Lq4xOF33YvM6voT15RuTw.0kmfTWOlFX/v4wM1n2OaRQDbBOVCPxi";

    /**
     * 流程类型
     */
    public static final String    BUSINESS_TYPE_P        = "projectAudit";
    public static final String BUSINESS_TYPE_P_STRING  = "项目管理审核";

    public static final String    BUSINESS_TYPE_E        = "enquiryAudit";
    public static final String BUSINESS_TYPE_E_STRING  = "询价管理审核";

    public static final String    BUSINESS_TYPE_C        = "cargoAudit";
    public static final String BUSINESS_TYPE_C_STRING  = "产品管理审核";

    public static final String    BUSINESS_TYPE_A        = "agentAudit";
    public static final String BUSINESS_TYPE_A_STRING  = "代理商管理审核";

    public static final Map<String, String> BUSINESS_TYPE_STATUS_MAP = new HashMap<>();
    static {
        BUSINESS_TYPE_STATUS_MAP.put(BUSINESS_TYPE_P, BUSINESS_TYPE_P_STRING);
        BUSINESS_TYPE_STATUS_MAP.put(BUSINESS_TYPE_E, BUSINESS_TYPE_E_STRING);
        BUSINESS_TYPE_STATUS_MAP.put(BUSINESS_TYPE_C, BUSINESS_TYPE_C_STRING);
        BUSINESS_TYPE_STATUS_MAP.put(BUSINESS_TYPE_A, BUSINESS_TYPE_A_STRING);
    }


    /**
     * 待办类型
     */
    public static final Integer    BUSINESS_TYPE_DAIBAN        = 0;
    public static final String BUSINESS_TYPE_DAIBAN_STRING  = "待办";

    public static final Integer    BUSINESS_TYPE_YIBAN        = 1;
    public static final String BUSINESS_TYPE_YIBAN_STRING  = "已办";

    public static final Map<Integer, String> BUSINESS_TYPE_T_STATUS_MAP = new HashMap<>();
    static {
        BUSINESS_TYPE_T_STATUS_MAP.put(BUSINESS_TYPE_DAIBAN, BUSINESS_TYPE_DAIBAN_STRING);
        BUSINESS_TYPE_T_STATUS_MAP.put(BUSINESS_TYPE_YIBAN, BUSINESS_TYPE_YIBAN_STRING);
    }

    /**
     * 待阅、已阅
     */
    public static final Integer    BUSINESS_TYPE_DAIYUE        = 0;
    public static final String BUSINESS_TYPE_DAIYUE_STRING  = "待阅";

    public static final Integer    BUSINESS_TYPE_YIYUE       = 1;
    public static final String BUSINESS_TYPE_YIYUE_STRING  = "已阅";


    public static final Map<Integer, String> BUSINESS_READ_TYPE_T_STATUS_MAP = new HashMap<>();
    static {
        BUSINESS_TYPE_T_STATUS_MAP.put(BUSINESS_TYPE_DAIYUE, BUSINESS_TYPE_DAIYUE_STRING);
        BUSINESS_TYPE_T_STATUS_MAP.put(BUSINESS_TYPE_YIYUE, BUSINESS_TYPE_YIYUE_STRING);
    }
}
