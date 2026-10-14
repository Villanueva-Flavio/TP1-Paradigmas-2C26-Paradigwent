package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.PlayerSide;
import com.fiuba.paradigwent.model.card.Minion;
import com.fiuba.paradigwent.model.enums.AttackLineType;

import java.util.Collections;

/* incompleto targetType */
public class DestroyCreatureEffect implements Effect{
    @Override
    public boolean requiresTarget() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Destroys a minion";
    }

    @Override
    public String getTargetDescription() {
        return targetType == AttackLineType.NONE
            ? "Objective: A minion"
            : "Objective: A minion from  " + targetType + " line";
    }
    private final AttackLineType targetType;

    /**
     * Constructs a DestroyCreatureEffect with the specified target type. If the provided targetType is null, it defaults to AttackLineType.NONE.
     * @param targetType 
    **/
    public DestroyCreatureEffect(AttackLineType targetType){
        this.targetType = targetType;
    }

    @Override 
    public void apply(GameContext context){
        Minion target = (Minion) context.getTargetCard();
        if(target == null){
            target = context.getBoard().getStrongestMiniondOf(
                context.getBoard().getOpponentSide());
        }
        if (target != null) {
            for (PlayerSide side : new PlayerSide[] {
                    context.getBoard().getPlayerSide(),
                    context.getBoard().getOpponentSide() }) {
                for (AttackLineType lineType : AttackLineType.values()) {
                    if (lineType != AttackLineType.NONE
                            && side.getAttackLine(lineType).getMinions().contains(target)) {
                        side.getAttackLine(lineType).removeMinion(target);
                        side.getOwner().sendToDiscard(Collections.singletonList(target));
                        return;
                    }
                }
            }
        }
    }
}
