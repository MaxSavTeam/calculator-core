package com.maxsavteam.utils;

import java.math.BigDecimal;

public class CalculatorUtils {

    public static String deleteZeros(String s){
        String res = s;
        int len = res.length();
        if ( res.contains( "." ) && res.charAt( len - 1 ) == '0' ) {
            while ( res.charAt( len - 1 ) == '0' ) {
                len--;
                res = res.substring( 0, len );
            }
            if ( res.charAt( len - 1 ) == '.' ) {
                res = res.substring( 0, len - 1 );
            }
        }
        while ( res.charAt( 0 ) == '0' && res.length() > 1 && res.charAt( 1 ) != '.' ) {
            res = res.substring( 1 );
        }
        return res;
    }

    public static BigDecimal deleteZeros(BigDecimal b){
        return new BigDecimal(deleteZeros(b.toPlainString()));
    }

}
