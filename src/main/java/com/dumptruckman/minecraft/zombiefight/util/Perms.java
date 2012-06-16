package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.pluginbase.permission.Perm;
import com.dumptruckman.minecraft.pluginbase.permission.Perm.Builder;

public class Perms {
    
    private static final String BASE_PERM = "zombiefight.";

    public static final Perm CMD_PGSPAWN = new Perm.Builder(BASE_PERM + "cmd.pgspawn").commandPermission().build();
    public static final Perm CMD_PGSPAWN_SET = new Builder(BASE_PERM + "cmd.pgspwan.set").parent(CMD_PGSPAWN).build();

    public static final Perm CMD_GSPAWN = new Perm.Builder(BASE_PERM + "cmd.gspawn").commandPermission().build();
    public static final Perm CMD_GSPAWN_SET = new Builder(BASE_PERM + "cmd.gspwan.set").parent(CMD_GSPAWN).build();

    public static final Perm CMD_START = new Perm.Builder(BASE_PERM + "cmd.start").commandPermission().build();

    public static final Perm CMD_END = new Perm.Builder(BASE_PERM + "cmd.end").commandPermission().build();

    public static final Perm CMD_ENABLE = new Perm.Builder(BASE_PERM + "cmd.enable").commandPermission().build();
    public static final Perm CMD_DISABLE = new Perm.Builder(BASE_PERM + "cmd.disable").commandPermission().build();

    public static final Perm CAN_ALWAYS_BREAK = new Builder(BASE_PERM + "alwaysbreak").build();
}
