package com.fiuba.paradigwent.view.game.modules;

public enum CardRace {
    DWARF("card-dwarf.png"),
    HIGHELF("card-highelf.png"),
    HUMAN("card-human.png"),
    MURGOL("card-murgol.png"),
    ORC("card-orc.png"),
    UNDEAD("card-undead.png");

    private final String assetName;

    CardRace(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetName() {
        return assetName;
    }
}
