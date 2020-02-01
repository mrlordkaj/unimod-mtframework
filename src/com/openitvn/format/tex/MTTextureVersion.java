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
package com.openitvn.format.tex;

/**
 *
 * @author Thinh Pham
 */
public enum MTTextureVersion {
    RE5  ("Resident Evil 5",             (short)0x0070),
    RE6  ("Resident Evil 6",             (short)0x009A),
    RE6B ("Resident Evil 6",             (short)0x009B),
    RER  ("Resident Evil Revelations",   (short)0xA09D),
    RER2 ("Resident Evil Revelations 2", (short)0x209D),
    ;
    
    private final String name;
    private final short value;
    private final short version;
    private final short build;
    
    private MTTextureVersion(String name, short value) {
        this.name = name;
        this.value = value;
        this.version = (short)(value & 0xff);
        this.build = (short)(value >> 8 & 0xff);
    }
    
    public short getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return String.format("%1$s (%2$03d.%3$03d)", name, version, build);
    }
    
    public static MTTextureVersion fromValue(short value) {
        for (MTTextureVersion ver : MTTextureVersion.values()) {
            if (ver.value == value)
                return ver;
        }
        return null;
    }
}
