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

import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.data.DataStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Thinh Pham
 */
abstract class MTMessageCoder {
    
    static ArrayList<Integer> encodeMessage(String original) {
        // at first, convert all control code to human-readable hex
        Pattern ctrlPattern = Pattern.compile("^.*(<[A-Z0-9]+>).*$");
        Matcher ctrlMatcher = ctrlPattern.matcher(original);
        while (ctrlMatcher.matches()) {
            String ctrlCode = ctrlMatcher.group(1);
            int charCode = encodeChar(ctrlCode);
            original = original.replaceFirst(ctrlCode, String.format("[0x%08X]", charCode));
            ctrlMatcher = ctrlPattern.matcher(original);
        }
        // then, break down to array of single human-readable hex codes,
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
                charCodes.add(encodeChar(String.valueOf(sb.charAt(0))));
                sb.deleteCharAt(0);
            }
        }
        // convert to array of integers
        return charCodes;
    }
    
    static String decodeMessage(DataStream ds) {
        StringBuilder sb = new StringBuilder();
        int charCode;
        boolean lineRemain = true;
        while (lineRemain) {
            charCode = ds.getInt();
            if (charCode == endCode) {
                lineRemain = false;
            } else {
                String decode = decodeChar(charCode);
                sb.append(decode == null ?
                        String.format("[0x%08X]", charCode) :
                        decode);
            }
        }
        return sb.toString();
    }
    
    static final String INTERNAL_CHARMAP = "/com/openitvn/format/msg/charmap.chr";
    static final String EXTERNAL_CHARMAP = Unicore.getWorkDir("/MTFramework/charmap.chr");
    
    private static final ArrayList<MTChar> CHARACTER_MAP = new ArrayList();
    private static int unkCode = 0x00440016; // use character "?" for undefined chars
    private static int endCode = 0x04010000; // termine sentencies
    
    static {
        Pattern pattern = Pattern.compile("^\\[0[xX]([0-9a-fA-F]{8})\\]=(.*)$");
        File file = new File(EXTERNAL_CHARMAP);
        try (InputStream is = file.exists() ?
                new FileInputStream(file) :
                MTMessageCoder.class.getResourceAsStream(INTERNAL_CHARMAP);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int charCode = Integer.parseInt(matcher.group(1), 16);
                    String decode = matcher.group(2);
                    if ("?".equals(decode))
                        unkCode = charCode;
                    if ("<END>".equals(decode))
                        endCode = charCode;
                    CHARACTER_MAP.add(new MTChar(charCode, decode));
                }
            }
        } catch (IOException ex) { }
    }
    
    public static int encodeChar(String character) {
        for (MTChar c : CHARACTER_MAP) {
            if (c.decode.equals(character))
                return c.charCode;
        }
        return unkCode;
    }
    
    public static String decodeChar(int charCode) {
        for (MTChar c : CHARACTER_MAP) {
            if (c.charCode == charCode)
                return c.decode;
        }
        return null;
    }
    
    public static int getEndCode() {
        return endCode;
    }
}
