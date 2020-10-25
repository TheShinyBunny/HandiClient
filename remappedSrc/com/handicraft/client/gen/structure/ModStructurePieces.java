/*
 * Copyright (c) 2020. Yaniv - TheShinyBunny
 */

package com.handicraft.client.gen.structure;

import net.minecraft.structure.StructurePieceType;

public class ModStructurePieces {

    public static final StructurePieceType DARK_TEMPLE_PIECE_TYPE = DarkTempleStructure.Generator::new;
    public static final StructurePieceType DARK_FORTRESS_BRIDGE_CROSSING = StructurePieceType.register(DarkFortressGenerator.BridgeCrossing::new, "DfBCr");
    public static final StructurePieceType DARK_FORTRESS_BRIDGE_END = StructurePieceType.register(DarkFortressGenerator.BridgeEnd::new, "DfBEF");
    public static final StructurePieceType DARK_FORTRESS_BRIDGE = StructurePieceType.register(DarkFortressGenerator.Bridge::new, "DfBS");
    public static final StructurePieceType DARK_FORTRESS_CORRIDOR_STAIRS = StructurePieceType.register(DarkFortressGenerator.CorridorStairs::new, "DfCCS");
    public static final StructurePieceType DARK_FORTRESS_CORRIDOR_BALCONY = StructurePieceType.register(DarkFortressGenerator.CorridorBalcony::new, "DfCTB");
    public static final StructurePieceType DARK_FORTRESS_CORRIDOR_EXIT = StructurePieceType.register(DarkFortressGenerator.CorridorExit::new, "DfCE");
    public static final StructurePieceType DARK_FORTRESS_CORRIDOR_CROSSING = StructurePieceType.register(DarkFortressGenerator.CorridorCrossing::new, "DfSCSC");
    public static final StructurePieceType DARK_FORTRESS_CORRIDOR_LEFT_TURN = StructurePieceType.register(DarkFortressGenerator.CorridorLeftTurn::new, "DfSCLT");
    public static final StructurePieceType DARK_FORTRESS_SMALL_CORRIDOR = StructurePieceType.register(DarkFortressGenerator.SmallCorridor::new, "DfSC");
    public static final StructurePieceType DARK_FORTRESS_CORRIDOR_RIGHT_TURN = StructurePieceType.register(DarkFortressGenerator.CorridorRightTurn::new, "DfSCRT");
    public static final StructurePieceType DARK_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM = StructurePieceType.register(DarkFortressGenerator.CorridorNetherWartsRoom::new, "DfCSR");
    public static final StructurePieceType DARK_FORTRESS_BRIDGE_PLATFORM = StructurePieceType.register(DarkFortressGenerator.BridgePlatform::new, "DfMT");
    public static final StructurePieceType DARK_FORTRESS_BRIDGE_SMALL_CROSSING = StructurePieceType.register(DarkFortressGenerator.BridgeSmallCrossing::new, "DfRC");
    public static final StructurePieceType DARK_FORTRESS_BRIDGE_STAIRS = StructurePieceType.register(DarkFortressGenerator.BridgeStairs::new, "DfSR");
    public static final StructurePieceType DARK_FORTRESS_START = StructurePieceType.register(DarkFortressGenerator.Start::new, "DfStart");
    
}
