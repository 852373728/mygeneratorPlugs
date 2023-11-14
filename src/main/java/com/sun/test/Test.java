package com.sun.test;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * @date 2023/11/10
 */
public class Test {
    public static void main(String[] args) {


        LocalDate now = LocalDate.now();

        long time = Timestamp.valueOf(now.atStartOfDay()).getTime();
        System.out.println(time);
    }
}
