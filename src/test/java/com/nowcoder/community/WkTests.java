package com.nowcoder.community;

import java.io.IOException;

/**
 * @author chenmin
 * @date 2021/11/13
 */
public class WkTests {

    public static void main(String[] args) {
        String cmd = "/usr/local/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com /Users/chenmin/Documents/Work/data/wk-images/3.png";
        try {
            // 提交给操作系统进行完成
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
