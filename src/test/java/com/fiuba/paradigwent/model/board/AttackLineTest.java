package com.fiuba.paradigwent.model.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.ModelTestFixtures;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

class AttackLineTest {

    /**
     *  Test: An attack line starts empty and without a horn.
    **/
    @Test
    void startsEmptyWithoutHorn() {
        AttackLine line = new AttackLine(AttackLineType.MELEE);

        assertEquals(0, line.calculateTotalPower());
        assertFalse(line.hasHorn());
        assertNull(line.getStrongestMinion());
    }

    /**
     *  Test: An attack line calculates its total power based on the current power of its minions.
    **/
    @Test
    void calculatesPowerFromCurrentMinionPower() {
        AttackLine line = new AttackLine(AttackLineType.MELEE);
        Minion first = ModelTestFixtures.minion("first", 5, AttackLineType.MELEE, null);
        Minion second = ModelTestFixtures.minion("second", 3, AttackLineType.MELEE, null);
        line.addMinion(first);
        line.addMinion(second);

        first.modifyPower(-2);

        assertEquals(6, line.calculateTotalPower());
    }

    @Test
    void weatheredLineUsesOnePowerPerMinion() {
        AttackLine line = new AttackLine(AttackLineType.MELEE);
        line.addMinion(ModelTestFixtures.minion("first", 5, AttackLineType.MELEE, null));
        line.addMinion(ModelTestFixtures.minion("second", 3, AttackLineType.MELEE, null));

        line.setWeathered(true);

        assertEquals(2, line.calculateTotalPower());
        assertTrue(line.isWeathered());
        line.setWeathered(false);
        assertEquals(8, line.calculateTotalPower());
    }

    /**
     *  Test: An attack line can be activated with a horn, which doubles its total power, and can be deactivated.
    **/
    @Test
    void hornDoublesTheWholeLineAndCanBeRemoved() {
        AttackLine line = new AttackLine(AttackLineType.RANGED);
        line.addMinion(ModelTestFixtures.minion("one", 4, AttackLineType.RANGED, null));
        line.activateHorn();

        assertTrue(line.hasHorn());
        assertEquals(8, line.calculateTotalPower());

        line.deActivateHorn();

        assertFalse(line.hasHorn());
        assertEquals(4, line.calculateTotalPower());
    }

    /**
     *  Test: Removing minions from an attack line updates its total power and strongest minion correctly.
    **/
    @Test
    void removingAndTakingMinionsUpdatesTheLine() {
        AttackLine line = new AttackLine(AttackLineType.SIEGE);
        Minion first = ModelTestFixtures.minion("first", 4, AttackLineType.SIEGE, null);
        Minion second = ModelTestFixtures.minion("second", 7, AttackLineType.SIEGE, null);
        line.addMinion(first);
        line.addMinion(second);
        line.removeMinion(first);

        assertEquals(7, line.calculateTotalPower());
        assertEquals(1, line.takeMinions().size());
        assertEquals(0, line.calculateTotalPower());
    }

    /**
     *  Test: The strongest minion in an attack line is determined by its current power, not its base power.
    **/
    @Test
    void strongestMinionUsesCurrentPower() {
        AttackLine line = new AttackLine(AttackLineType.MELEE);
        Minion first = ModelTestFixtures.minion("first", 10, AttackLineType.MELEE, null);
        Minion second = ModelTestFixtures.minion("second", 4, AttackLineType.MELEE, null);
        second.modifyPower(10);
        line.addMinion(first);
        line.addMinion(second);

        assertEquals(second, line.getStrongestMinion());
    }
}
