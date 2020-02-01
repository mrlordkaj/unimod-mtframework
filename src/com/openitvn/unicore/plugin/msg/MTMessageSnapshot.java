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

import com.openitvn.unicore.data.BufferStream;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.util.StringHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */

/* MTMessageSave file specifications
struct MTMessageSaveHeader {
    int32 magic; // SAV\0
    int16 version;
    int32 originalFileSize;
    int32 messageCount;
} //total 14 bytes

byte[] originalFileData; //just copy orignal file data

foreach(messageCount) {
    short flags;
    byte[] messageData; //line data
}
*/
public class MTMessageSnapshot {
    private final int SAV_MAGIC = StringHelper.makeFourCC('S','A','V','\0');
    private static final byte FLAG_HIDDEN    = 0b0001;
    private static final byte FLAG_SKIP      = 0b0010;
    private static final byte FLAG_APPROVED  = 0b0100;
    private static final byte FLAG_UNUSED    = 0b1000;
    
    private final MessageTableModel model;
    private MTMessage message;
    
    private short version;
    private int originalFileSize;
    private int messageCount;
    private DataStream originalData;
    
    public MTMessageSnapshot(MessageTableModel model) {
        this.model = model;
    }
    
    public void setOriginalData(DataStream ds) {
        originalData = ds;
        originalFileSize = (int)ds.capacity();
        message = new MTMessage(model);
        message.readData(ds);
        messageCount = model.getRowCount();
    }
    
    public void readData(DataStream ds) throws UnsupportedOperationException {
        try {
            ds.rewind();
            if (ds.getInt() != SAV_MAGIC)
                throw new UnsupportedOperationException("Invalid message save file.");

            version = ds.getShort();
            originalFileSize = ds.getInt();
            messageCount = ds.getInt();

            //read original
            originalData = new BufferStream(ds.get(new byte[originalFileSize]));
            message = new MTMessage(model);
            message.readData(originalData);

            //read translation messages
            if (messageCount == model.entries().length) {
                for (MessageTableEntry entry : model.entries()) {
                    short flags = ds.getShort();
                    entry.setHidden((flags & FLAG_HIDDEN) != 0);
                    entry.setSkip((flags & FLAG_SKIP) != 0);
                    entry.setApproved((flags & FLAG_APPROVED) != 0);
                    String decode = MTMessageConverter.decodeMessage(ds);
                    entry.setTranslation(decode);
                }
            }
        } catch (BufferUnderflowException | IndexOutOfBoundsException ex) {
            throw new UnsupportedOperationException(ex.getMessage());
        }
    }
    
    public byte[] compile() throws IOException {
        return message.toData();
    }
    
    public byte[] toData() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteBuffer bb = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        //write magic
        bos.write(bb.putInt(0, SAV_MAGIC).array(), 0, 4);
        //write version
        bos.write(new byte[] { 1, 0 });
        //write original file size
        bos.write(bb.putInt(0, (int)originalData.capacity()).array(), 0, 4);
        //write messageCount
        bos.write(bb.putInt(0, model.entries().length).array(), 0, 4);
        //write originalData
        originalData.rewind();
        byte[] org = originalData.get(new byte[originalFileSize]);
        bos.write(org);
        //write translation data
        int endCode = MTCharmap.getInstance().getEndCode();
        for (MessageTableEntry entry : model.entries()) {
            //write flags byte
            byte flags = 0;
            if (entry.isHidden())
                flags |= FLAG_HIDDEN;
            if (entry.isSkip())
                flags |= FLAG_SKIP;
            if (entry.isApproved())
                flags |= FLAG_APPROVED;
            //write flags and pad
            bos.write(new byte[] { flags, 0 });
            //write message
            Integer[] encode = MTMessageConverter.encodeMessage(entry.getTranslation());
            for(int charCode : encode)
                bos.write(bb.putInt(0, charCode).array(), 0, 4);
            //write end code
            bos.write(bb.putInt(0, endCode).array(), 0, 4);
        }
        bos.close();
        return bos.toByteArray();
    }
}
