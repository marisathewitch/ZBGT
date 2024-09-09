package com.zorbatron.zbgt.api.metatileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import com.zorbatron.zbgt.ZBGTConfig;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;

public abstract class LaserCapableMultiMapMultiblockController extends MultiMapMultiblockController {

    private final boolean allowSubstationHatches;

    public LaserCapableMultiMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        this(metaTileEntityId, recipeMaps, true);
    }

    public LaserCapableMultiMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps,
                                                    boolean allowSubstationHatches) {
        super(metaTileEntityId, recipeMaps);
        this.allowSubstationHatches = allowSubstationHatches;
    }

    public boolean allowsSubstationHatches() {
        return this.allowSubstationHatches && ZBGTConfig.multiblockSettings.allowSubstationHatches;
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();

        List<IEnergyContainer> list = new ArrayList<>();
        list.addAll(getAbilities(MultiblockAbility.INPUT_ENERGY));
        list.addAll(getAbilities(MultiblockAbility.INPUT_LASER));
        if (allowsSubstationHatches()) {
            list.addAll(getAbilities(MultiblockAbility.SUBSTATION_INPUT_ENERGY));
        }

        this.energyContainer = new EnergyContainerList(Collections.unmodifiableList(list));
    }

    @Override
    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn,
                                               boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut,
                                               boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(false, checkMaintenance, checkItemIn, checkItemOut,
                checkFluidIn, checkFluidOut, checkMuffler);

        if (checkEnergyIn) {
            predicate = predicate.or(autoEnergyInputs());
        }

        return predicate;
    }

    public TraceabilityPredicate autoEnergyInputs(int min, int max, int previewCount) {
        if (allowsSubstationHatches()) {
            return new TraceabilityPredicate(abilities(MultiblockAbility.INPUT_ENERGY, MultiblockAbility.INPUT_LASER,
                    MultiblockAbility.SUBSTATION_INPUT_ENERGY)
                            .setMinGlobalLimited(min).setMaxGlobalLimited(max).setPreviewCount(previewCount));
        } else {
            return new TraceabilityPredicate(abilities(MultiblockAbility.INPUT_ENERGY, MultiblockAbility.INPUT_LASER)
                    .setMinGlobalLimited(min).setMaxGlobalLimited(max).setPreviewCount(previewCount));
        }
    }

    public TraceabilityPredicate autoEnergyInputs(int min, int max) {
        return autoEnergyInputs(min, max, 2);
    }

    public TraceabilityPredicate autoEnergyInputs() {
        return autoEnergyInputs(1, 3);
    }
}