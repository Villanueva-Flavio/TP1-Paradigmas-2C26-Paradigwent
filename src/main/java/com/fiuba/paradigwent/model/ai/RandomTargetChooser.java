package com.fiuba.paradigwent.model.ai;

import java.util.List;
import java.util.Random;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.targeting.TargetSelector;
import com.fiuba.paradigwent.model.targeting.TargetSide;

/**
 * Chooses targets at random for the AI. Minions are taken from the side the
 * effect is meant for (allies for buffs, enemies for removal); a mandatory
 * target falls back to any minion of the board.
 */
class RandomTargetChooser implements TargetSelector {
    private static final List<AttackLineType> PLAYABLE_LINES =
        List.of(AttackLineType.MELEE, AttackLineType.RANGED, AttackLineType.SIEGE);

    private final GameContext context;
    private final Random random;
    private Minion chosenMinion;
    private AttackLineType chosenLine;

    RandomTargetChooser(GameContext context, Random random) {
        this.context = context;
        this.random = random;
    }

    @Override
    public void selectNothing() {
        chosenMinion = null;
        chosenLine = null;
    }

    @Override
    public void selectMinion(String prompt, boolean optional, TargetSide preferred) {
        List<Minion> candidates = context.minionsOn(preferred);
        if (candidates.isEmpty() && !optional) {
            candidates = context.minionsOnBoard();
        }
        chosenMinion = candidates.isEmpty() ? null : candidates.get(random.nextInt(candidates.size()));
    }

    @Override
    public void selectLine(String prompt) {
        chosenLine = PLAYABLE_LINES.get(random.nextInt(PLAYABLE_LINES.size()));
    }

    Minion chosenMinion() {
        return chosenMinion;
    }

    AttackLineType chosenLine() {
        return chosenLine;
    }
}
