package com.adityagunjal.sdl_project.helpers;

public class Helpers {

    public static  int nextEven(int n){
        return (n % 2 == 0) ? n + 2 : n + 1;
    }

    public static int nextOdd(int n){
        return (n % 2 == 0) ? n + 1 : n + 2;
    }

    public static int stringCompare(String str1, String str2)
    {

        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }

        if (l1 != l2) {
            return l1 - l2;
        } else {
            return 0;
        }
    }

}
