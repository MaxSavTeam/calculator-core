package com.maxsavteam.calculator.utils;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.maxsavteam.calculator.exceptions.CalculatingException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtils {
	public static final BigDecimal E = new BigDecimal( "2.7182818284590452354" );
	public static final BigDecimal PI = new BigDecimal( "3.14159265358979323846" );
	public static final BigDecimal FI = new BigDecimal( "1.618" );

	private static int mRoundScale = 8;

	public static void setRoundScale(int mRoundScale) {
		MathUtils.mRoundScale = mRoundScale;
	}

	public static BigDecimal exp(BigDecimal x) {
		return BigDecimalMath.exp( x, new MathContext( mRoundScale ) );
	}

	public static BigDecimal ln(BigDecimal x) {
		if(x.signum() < 0)
			throw new CalculatingException(CalculatingException.NEGATIVE_PARAMETER_OF_LOG);
		return BigDecimalMath.log( x, new MathContext( mRoundScale ) );
	}

	public static BigDecimal log(BigDecimal x) {
		if(x.signum() < 0)
			throw new CalculatingException(CalculatingException.NEGATIVE_PARAMETER_OF_LOG);
		return BigDecimalMath.log10( x, new MathContext( mRoundScale ) );
	}

	public static BigDecimal logWithBase(BigDecimal x, BigDecimal base){
		BigDecimal logX = log( x ),
				logB = log( base );
		return logX.divide( logB, mRoundScale, RoundingMode.HALF_EVEN );
	}

	public static BigDecimal abs(BigDecimal x) {
		if ( x.signum() < 0 ) {
			return x.multiply( new BigDecimal( "-1" ) );
		} else {
			return x;
		}
	}

	public static BigDecimal tan(BigDecimal x) {
		if(x.compareTo(BigDecimal.valueOf(90)) == 0)
			throw new CalculatingException(CalculatingException.TAN_OF_90);
		return BigDecimalMath.tan( toRadians( x ), new MathContext( 6 ) );
	}

	public static BigDecimal sin(BigDecimal x) {
		return BigDecimalMath.sin( toRadians( x ), new MathContext( 6 ) );
	}

	public static BigDecimal cos(BigDecimal x) {
		return BigDecimalMath.cos( toRadians( x ), new MathContext( 6 ) );
	}

	public static BigDecimal fact(BigDecimal a, int step) {
		if(a.signum() == 0)
			return BigDecimal.ONE;
		BigDecimal x = a;
		BigDecimal ans = BigDecimal.ONE;
		BigDecimal bStep = BigDecimal.valueOf( step );
		for(; 0 <= x.compareTo( BigDecimal.ONE ); x = x.subtract( bStep )){
			ans = ans.multiply( x );
		}
		return ans;
	}

	public static BigDecimal floor(BigDecimal x){
		String s = x.toPlainString();
		int pos = s.indexOf( "." );
		if(pos == -1 || pos == s.length() - 1) {
			return x;
		}else{
			return new BigDecimal( s.substring( 0, pos ) );
		}
	}

	public static BigDecimal ceil(BigDecimal x){
		String s = x.toPlainString();
		int pos = s.indexOf( "." );
		if(pos == -1 || pos == s.length() - 1) {
			return x;
		}else {
			String afterDot = s.substring( pos + 1 );
			if(CalculatorUtils.removeZeros( afterDot ).equals( "0" ))
				return new BigDecimal( s.substring( 0, pos ) ); // delete part after dot

			BigDecimal b = abs( new BigDecimal( s.substring( 0, pos ) ) );
			b = b.add( BigDecimal.ONE );
			if(s.charAt( 0 ) == '-')
				b = b.multiply( BigDecimal.valueOf( -1 ) );
			return b;
		}
	}

	public static BigDecimal round(BigDecimal x) {
		String s = x.toPlainString();
		int pos = s.indexOf( "." );
		if ( pos == -1 || pos == s.length() - 1 ) {
			return x;
		} else {
			String newString = s.substring( 0, pos );
			char next = s.charAt( pos + 1 );
			if(next >= '0' && next < '5'){
				return new BigDecimal( newString );
			}else {
				BigDecimal b = abs( new BigDecimal( newString ) );
				b = b.add( BigDecimal.ONE );
				if(newString.charAt( 0 ) == '-')
					b = b.multiply( BigDecimal.valueOf( -1 ) );
				return b;
			}
		}
	}

	public static BigDecimal rootWithBase(BigDecimal a, BigDecimal n) {
		if(n.remainder(BigDecimal.valueOf(2)).signum() == 0 && a.signum() < 0)
			throw new CalculatingException(CalculatingException.ROOT_OF_EVEN_DEGREE_OF_NEGATIVE_NUMBER);
		BigDecimal log = ln( a );
		BigDecimal dLog = log.divide( n, 10, RoundingMode.HALF_EVEN );
		return exp( dLog );
	}

	public static BigDecimal powWithExp(BigDecimal a, BigDecimal n) {
		BigDecimal ln = ln( a );
		BigDecimal multiplying = n.multiply( ln );
		return exp( multiplying );
	}

	public static BigDecimal pow(BigDecimal a, BigDecimal n) {
		if(a.signum() == 0){
			if(n.signum() < 0)
				throw new CalculatingException(CalculatingException.NAN);
			else if(n.signum() == 0)
				throw new CalculatingException(CalculatingException.UNDEFINED);
			else
				return BigDecimal.ZERO;
		}
		if ( n.signum() < 0 ) {
			BigDecimal result = pow( a, n.multiply( BigDecimal.valueOf( -1 ) ) );
			String strRes = BigDecimal.ONE.divide( result, 8, RoundingMode.HALF_EVEN ).toPlainString();
			return new BigDecimal( CalculatorUtils.removeZeros( strRes ) );
		}
		if ( Fraction.isFraction( n ) ) {
			BigDecimal scaledN = n.setScale(3, RoundingMode.HALF_DOWN);
			Fraction fraction = new Fraction( scaledN.toPlainString() );
			return MathUtils.rootWithBase( sysPow( a, fraction.getNumerator() ), fraction.getDenominator() );
		}
		return sysPow( a, n );
	}

	private static BigDecimal sysPow(BigDecimal a, BigDecimal n) {
		if ( n.compareTo( BigDecimal.ZERO ) == 0 ) {
			return BigDecimal.ONE;
		}
		if ( n.remainder( BigDecimal.valueOf( 2 ) ).compareTo( BigDecimal.ONE ) == 0 ) {
			return sysPow( a, n.subtract( BigDecimal.ONE ) ).multiply( a );
		} else {
			BigDecimal b = sysPow( a, n.divide( BigDecimal.valueOf( 2 ), 0, RoundingMode.HALF_EVEN ) );
			return b.multiply( b );
		}
	}

	public static BigDecimal toRadians(BigDecimal decimal) {
		return decimal.multiply(PI).divide( BigDecimal.valueOf( 180 ), 8, RoundingMode.HALF_EVEN );
	}
}
