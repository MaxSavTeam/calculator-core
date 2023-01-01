/*
 * Copyright (C) 2022 MaxSav Team
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.maxsavteam.calculator.Calculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoreTest {

	private final Calculator calculator = new Calculator();

	String calc(String input) {
		return calculator.calculate(input).format();
	}

	@Test
	void testSimple(){
		assertEquals("1", calc("1"));
		assertEquals("-1", calc("-1"));

		assertEquals("2", calc("1+1"));
		assertEquals("0", calc("1-1"));
		assertEquals("-1", calc("1-2"));

		assertEquals("21", calc("-3+24"));
		assertEquals("21", calc("-3+24*1"));
		assertEquals("21", calc("24-3"));
		assertEquals("6", calc("-3+24*1-15"));
	}

	@Test
	void testFunctions(){
		assertEquals("0", calc("sin(0)"));
		assertEquals("0", calc("sin0"));

		assertEquals("2", calc("sqrt4"));
		assertEquals("2", calc("sqrt4(16)"));

		assertEquals("-1", calc("log0.5(2)"));
	}

	@Test
	void testLists(){
		assertEquals("(1; 2; 3)", calc("(1;2;3)"));
		assertEquals("(1; 2; 3)", calc("1; 2;3"));
		assertEquals("(1; 5)", calc("(1;2+3)"));
		assertEquals("(2; 4)", calc("2*(1;2)"));
	}

}
