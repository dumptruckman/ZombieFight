package com.dumptruckman.minecraft.zombiefight.util;

import com.dumptruckman.minecraft.pluginbase.permission.Perm;

public class Perms {
    
    private static final String BASE_PERM = "zombiefight.";

    public static final Perm CMD_CHECK = new Perm.Builder(BASE_PERM + "cmd.check").commandPermission().build();
}
