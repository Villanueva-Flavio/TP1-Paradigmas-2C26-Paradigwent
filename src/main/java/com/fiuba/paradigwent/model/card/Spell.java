package com.fiuba.paradigwent.model.card;

import com.fiuba.paradigwent.model.GameContext;
import com.fiuba.paradigwent.model.enums.AttackLineType;
import com.fiuba.paradigwent.model.effect.Effect;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Spell extends Card {
    private List<Effect> effects = new ArrayList<>();

    /**
     * Creates a new Spell card with the specified parameters.
     * @param id
     * @param name
     * @param stackLimite
     * @param attackLineType
    **/
    public Spell(String id, String name, int stackLimite, AttackLineType attackLineType, String description) {
        super(id, name, stackLimite, attackLineType, description);
    }

    @Override
    public void play(GameContext context) {
        for (Effect effect : effects) {
            effect.apply(context);
        }
    }

    public List<Effect> getEffects() {
        return new ArrayList<>(effects);
    }

    @Override
    public boolean requiresTarget() {
        return effects.stream().anyMatch(Effect::requiresTarget);
    }

    @Override
    public String getEffectDescription() {
        return effects.stream()
            .map(Effect::getDescription)
            .collect(Collectors.joining(", "));
    }

    @Override
    public String getTargetDescription() {
        return effects.stream()
            .map(Effect::getTargetDescription)
            .filter(description -> !description.isBlank())
            .distinct()
            .collect(Collectors.joining(" | "));
    }
}
