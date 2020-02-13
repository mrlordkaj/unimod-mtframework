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


import com.openitvn.format.arc.MTArchive;
import com.openitvn.format.mod.MTMod;
import com.openitvn.format.mrl.MTMaterialLibrary;
import com.openitvn.format.tex.MTTexturePack;
import com.openitvn.unicore.plugin.FileType;
import com.openitvn.unicore.plugin.PanelLocation;
import com.openitvn.unicore.plugin.PluginManifest;
import com.openitvn.unicore.plugin.msg.MessageEditor;
import com.unimod.plugin.panel.TestPanel;

/**
 *
 * @author Thinh Pham
 */
public final class PackageManifest extends PluginManifest {
    
    public PackageManifest() {
        // TODO: define your supported file extensions here
        putFileView("MTF Archive",  loadIcon("/_arc.png"),  FileType.Archive,   MTArchive.class,            "arc");
        putFileView("MTF Texture",  loadIcon("/_tex.png"),  FileType.Texture,   MTTexturePack.class,        "tex", "rtex");
        putFileView("MTF Model",    loadIcon("/_mod.png"),  FileType.World,     MTMod.class,                "mod");
        putFileView("MTF Message",  loadIcon("/_msg.png"),  FileType.Custom,    MessageEditor.class,        "msg", "msg2");
        putFileView("MTF Material", null,                   FileType.Material,  MTMaterialLibrary.class,    "mrl");
        
        // TODO: define your custom control panel here
        putControlPanel("Test", PanelLocation.Sidebar, TestPanel.class, true);
    }

    @Override
    public String getId() {
        // TODO: define package identity here
        return "com.unimod.mtframework";
    }

    @Override
    public String getName() {
        // TODO: define package name here
        return "Capcom MTF Pack";
    }

    @Override
    public String getVersion() {
        // TODO: define package version here
        return "1.0.0";
    }
}
