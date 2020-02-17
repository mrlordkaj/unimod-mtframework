/*
 * Copyright (C) 2020 Thinh
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
import com.openitvn.unicore.message.IMessage;
import com.openitvn.unicore.message.IMessageEntry;
import com.openitvn.util.StringHelper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 *
 * @author Thinh
 */
public class MTMessage extends IMessage {
    
    private static final int MAGIC_MSG2 = StringHelper.makeFourCC("MSG2");
    
    private final byte[] unk1 = new byte[24];
    private short numLines;
    private short unk2;
    private final byte[] unk3 = new byte[32];
    
    @Override
    public void decode(DataStream ds) {
        if (ds.getInt() == MAGIC_MSG2) {
            // read header
            ds.get(unk1);
            numLines = ds.getShort();
            unk2 = ds.getShort();
            ds.get(unk3);
            // read lines
            for (short i = 0; i < numLines; i++) {
                addEntry(MTMessageCoder.decodeMessage(ds));
            }
        } else {
            throw new UnsupportedOperationException("Invalid MTF Message file format");
        }
    }
    
    @Override
    public byte[] encode() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ByteBuffer bb = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
            // write header
            bb.putInt(MAGIC_MSG2);
            bb.put(unk1);
            bb.putShort(numLines);
            bb.putShort(unk2);
            bb.put(unk3);
            bos.write(bb.array());
            // write compiled translation data
            int endCode = MTCharmap.getEndCode();
            bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            for (IMessageEntry e : entries) {
                // write line
                ArrayList<Integer> charCodes = MTMessageCoder.encodeMessage(e.getMessageForCompile());
                for (int charCode : charCodes) {
                    bb.putInt(0, charCode);
                    bos.write(bb.array());
                }
                // write end code
                bb.putInt(0, endCode);
                bos.write(bb.array());
            }
            return bos.toByteArray();
        } catch (IOException ex) {
            throw new UnsupportedOperationException(ex.getMessage());
        }
    }

    @Override
    public InputStream openCharmap() throws IOException {
        // open external charmap.tbl from default directory if exist
        // or else open from internal
        File file = new File(MTCharmap.EXTERNAL_CHARMAP);
        return file.exists() ?
                new FileInputStream(file) :
                getClass().getResourceAsStream(MTCharmap.INTERNAL_CHARMAP);
    }
    
    @Override
    public String[] getEscapePatterns() {
        return new String[] {
            "\\[0[xX][0-9A-F]+\\]",
            "<[A-Z0-9_]+>",
            "\\(dummy\\)"
        };
    }
}
