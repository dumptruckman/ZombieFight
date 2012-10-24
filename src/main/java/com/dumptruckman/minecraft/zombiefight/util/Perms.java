/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.pluginbase.permission.Perm;
import com.dumptruckman.minecraft.pluginbase.permission.PermDefault;
import com.dumptruckman.minecraft.pluginbase.permission.PermFactory;

public class Perms {
    
    private static final String BASE_PERM = "zombiefight.";

    public static final Perm CMD_PGSPAWN = PermFactory.newPerm(BASE_PERM + "cmd.pgspawn").commandPermission().build();
    public static final Perm CMD_PGSPAWN_SET = PermFactory.newPerm(BASE_PERM + "cmd.pgspwan.set").parent(CMD_PGSPAWN).build();

    public static final Perm CMD_BORDER = PermFactory.newPerm(BASE_PERM + "cmd.border").commandPermission().build();

    public static final Perm CMD_GSPAWN = PermFactory.newPerm(BASE_PERM + "cmd.gspawn").commandPermission().build();
    public static final Perm CMD_GSPAWN_SET = PermFactory.newPerm(BASE_PERM + "cmd.gspwan.set").parent(CMD_GSPAWN).build();

    public static final Perm CMD_START = PermFactory.newPerm(BASE_PERM + "cmd.start").commandPermission().build();

    public static final Perm CMD_END = PermFactory.newPerm(BASE_PERM + "cmd.end").commandPermission().build();

    public static final Perm CMD_ENABLE = PermFactory.newPerm(BASE_PERM + "cmd.enable").commandPermission().build();
    public static final Perm CMD_DISABLE = PermFactory.newPerm(BASE_PERM + "cmd.disable").commandPermission().build();
    public static final Perm CMD_SELECT = PermFactory.newPerm(BASE_PERM + "cmd.select").commandPermission().build();
    public static final Perm CMD_DESELECT = PermFactory.newPerm(BASE_PERM + "cmd.deselect").commandPermission().build();

    public static final Perm CMD_CLEANUP = PermFactory.newPerm(BASE_PERM + "cmd.cleanup").commandPermission().build();

    public static final Perm ALL_KITS = PermFactory.newPerm(BASE_PERM + "kit.*").addToAll().def(PermDefault.FALSE).build();
    public static final Perm KIT = PermFactory.newPerm(BASE_PERM + "kit").commandPermission().parent(ALL_KITS).build();

    public static final Perm CAN_ALWAYS_BREAK = PermFactory.newPerm(BASE_PERM + "alwaysbreak").addToAll().build();
}
