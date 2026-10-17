package com.fiuba.paradigwent.model.effect;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.card.Minion;

public class ModifyPowerEffect implements Effect {
    /**
     * Executes the operation represented by requiresTarget.
     * @return the value produced by the method
     */
    @Override
    public boolean requiresTarget() {
        return true;
    }
    /**
     * Reports the state represented by isOptionalTargetEffect.
     * @return the value produced by the method
     */
    @Override
    public boolean isOptionalTargetEffect() {
        return true;
    }
    /**
     * Reports the state represented by isModifyPowerEffect.
     * @return the value produced by the method
     */
    @Override
    public boolean isModifyPowerEffect() {
        return true;
    }
    /**
     * Returns the value provided by getDescription.
     * @return the value produced by the method
     */
    @Override
    public String getDescription() {
        return "Modify the power of a minion";
    }
    /**
     * Returns the value provided by getTargetDescription.
     * @return the value produced by the method
     */
    @Override
    public String getTargetDescription() {
        return "Objective: A minion from any line";
    }
    private final int amount;

    /**
     * Constructs a ModifyPowerEffect with the specified amount to modify the power of a target minion.
     * @param amount
    **/
    public ModifyPowerEffect(int amount) {
        this.amount = amount;
    }
    /**
     * Executes the operation represented by apply.
     * @param context the value used by the method
     */
    @Override
    public void apply(GameContext context){
        if (context.getTargetCard() != null) {
            context.getTargetCard().asMinion().ifPresent(target -> target.modifyPower(amount));
        }
    }
}
