package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.board.AttackLine;
import com.fiuba.paradigwent.model.board.PlayerSide;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.effect.Effect;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Minion extends Card{

    private int powerModifier;
    private List<Effect> effects = new ArrayList<>();

    /**
     * Constructor for the Minion class, which represents a minion card in the game. It initializes the minion with its unique identifier, name, stack limit, base power, and attack line type.
     * @param id
     * @param name
     * @param stackLimite
     * @param basePower
     * @param attackLineType
     * @param description
    **/
    public Minion(String id, String name,int stackLimite, int basePower, AttackLineType attackLineType, String description){
        super(id, name,stackLimite, attackLineType, description);
        this.basePower = basePower;
        this.powerModifier = 0;
    }

    /**
     * @return The current power of the minion, which is the sum of its base power and any modifiers that have been applied to it.
    **/
    public int getCurrentPower() {
        return basePower + powerModifier;
    }
    /**
     * Returns the value provided by getDisplayedPower.
     * @return the value produced by the method
     */
    @Override
    public int getDisplayedPower() {
        return getCurrentPower();
    }
    /**
     * Executes the operation represented by asMinion.
     * @return the value produced by the method
     */
    @Override
    public Optional<Minion> asMinion() {
        return Optional.of(this);
    }

    /**
     * Modifies the power of the minion by a specified amount. This method adjusts the power modifier, which affects the minion's current power.
     * @param modifier The amount to modify the minion's power by (can be positive or negative).
    **/
    public void modifyPower(int modifier) {
        this.powerModifier += modifier;
    }
    /**
     * Executes the operation represented by play.
     * @param context the value used by the method
     */
    @Override
    public void play(GameContext context) {
        PlayerSide side = context.getBoard().getSideFor(context.getCurrentPlayer());
        AttackLine line = side.getAttackLine(this.getAttackLineType());
        line.addMinion(this);
        for (Effect effect : effects) {
            effect.apply(context);
        }

    }
    /**
     * Returns the value provided by getEffects.
     * @return the value produced by the method
     */
    public List<Effect> getEffects() {
        return new ArrayList<>(effects);
    }
    /**
     * Executes the operation represented by requiresTarget.
     * @return the value produced by the method
     */
    @Override
    public boolean requiresTarget() {
        return effects.stream()
            .anyMatch(effect -> !effect.isLineTargetEffect()
                && effect.requiresTarget());
    }
    /**
     * Returns the value provided by getTargetDescription.
     * @return the value produced by the method
     */
    @Override
    public String getTargetDescription() {
        return effects.stream()
            .map(Effect::getTargetDescription)
            .filter(description -> !description.isBlank())
            .distinct()
            .reduce((left, right) -> left + " | " + right)
            .orElse("");
    }
    /**
     * Reports the state represented by hasOptionalTarget.
     * @return the value produced by the method
     */
    @Override
    public boolean hasOptionalTarget() {
        return effects.stream().anyMatch(Effect::isOptionalTargetEffect);
    }
}
