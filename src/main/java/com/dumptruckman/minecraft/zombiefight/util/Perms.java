/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.pluginbase.permission.Perm;
import com.dumptruckman.minecraft.pluginbase.permission.Perm.Builder;
import org.bukkit.permissions.PermissionDefault;

public class Perms {
    
    private static final String BASE_PERM = "zombiefight.";

    public static final Perm CMD_PGSPAWN = new Perm.Builder(BASE_PERM + "cmd.pgspawn").commandPermission().build();
    public static final Perm CMD_PGSPAWN_SET = new Builder(BASE_PERM + "cmd.pgspwan.set").parent(CMD_PGSPAWN).build();

    public static final Perm CMD_BORDER = new Perm.Builder(BASE_PERM + "cmd.border").commandPermission().build();

    public static final Perm CMD_GSPAWN = new Perm.Builder(BASE_PERM + "cmd.gspawn").commandPermission().build();
    public static final Perm CMD_GSPAWN_SET = new Builder(BASE_PERM + "cmd.gspwan.set").parent(CMD_GSPAWN).build();

    public static final Perm CMD_START = new Perm.Builder(BASE_PERM + "cmd.start").commandPermission().build();

    public static final Perm CMD_END = new Perm.Builder(BASE_PERM + "cmd.end").commandPermission().build();

    public static final Perm CMD_ENABLE = new Perm.Builder(BASE_PERM + "cmd.enable").commandPermission().build();
    public static final Perm CMD_DISABLE = new Perm.Builder(BASE_PERM + "cmd.disable").commandPermission().build();
    public static final Perm CMD_SELECT = new Perm.Builder(BASE_PERM + "cmd.select").commandPermission().build();
    public static final Perm CMD_DESELECT = new Perm.Builder(BASE_PERM + "cmd.deselect").commandPermission().build();

    public static final Perm CMD_CLEANUP = new Perm.Builder(BASE_PERM + "cmd.cleanup").commandPermission().build();

    public static final Perm ALL_KITS = new Builder(BASE_PERM + "kit.*").addToAll().def(PermissionDefault.FALSE).build();
    public static final Perm KIT = new Builder(BASE_PERM + "kit").commandPermission().parent(ALL_KITS).build();

    public static final Perm CAN_ALWAYS_BREAK = new Builder(BASE_PERM + "alwaysbreak").addToAll().build();
}
