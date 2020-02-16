/*
 * Copyright (C) 2017 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openitvn.format.msg;

/**
 *
 * @author Thinh Pham
 */
public class MTChar {
    
    public final int charCode;
    public final String decode;
    
    public MTChar(int charCode, String decode) {
        this.charCode = charCode;
        switch (decode) {
            case "\\s":
                this.decode = " ";
                break;
                
            case "\\t":
                this.decode = "\t";
                break;
                
            default:
                this.decode = decode;
                break;
        }
    }
}
