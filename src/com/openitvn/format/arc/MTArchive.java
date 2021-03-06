/*
 * Copyright (C) 2016 Thinh Pham
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

import com.openitvn.unicore.archive.IArchive;
import com.openitvn.unicore.archive.ICompression;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.data.FileStream;
import com.openitvn.helper.StringHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 * https://github.com/mrlordkaj/unimod-mtframework/wiki/MTF-Archive
 */
public class MTArchive extends IArchive<MTArchiveEntry> {
    
    private final int ARC_MAGIC = StringHelper.makeFourCC("ARC\0");
    private final int HEADER_SIZE = 8;
    private final int ENTRY_SIZE = 80;
    
    private short version;
    
    public MTArchive() {
        compression = ICompression.Deflate;
    }
    
    @Override
    protected void parse(File in) throws IOException {
        try (FileStream fs = new FileStream(in)) {
            if (fs.getInt() == ARC_MAGIC) {
                // read header
                version = fs.getShort();
                int numEntries = fs.getShort();
                // parse entries
                for (int i = 0; i < numEntries; i++) {
                    String name = fs.readFixedString(64);
                    int ext = fs.getInt();
                    int packed = fs.getInt();
                    int sizeFlag = fs.getInt();
                    int size = sizeFlag & 0x00ffffff;
                    byte flag = (byte)(sizeFlag >> 24);
                    int offset = fs.getInt();
                    entries.add(new MTArchiveEntry(this, name, ext, packed, size, flag, offset));
                }
            } else {
                throw new IOException("Invalid MTF Archive format");
            }
        }
    }
    
    @Override
    public void repack(File out) throws IOException {
        try (FileInputStream fis = new FileInputStream(getFile());
                FileOutputStream fos = new FileOutputStream(out)) {
            // preserve header size
            short numEntries = (short) entries.size();
            ByteBuffer bb = ByteBuffer.allocate(HEADER_SIZE + ENTRY_SIZE * numEntries);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putInt(ARC_MAGIC);
            bb.putShort(version);
            bb.putShort(numEntries);
            fos.write(bb.array());
            // write entries
            for (int i = 0; i < numEntries; i++) {
                MTArchiveEntry e = entries.get(i);
                // set new offset for entry as current pointer of file writer
                int newOffset = (int) fos.getChannel().position();
                // begin write entry content
                if (e.getPacked() >= 0) {
                    // if the entry has not been replaced, just copy packed data from source archive
                    fis.getChannel().position(e.getOffset());
                    DataStream.copy(fis, fos, e.getPacked());
                } else {
                    // else, perform packing and write to archive
                    e.pack(fos, newOffset);
                }
                // comeback to fix entry header
                long mark = fos.getChannel().position();
                fos.getChannel().position(HEADER_SIZE + ENTRY_SIZE * i);
                bb = ByteBuffer.allocate(ENTRY_SIZE).order(ByteOrder.LITTLE_ENDIAN);
                String entryName = e.getPathWithoutExt();
                bb.put(entryName.getBytes());
                bb.position(bb.position() + 64 - entryName.length()); // fill 64 bytes
                bb.putInt(e.getHash());
                bb.putInt((int)e.getPacked());
                int sizeAndFlag = e.getFlags() << 24 | (int)e.getSize();
                bb.putInt(sizeAndFlag);
                bb.putInt(newOffset);
                fos.write(bb.array());
                // return mark for write next entry
                fos.getChannel().position(mark);
            }
        }
    }
    
//    @Override
//    public void refresh() throws IOException {
//        try (FileInputStream fs = new FileInputStream(getFile())) {
//            for (int i = 0; i < entries.size(); i++) {
//                fs.getChannel().position(HEADER_SIZE + ENTRY_SIZE * i + 76);
//                ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
//                fs.read(bb.array());
//                long newOffset = bb.getInt();
//                entries.get(i).setOffset(newOffset);
//            }
//        }
//    }
}