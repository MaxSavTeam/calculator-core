package com.maxsavteam.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public static BigDecimal rootWithBase(BigDecimal a, BigDecimal n) {
        BigDecimal log = MathUtils.ln( a );
        BigDecimal dLog = log.divide( n, 10, RoundingMode.HALF_EVEN );
        return MathUtils.exp( dLog );
    }

    public static BigDecimal pow(BigDecimal a, BigDecimal n) {
        if ( Fraction.isFraction( n ) ) {
            Fraction fraction = new Fraction( n.toPlainString() );
            return rootWithBase( sysPow( a, fraction.getNumerator() ), fraction.getDenominator() );
        }
        if ( n.compareTo( BigDecimal.ZERO ) < 0 ) {
            BigDecimal result = sysPow( a, n.multiply( BigDecimal.valueOf( -1 ) ) );
            String strRes = BigDecimal.ONE.divide( result, 8, RoundingMode.HALF_EVEN ).toPlainString();
            return new BigDecimal( deleteZeros( strRes ) );
        } else {
            return sysPow( a, n );
        }
    }

    private static BigDecimal sysPow(BigDecimal a, BigDecimal n) {
        if ( n.compareTo( BigDecimal.ZERO ) == 0 ) {
            return BigDecimal.ONE;
        }
        if ( getRemainder( n, BigDecimal.valueOf( 2 ) ).compareTo( BigDecimal.ONE ) == 0 ) {
            return sysPow( a, n.subtract( BigDecimal.ONE ) ).multiply( a );
        } else {
            BigDecimal b = sysPow( a, n.divide( BigDecimal.valueOf( 2 ), 0, RoundingMode.HALF_EVEN ) );
            return b.multiply( b );
        }
    }

    public static BigDecimal toRadians(BigDecimal decimal) {
        return decimal.multiply( MathUtils.PI ).divide( BigDecimal.valueOf( 180 ), 8, RoundingMode.HALF_EVEN );
    }

    public static BigDecimal getRemainder(BigDecimal a, BigDecimal b) {
        return a.remainder( b );
    }

}
