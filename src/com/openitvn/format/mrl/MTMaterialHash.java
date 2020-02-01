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
package com.openitvn.format.mrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Thinh Pham
 */
public abstract class MTMaterialHash {
    
    private static final String INT_MAP = "/com/openitvn/format/mrl/hashmap.dat";
    private static final String EXT_MAP = "dev/MTF/mrl/hashmap.dat";
    
    private static HashMap<Integer, String> nameCache;
    
    private static void initHashCache() {
        nameCache = new HashMap<>();
        File dev = new File(EXT_MAP);
        try (InputStream is = dev.exists() ? new FileInputStream(dev) : MTMaterialHash.class.getResourceAsStream(INT_MAP);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr)) {
            Pattern pattern = Pattern.compile("^0[xX]([0-9a-fA-F]{8})\\s*=\\s*([a-zA-Z0-9_\\-\\.]+)$");
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int hash = (int) (long) Long.parseLong(matcher.group(1), 16);
                    String name = matcher.group(2).trim();
                    nameCache.put(hash, name);
                }
            }
        } catch (IOException ex) { }
        
//        // pl
//        nameCache.put(0x804e2cb2, "XfB_N__E0__m23_"); // b2 2c 4e 80
//        nameCache.put(0xb73758b5, "XfB_N__E0__m51_"); // b5 58 37 b7
    }
    
    public static void cleanup() {
        if (new File(EXT_MAP).exists())
            nameCache = null;
    }
    
    public static String getName(int hash) {
        if (nameCache == null)
            initHashCache();
        return nameCache.get(hash);
    }
    
    public static Integer getHash(String name) {
        if (nameCache == null)
            initHashCache();
        for (Entry<Integer, String> e : nameCache.entrySet()) {
            if (e.getValue().equals(name))
                return e.getKey();
        }
        return null;
    }
}
