package com.kleinercode.fabric.zoneprotector;

import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.filefix.FileFix;
import net.minecraft.util.filefix.operations.FileFixOperations;

public class ZoneStorageFileFix extends FileFix {

    public ZoneStorageFileFix(Schema schema) {
        super(schema);
    }

    @Override
    public void makeFixer() {

        addFileFixOperation(FileFixOperations.move("data/zones.dat", "data/zoneprotector/zones.dat"));

    }

}
