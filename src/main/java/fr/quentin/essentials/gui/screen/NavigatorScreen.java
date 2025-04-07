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
public class NavigatorScreen extends Screen {
    private static final Identifier TEXTURE = Identifier.of(Essentials.MOD_ID, "textures/gui/container/navigator.png");
    private int x = 0;
    private int y = 0;
    private static final int backgroundWidth = 176;
    private static final int backgroundHeight = 124;

    // Variable for Realms button
    private static final int realmsButtonWidth = 70;
    private static final int realmsButtonHeight = 52;
    // Variable for Open World button
    private static final int openWorldButtonWidth = 70;
    private static final int openWorldButtonHeight = 52;
    // Variable for Spawn button
    private static final int spawnButtonWidth = 88;
    private static final int spawnButtonHeight = 34;
    // Variable for Homes button
    private static final int homesButtonWidth = 88;
    private static final int homesButtonHeight = 34;
    // Variable for Towns button
    private static final int townsButtonWidth = 88;
    private static final int townsButtonHeight = 34;

    public NavigatorScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        this.x = (this.width - backgroundWidth) / 2;
        this.y = (this.height - backgroundHeight) / 2;

        // Code for Realms button
        int realmsButtonX = this.x + 8;
        int realmsButtonY = this.y + 9;
        RealmsWidget realmsWidget = new RealmsWidget(realmsButtonX, realmsButtonY, realmsButtonWidth, realmsButtonHeight);
        this.addDrawableChild(realmsWidget);

        // Code for Open World button
        int openWorldButtonX = this.x + 8;
        int openWorldButtonY = this.y + 63;
        OpenWorldWidget openWorldWidget = new OpenWorldWidget(openWorldButtonX, openWorldButtonY, openWorldButtonWidth, openWorldButtonHeight);
        this.addDrawableChild(openWorldWidget);

        // Code for Spawn World button
        int spawnButtonX = this.x + 80;
        int spawnButtonY = this.y + 9;
        SpawnWidget spawnWidget = new SpawnWidget(spawnButtonX, spawnButtonY, spawnButtonWidth, spawnButtonHeight);
        this.addDrawableChild(spawnWidget);

        // Code for Homes World button
        int homesButtonX = this.x + 80;
        int homesButtonY = this.y + 45;
        HomesWidget homesWidget = new HomesWidget(homesButtonX, homesButtonY, homesButtonWidth, homesButtonHeight);
        this.addDrawableChild(homesWidget);

        // Code for Towns World button
        int townsButtonX = this.x + 80;
        int townsButtonY = this.y + 81;
        TownsWidget townsWidget = new TownsWidget(townsButtonX, townsButtonY, townsButtonWidth, townsButtonHeight);
        this.addDrawableChild(townsWidget);
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