package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.pluginbase.permission.Perm;
import com.dumptruckman.minecraft.pluginbase.permission.Perm.Builder;

public class Perms {
    
    private static final String BASE_PERM = "zombiefight.";

    public static final Perm CMD_PGSPAWN = new Perm.Builder(BASE_PERM + "cmd.pgspawn").commandPermission().build();
    public static final Perm CMD_PGSPAWN_SET = new Builder(BASE_PERM + "cmd.pgspwan.set").parent(CMD_PGSPAWN).build();
}
