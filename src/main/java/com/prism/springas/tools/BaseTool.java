package com.prism.springas.tools;

import java.util.UUID;

public class BaseTool {

    /*
        -获取32位标准UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

}
