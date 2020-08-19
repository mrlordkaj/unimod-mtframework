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
package net.unimod.mtframework;

import com.openitvn.format.arc.MTArchive;
import com.openitvn.format.arc.MTArchiveEntry;
import com.openitvn.helper.FileHelper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Thinh
 */
final class REStageEntity {
    
    final ArrayList<MTArchiveEntry> ints = new ArrayList<>();
    final ArrayList<File> exts = new ArrayList<>();
    final MTArchive arc;

    REStageEntity(String root, File arcFile) throws IOException {
        arc = new MTArchive();
        arc.open(arcFile);
        // search internal model files
        String regex = "^stage\\\\s\\d+\\\\scr\\\\scr\\d+\\\\[\\w\\d_]+\\.mod$";
        for (MTArchiveEntry e : arc.entries) {
            // find scene mod by path template
            String path = e.getPath();
            if (path.matches(regex)) {
                // check override external files
                File ext = new File(root + "/" + path);
                if (ext.exists()) {
                    exts.add(ext);
                } else {
                    ints.add(e);
                }
            }
        }
    }
    
    public int getInternalCount() {
        return ints.size();
    }
    
    public int getExternalCount() {
        return exts.size();
    }
    
    @Override
    public String toString() {
        String file = arc.getFile().getName();
        return FileHelper.cropFileExt(file);
    }
}
