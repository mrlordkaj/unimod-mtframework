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

import com.openitvn.unicore.data.DataStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Thinh Pham
 */
public abstract class MTMessageCoder {
    
    public static ArrayList<Integer> encodeMessage(String original) {
        // at first, we convert all control code to human-readable hex
        Pattern ctrlPattern = Pattern.compile("^.*(\\<[A-Z0-9_]+\\>).*$");
        Matcher ctrlMatcher = ctrlPattern.matcher(original);
        while (ctrlMatcher.matches()) {
            String ctrlCode = ctrlMatcher.group(1);
            int charCode = MTCharmap.encode(ctrlCode);
            original = original.replaceFirst(ctrlCode, String.format("[0x%08X]", charCode));
            ctrlMatcher = ctrlPattern.matcher(original);
        }
        // then, we break down to array of single human-readable hex codes,
        // at last, convert all to integer values upon character map
        StringBuilder sb = new StringBuilder(original);
        Pattern hexPattern = Pattern.compile("^\\[0[xX][0-9a-fA-F]{8}\\].*$");
        ArrayList<Integer> charCodes = new ArrayList<>();
        while (sb.length() > 0) {
            Matcher hexMatcher = hexPattern.matcher(sb);
            if (hexMatcher.matches()) {
                String hexCode = sb.substring(3, 11);
                charCodes.add(Integer.parseInt(hexCode, 16));
                sb.delete(0, 12);
            } else {
                charCodes.add(MTCharmap.encode(String.valueOf(sb.charAt(0))));
                sb.deleteCharAt(0);
            }
        }
        // convert to array of integers
        return charCodes;
    }
    
    public static String decodeMessage(DataStream ds) {
        StringBuilder sb = new StringBuilder();
        int charCode;
        boolean lineRemain = true;
        int endCode = MTCharmap.getEndCode();
        while (lineRemain) {
            charCode = ds.getInt();
            if (charCode == endCode) {
                lineRemain = false;
            } else {
                String decode = MTCharmap.decode(charCode);
                sb.append(decode == null ?
                        String.format("[0x%08X]", charCode) :
                        decode);
            }
        }
        return sb.toString();
    }
}
