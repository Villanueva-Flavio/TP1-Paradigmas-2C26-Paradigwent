package com.fiuba.paradigwent.model.targeting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fiuba.paradigwent.model.ModelTestFixtures;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.card.Spell;
import com.fiuba.paradigwent.model.effect.DestroyCreatureEffect;
import com.fiuba.paradigwent.model.effect.DuplicateLineEffect;
import com.fiuba.paradigwent.model.effect.ModifyPowerEffect;
import com.fiuba.paradigwent.model.effect.StealCardEffect;
import com.fiuba.paradigwent.model.enums.AttackLineType;

/**
 * Cards tell a {@link TargetSelector} what they need instead of being asked
 * which effects they carry.
 */
class TargetingTest {

    @Test
    void destroyAsksForAMandatoryEnemyMinion() {
        RecordingSelector selector = new RecordingSelector();

        new DestroyCreatureEffect(AttackLineType.NONE).targeting().request(selector);

        assertEquals("minion", selector.requested);
        assertFalse(selector.optional);
        assertEquals(TargetSide.ENEMY, selector.side);
    }

    @Test
    void buffAsksForAnOptionalAllyMinion() {
        RecordingSelector selector = new RecordingSelector();

        new ModifyPowerEffect(2).targeting().request(selector);

        assertEquals("minion", selector.requested);
        assertTrue(selector.optional);
        assertEquals(TargetSide.ALLY, selector.side);
    }

    @Test
    void hornWithoutLineAsksForALineAndWithLineAsksForNothing() {
        RecordingSelector free = new RecordingSelector();
        RecordingSelector fixed = new RecordingSelector();

        new DuplicateLineEffect(AttackLineType.NONE).targeting().request(free);
        new DuplicateLineEffect(AttackLineType.SIEGE).targeting().request(fixed);

        assertEquals("line", free.requested);
        assertEquals("nothing", fixed.requested);
    }

    @Test
    void cardRequestsTheFirstEffectThatNeedsATarget() {
        Spell spell = new Spell("spell", "Spell", 1, AttackLineType.NONE, null,
            List.of(new StealCardEffect(1), new DestroyCreatureEffect(AttackLineType.NONE)));
        RecordingSelector selector = new RecordingSelector();

        spell.targeting().request(selector);

        assertEquals("minion", selector.requested);
    }

    @Test
    void cardWithoutTargetedEffectsAsksForNothing() {
        RecordingSelector selector = new RecordingSelector();

        ModelTestFixtures.minion("plain", 3, AttackLineType.MELEE, null).targeting().request(selector);

        assertEquals("nothing", selector.requested);
    }

    @Test
    void mandatoryMinionTargetValidatesTheChoice() {
        Targeting targeting = new MinionTarget("prompt", false, TargetSide.ENEMY);
        Minion onBoard = ModelTestFixtures.minion("on-board", 3, AttackLineType.MELEE, null);
        Minion elsewhere = ModelTestFixtures.minion("elsewhere", 3, AttackLineType.MELEE, null);

        assertTrue(targeting.isSatisfiedBy(onBoard, null, List.of(onBoard)));
        assertFalse(targeting.isSatisfiedBy(elsewhere, null, List.of(onBoard)));
        assertFalse(targeting.isSatisfiedBy(null, null, List.of(onBoard)));
        assertTrue(targeting.isSatisfiedBy(null, null, List.of()));
    }

    @Test
    void lineTargetNeedsARealLine() {
        Targeting targeting = new LineTarget("prompt");

        assertTrue(targeting.isSatisfiedBy(null, AttackLineType.RANGED, List.of()));
        assertFalse(targeting.isSatisfiedBy(null, AttackLineType.NONE, List.of()));
        assertFalse(targeting.isSatisfiedBy(null, null, List.of()));
    }

    private static final class RecordingSelector implements TargetSelector {
        private String requested;
        private boolean optional;
        private TargetSide side;

        @Override
        public void selectNothing() {
            requested = "nothing";
        }

        @Override
        public void selectMinion(String prompt, boolean optional, TargetSide preferred) {
            requested = "minion";
            this.optional = optional;
            this.side = preferred;
        }

        @Override
        public void selectLine(String prompt) {
            requested = "line";
        }
    }
}
