package xyz.naomieow.devtools.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FolderButtonWidget extends ButtonWidget {
    private int type;

    public FolderButtonWidget(int x, int y, PressAction onPress) {
        super(x, y, 20, 20, Text.literal("Open folder."), onPress, DEFAULT_NARRATION_SUPPLIER);
    }
    static enum IconLocation {
        DATAPACK(20, 0),
        WORLD(0, 0),
        DATAPACK_ACTIVE(20, 20),
        WORLD_ACTIVE(0, 20);

        private final int u;
        private final int v;
        private IconLocation(int u, int v) {
            this.u = u;
            this.v = v;
        }

        public int getU() { return this.u; }
        public int getV() { return this.v; }
    }
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, new Identifier("devtools:textures/gui/world_buttons.png"));
        IconLocation iconLocation;
        if (this.isMouseOver(mouseX, mouseY)) {
            iconLocation = this.type == 0 ? IconLocation.WORLD_ACTIVE : IconLocation.DATAPACK_ACTIVE;
        } else {
            iconLocation = this.type == 0 ? IconLocation.WORLD : IconLocation.DATAPACK;
        }
        drawTexture(matrices, this.getX(), this.getY(), iconLocation.getU(), iconLocation.getV(), this.width, this.height, 40, 40);
    }

    public FolderButtonWidget setButton(Integer num) {
        this.type = num;
        return this;
    }
}

