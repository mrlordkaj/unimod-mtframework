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
package com.openitvn.format.arc;

import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class MTArchiveExtension {
    
    private static final HashMap<Integer, String> HASH_MAP = new HashMap();
    
    static {
        HASH_MAP.put(0x241f5deb,"tex");   // texture
        HASH_MAP.put(0x7808ea10,"rtex");  // abstract texture
        HASH_MAP.put(0x58a15856,"mod");   // model
        HASH_MAP.put(0x2749c8a8,"mrl");   // material library
        HASH_MAP.put(0x10c460e6,"msg");   // messages
        HASH_MAP.put(0x0253f147,"hit");   // model hit
        HASH_MAP.put(0x358012e8,"vib");   // vibration
        HASH_MAP.put(0x2447d742,"xfs");   // id/jpn
        HASH_MAP.put(0x2282360d,"xfs");
        HASH_MAP.put(0x60dd1b16,"xfs");
        HASH_MAP.put(0x671f21da,"xfs");
        HASH_MAP.put(0x557ecc08,"xfs");   // aef
        HASH_MAP.put(0x176c3f95,"xfs");   // stage src
        HASH_MAP.put(0x15302ef4,"xfs");   // stage src
        HASH_MAP.put(0x19a59a91,"xfs");   // lightLnk
        HASH_MAP.put(0x266e8a91,"xfs");
        HASH_MAP.put(0x65b275e5,"xfs");   // fsm
        HASH_MAP.put(0x66b45610,"xfs");   // fsm
        HASH_MAP.put(0x4d894d5d,"xfs");   // light env
        HASH_MAP.put(0x017a550d,"xfs");   // scr00
        HASH_MAP.put(0x2c4666d1,"xfs");   // scr00
        HASH_MAP.put(0x1efb1b67,"xfs");   // em
        HASH_MAP.put(0x4ca26828,"xfs");   // soundeffect/em
        HASH_MAP.put(0x758b2eb7,"xfs");   // cef/wp
        HASH_MAP.put(0x76820d81,"lmt");   // fig, figure?
        HASH_MAP.put(0x4c0db839,"sdl");
        HASH_MAP.put(0x4e397417,"ean");
        HASH_MAP.put(0x6d5ae854,"efl");   // core effect
        HASH_MAP.put(0x167dbbff,"strq");  // background music
        HASH_MAP.put(0x0ecd7df4,"scst");  // sound effect
        HASH_MAP.put(0x232e228c,"rev");   // sound effect
        HASH_MAP.put(0x02833703,"efs");
        HASH_MAP.put(0x51fc779f,"sbc1");
        HASH_MAP.put(0x7e33a16c,"spac");  // sound effect
        HASH_MAP.put(0x1bcc4966,"sreq");  // sound effect
        HASH_MAP.put(0x3e363245,"chn");   // pawn
        HASH_MAP.put(0x276de8b7,"e2d");   // rtt
        HASH_MAP.put(0x36e29465,"havok");
        HASH_MAP.put(0x5f36b659,"way");
        HASH_MAP.put(0x39c52040,"lcm");
        HASH_MAP.put(0x0dadab62,"obja");
    }
    
    public static String getString(int hash) {
        return HASH_MAP.containsKey(hash) ?
                HASH_MAP.get(hash) : String.format("0x%08x", hash);
    }
}
