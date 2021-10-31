/*
 * Copyright (C) 2021 MaxSav Team
 *
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

package com.maxsavteam;

import com.maxsavteam.calculator.Calculator;
import com.maxsavteam.calculator.exceptions.CalculatingException;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        Scanner scanner = new Scanner(System.in);
        //calculator.setDecimalSeparator(scanner.nextLine().charAt(0));
        //calculator.setGroupingSeparator(scanner.nextLine().charAt(0));
        while(true){
            String exp = scanner.nextLine();
            if(exp.equals("stop"))
                break;
            if(exp.equals("restart")){
                main(args);
                break;
            }
            long start = System.currentTimeMillis();
            try {
                System.out.println(calculator.calculate(exp).format());
            }catch (CalculatingException e){
                e.printStackTrace();
            }
            System.err.println(System.currentTimeMillis() - start);
        }
    }

}
