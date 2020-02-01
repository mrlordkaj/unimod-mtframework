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
package com.openitvn.unicore.plugin.msg;

import com.openitvn.unicore.data.DataStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Thinh Pham
 */
public abstract class MTMessageConverter {
    
    public static Integer[] encodeMessage(String originString) {
        //at first, we convert all control code to human-readable hex
        Pattern controlPattern = Pattern.compile("^.*(\\<[A-Za-z]+\\>).*$");
        Matcher controlMatcher = controlPattern.matcher(originString);
        while (controlMatcher.matches()) {
            String controlCode = controlMatcher.group(1);
            int charCode = MTCharmap.getInstance().encode(controlCode);
            originString = originString.replaceFirst(controlCode, String.format("[0x%08X]", charCode));
            controlMatcher = controlPattern.matcher(originString);
        }
        
        //then, we break down to array of single human-readable hex codes,
        //at last, convert all to integer values upon character map
        StringBuilder sb = new StringBuilder(originString);
        Pattern hexPattern = Pattern.compile("^\\[0[xX][0-9a-fA-F]{8}\\].*$");
        ArrayList<Integer> charCodes = new ArrayList<>();
        while (sb.length() > 0) {
            Matcher hexMatcher = hexPattern.matcher(sb);
            if (hexMatcher.matches()) {
                String hexCode = sb.substring(3, 11);
                charCodes.add(Integer.parseInt(hexCode, 16));
                sb.delete(0, 12);
            } else {
                charCodes.add(MTCharmap.getInstance().encode(String.valueOf(sb.charAt(0))));
                sb.deleteCharAt(0);
            }
        }
        
        //convert to array of integers
        return charCodes.toArray(new Integer[charCodes.size()]);
    }
    
    public static String decodeMessage(DataStream ds) {
        StringBuilder sb = new StringBuilder();
        int charCode;
        boolean lineRemaining = true;
        
        int endCode = MTCharmap.getInstance().getEndCode();
        while (lineRemaining) {
            charCode = ds.getInt();
            if (charCode == endCode) {
                lineRemaining = false;
            } else {
                String decode = MTCharmap.getInstance().decode(charCode);
                if(decode == null) sb.append(String.format("[0x%08X]", charCode));
                else sb.append(decode);
            }
        }
        return sb.toString();
    }
}
