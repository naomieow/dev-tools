package xyz.naomieow.devtools.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.naomieow.devtools.DevToolsMod;
import xyz.naomieow.devtools.widgets.FolderButtonWidget;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At(value="TAIL"), method = "initWidgets")
    private void createWorldFolderButton(CallbackInfo ci) {
        assert this.client != null;
        if (this.client.isIntegratedServerRunning()) {
            GridWidget gridWidget = new GridWidget();
            gridWidget.getMainPositioner().margin(4, 4, 0, 0);
            GridWidget.Adder adder = gridWidget.createAdder(1);

            DevToolsMod.LOGGER.info("Created!");
            DevToolsMod.LOGGER.info(adder.getGridWidget().getMainPositioner().toString());

            adder.add(new FolderButtonWidget(0, 0, (button) -> {
                String dir = this.client.getLevelStorage().getSavesDirectory().toString();
            }).setButton(0));
            adder.add(new FolderButtonWidget(0, 0, (button) -> {
                String dir = this.client.getLevelStorage().getSavesDirectory().toString();

            }).setButton(1));

            gridWidget.refreshPositions();
            SimplePositioningWidget.setPos(gridWidget, 114, 16, this.width, this.height, 0.5F, 0.25F);
            gridWidget.forEachChild(this::addDrawableChild);
        }
    }
}
