package net.femboypig.mmcf.client;

import net.fabricmc.api.ClientModInitializer;

public final class MakeMyCrystalsFasterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // The optimization itself is applied by MultiPlayerGameModeMixin.
    }
}
