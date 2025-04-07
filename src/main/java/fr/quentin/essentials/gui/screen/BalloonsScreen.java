package fr.quentin.essentials.gui.screen;

import fr.quentin.essentials.Essentials;
import fr.quentin.essentials.gui.widget.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BalloonsScreen extends Screen {
    private static final Identifier TEXTURE = Identifier.of(Essentials.MOD_ID, "textures/gui/container/balloons.png");
    private int x = 0;
    private int y = 0;
    private static final int backgroundWidth = 176;
    private static final int backgroundHeight = 124;

    // Variable for Back button
    private static final int backButtonWidth = 16;
    private static final int backButtonHeight = 14;
    // Variable for Red Balloons button
    private static final int redBalloonsButtonWidth = 79;
    private static final int redBalloonsButtonHeight = 34;
    // Variable for Yellow Balloons button
    private static final int yellowBalloonsButtonWidth = 79;
    private static final int yellowBalloonsButtonHeight = 34;
    // Variable for Blue Balloons button
    private static final int blueBalloonsButtonWidth = 79;
    private static final int blueBalloonsButtonHeight = 34;
    // Variable for Green Balloons button
    private static final int greenBalloonsButtonWidth = 79;
    private static final int greenBalloonsButtonHeight = 34;

    public BalloonsScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        this.x = (this.width - backgroundWidth) / 2;
        this.y = (this.height - backgroundHeight) / 2;

        // Code for Back button
        int backButtonX = this.x + 8;
        int backButtonY = this.y + 9;
        BackWidget backWidget = new BackWidget(backButtonX, backButtonY, backButtonWidth, backButtonHeight);
        this.addDrawableChild(backWidget);
        // Code for Red Balloons button
        int redBalloonsButtonX = this.x + 8;
        int redBalloonsButtonY = this.y + 30;
        RedBalloonsWidget redBalloonsWidget = new RedBalloonsWidget(redBalloonsButtonX, redBalloonsButtonY, redBalloonsButtonWidth, redBalloonsButtonHeight);
        this.addDrawableChild(redBalloonsWidget);
        // Code for Yellow Balloons button
        int yellowBalloonsButtonX = this.x + 89;
        int yellowBalloonsButtonY = this.y + 30;
        YellowBalloonsWidget yellowBalloonsWidget = new YellowBalloonsWidget(yellowBalloonsButtonX, yellowBalloonsButtonY, yellowBalloonsButtonWidth, yellowBalloonsButtonHeight);
        this.addDrawableChild(yellowBalloonsWidget);
        // Code for Blue Balloons button
        int blueBalloonsButtonX = this.x + 8;
        int blueBalloonsButtonY = this.y + 66;
        BlueBalloonsWidget blueBalloonsWidget = new BlueBalloonsWidget(blueBalloonsButtonX, blueBalloonsButtonY, blueBalloonsButtonWidth, blueBalloonsButtonHeight);
        this.addDrawableChild(blueBalloonsWidget);
        // Code for Green Balloons button
        int greenBalloonsButtonX = this.x + 89;
        int greenBalloonsButtonY = this.y + 66;
        GreenBalloonsWidget greenBalloonsWidget = new GreenBalloonsWidget(greenBalloonsButtonX, greenBalloonsButtonY, greenBalloonsButtonWidth, greenBalloonsButtonHeight);
        this.addDrawableChild(greenBalloonsWidget);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderDarkening(context);
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, this.x, this.y, 0.0F, 0.0F, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
