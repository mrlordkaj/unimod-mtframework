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

import com.openitvn.unicore.Unicore;
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
public class MTCharmap {
    public static final String INTERNAL_CHARMAP = "/com/openitvn/unicore/plugin/msg/charmap.tbl";
    public static final String EXTERNAL_CHARMAP = Unicore.workDir + "/MTFramework/MessageViewer/charmap.tbl";
    
    private int undefinedCode = 0x00440016; //use character "?" for undefined chars
    private int endCode = 0x04010000; //termine sentencies
    
    private static MTCharmap instance;
    public static MTCharmap getInstance() {
        if (instance == null)
            instance = new MTCharmap();
        return instance;
    }
    
    private ArrayList<MTChar> charMap = new ArrayList<>();
    
    private MTCharmap() {
        loadCharmapFile();
    }
    
    public final void loadCharmapFile() {
        charMap.clear();
        Pattern pattern = Pattern.compile("^\\[0[xX]([0-9a-fA-F]{8})\\]=(.*)$");
        try {
            File file = new File(EXTERNAL_CHARMAP);
            InputStream is = file.exists() ? new FileInputStream(file) : getClass().getResourceAsStream(INTERNAL_CHARMAP);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int charCode = Integer.parseInt(matcher.group(1), 16);
                    String decode = matcher.group(2);
                    if ("?".equals(decode))
                        undefinedCode = charCode;
                    if ("<end>".equals(decode))
                        endCode = charCode;
                    charMap.add(new MTChar(charCode, decode));
                }
            }
        } catch (IOException ex) {
            charMap = null;
        }
    }
    
    public int getEndCode() {
        return endCode;
    }
    
    public int encode(String character) {
        for (MTChar charEntry : charMap)
            if(charEntry.decode().equals(character)) return charEntry.charCode();
        return undefinedCode;
    }
    
    public String decode(int charCode) {
        for (MTChar charEntry : charMap)
            if(charEntry.charCode() == charCode) return charEntry.decode();
        return null;
    }
}
