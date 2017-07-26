/*
 * Created on 28-Apr-2004
 * Created by Paul Gardner
 * Copyright (C) Azureus Software, Inc, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.gudy.azureus2.pluginsimpl.update;

/**
 * @author parg
 *
 */

import java.util.*;
import java.util.zip.*;
import java.net.URL;
import java.io.*;

import org.gudy.azureus2.core3.html.HTMLUtils;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.util.*;

import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.plugins.installer.InstallablePlugin;
import org.gudy.azureus2.plugins.installer.PluginInstallationListener;
import org.gudy.azureus2.plugins.installer.PluginInstaller;
import org.gudy.azureus2.plugins.installer.StandardPlugin;
import org.gudy.azureus2.plugins.logging.*;
import org.gudy.azureus2.plugins.update.*;
import org.gudy.azureus2.plugins.utils.resourcedownloader.*;
import org.gudy.azureus2.plugins.ui.*;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.plugins.ui.model.*;
import org.gudy.azureus2.pluginsimpl.*;
import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
import org.gudy.azureus2.pluginsimpl.update.sf.*;
import org.gudy.azureus2.update.CorePatchChecker;

import com.aelitis.azureus.core.versioncheck.VersionCheckClient;

public class
PluginUpdatePlugin
        implements Plugin
{
    public void example(){
        long start = SystemTime.getMonotonousTime();
        while( true ) {
            long now = SystemTime.getMonotonousTime();
            if (now - start >= 5 * 60 * 1000) {
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (Throwable e) {
                Debug.out(e);
                return;
            }
        }
    }
}
