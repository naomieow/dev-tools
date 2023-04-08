package xyz.naomieow.devtools.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import xyz.naomieow.devtools.DevToolsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.world.WorldListWidget.WorldEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(WorldEntry.class)
public abstract class WorldEntryMixin extends WorldListWidget.Entry {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private LevelSummary level;

    @Shadow @Final private WorldListWidget field_19135;

    @Inject(at = @At("HEAD"), method = "render")
    private void addFolderButton(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if ((Boolean) this.client.options.getTouchscreen().getValue() || hovered) {
            RenderSystem.setShaderTexture(0, new Identifier("devtools:textures/gui/world_folder.png"));
            int i = mouseX - x;
            boolean bl = i > 234;
            int j = bl ? 32 : 0;
            if (Screen.hasShiftDown()) {
                DrawableHelper.drawTexture(matrices, x + 229, y, 32.0F, (float) j, 64, 32, 128, 128);
            } else {
                DrawableHelper.drawTexture(matrices, x + 198, y, 96.0F, (float) j, 64, 32, 128, 128);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked", cancellable = true)
    private void clickFolderButton(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.level.isUnavailable()) {
            cir.setReturnValue(true);
        } else {
            this.field_19135.setSelected((WorldListWidget.Entry)this);
            if (mouseX - (double)this.field_19135.getRowLeft() >= 234.0F) {
                String string = this.level.getName();
                try {
                    LevelStorage.Session session = this.client.getLevelStorage().createSession(string);
                    if (Screen.hasShiftDown()) {
                        Util.getOperatingSystem().open(session.getDirectory(WorldSavePath.DATAPACKS).toFile());
                    } else {
                        Util.getOperatingSystem().open(session.getDirectory(WorldSavePath.ROOT).toFile());
                    }
                    try {
                        session.close();
                    } catch (IOException e) {
                        DevToolsMod.LOGGER.error("Failed to unlock level {}", string, e);
                    }
                } catch (IOException e) {
                    SystemToast.addWorldAccessFailureToast(this.client, string);
                    DevToolsMod.LOGGER.error("Failed to access level {}", string, e);
                }
            }
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/WorldListWidget$WorldEntry;play()V"), method = "mouseClicked")
    private void saveWorldLocation(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        String string = this.level.getName();
        try {
            LevelStorage.Session session = this.client.getLevelStorage().createSession(string);
            DevToolsMod.worldSaveLocation = session.getDirectory(WorldSavePath.ROOT);
            DevToolsMod.worldDatapackLocation = session.getDirectory(WorldSavePath.DATAPACKS);
            try {
                session.close();
            } catch (IOException e) {
                DevToolsMod.LOGGER.error("Failed to unlock level {}", string, e);
            }
        } catch (IOException e) {
            SystemToast.addWorldAccessFailureToast(this.client, string);
            DevToolsMod.LOGGER.error("Failed to access level {}", string, e);
        }
    }
}
