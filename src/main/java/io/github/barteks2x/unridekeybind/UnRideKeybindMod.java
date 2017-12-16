/*
 * This file is part of Genesis Mod, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017 Boethie
 * Copyright (c) 2017 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.barteks2x.unridekeybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;

@Mod(modid = UnRideKeybindMod.MODID, version = UnRideKeybindMod.VERSION)
public class UnRideKeybindMod {

    public static final String MODID = "unridekeybind";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Keybinds.INSTANCE.init();
    }

    public static abstract class Keybinds {

        @SuppressWarnings("NullableProblems") @SidedProxy @Nonnull
        public static Keybinds INSTANCE;

        public abstract void init();

        public abstract boolean dismount();

        @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
        public static class ClientProxy extends Keybinds {

            private KeyBinding dismount;

            private boolean dismounting = false;

            @SubscribeEvent
            public static void onEvent(InputEvent.KeyInputEvent event) {
                ((ClientProxy)INSTANCE).onKey();
            }

            private void onKey() {
                this.dismounting = dismount.isKeyDown();
            }

            @Override public void init() {
                dismount = new KeyBinding("key.unridekeybind.desc", Keyboard.KEY_R, "key.unridekeybind.category");
                ClientRegistry.registerKeyBinding(dismount);
            }

            @Override public boolean dismount() {
                return dismounting;
            }
        }

        public static class ServerProxy extends Keybinds {

            @Override public void init() {
                // no-op
            }

            @Override public boolean dismount() {
                return false;
            }
        }
    }
}
