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

    public static final Map<Integer, String> REVIEW_STATUS_MAP = new HashMap<Integer, String>();

    static {
        REVIEW_STATUS_MAP.put(STATE_DRAFT, STATE_STRING_DRAFT);
        REVIEW_STATUS_MAP.put(STATE_APPROVING, STATE_STRING_APPROVING);
        REVIEW_STATUS_MAP.put(STATE_APPROVED, STATE_STRING_APPROVED);
    }

    /**
     * 默认密码
     */
    public static String DEFAULT_PASSWORD = "$2a$10$Lq4xOF33YvM6voT15RuTw.0kmfTWOlFX/v4wM1n2OaRQDbBOVCPxi";

}
