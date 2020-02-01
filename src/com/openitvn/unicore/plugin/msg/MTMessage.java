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
import com.openitvn.util.StringHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham <mrlordkaj@gmail.com>
 */
public class MTMessage {
    private static final int MAGIC_MSG2 = StringHelper.makeFourCC('M','S','G','2');
    
    private byte[] unk1;
    private short lineCount;
    private short unk2;
    private byte[] unk3;
    
    private final MessageTableModel model;
    
    public MTMessage(MessageTableModel model) {
        this.model = model;
    }
    
    public void readData(DataStream ds) throws UnsupportedOperationException {
        ds.rewind();
        if (ds.getInt() != MAGIC_MSG2)
            throw new UnsupportedOperationException("Invalid MT Framework Message file format.");
        unk1 = ds.get(new byte[24]);
        lineCount = ds.getShort();
        unk2 = ds.getShort();
        unk3 = ds.get(new byte[32]);
        for (short i = 0; i < lineCount; i++) {
            String decode = MTMessageConverter.decodeMessage(ds);
            MessageTableEntry newEntry = new MessageTableEntry(i, decode, "");
            model.addEntry(newEntry);
        }
    }
    
    public byte[] toData() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteBuffer bb = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        //write header
        bos.write(bb.putInt(0, MAGIC_MSG2).array(), 0, 4);
        bos.write(unk1);
        bos.write(bb.putShort(0, lineCount).array(), 0, 2);
        bos.write(bb.putShort(0, unk2).array(), 0, 2);
        bos.write(unk3);
        //write compiled translation data
        int endCode = MTCharmap.getInstance().getEndCode();
        for (MessageTableEntry entry : model.entries()) {
            //write message
            Integer[] encode = MTMessageConverter.encodeMessage(entry.getMessageForCompile());
            for (int charCode : encode)
                bos.write(bb.putInt(0, charCode).array(), 0, 4);
            //write end code
            bos.write(bb.putInt(0, endCode).array(), 0, 4);
        }
        bos.close();
        return bos.toByteArray();
    }
}
