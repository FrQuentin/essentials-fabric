package fr.quentin.essentials.toast;

import com.google.common.collect.ImmutableList;
import fr.quentin.essentials.Essentials;
import fr.quentin.essentials.utils.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MarketRestockToast implements Toast {
    private static final Identifier TEXTURE = Identifier.of(Essentials.MOD_ID, "toast/market_restock");
    private static final int LINE_HEIGHT = 12;
    private static final int TEXT_X = 26;
    private static final int TITLE_Y = 7;
    private static final int DESCRIPTION_Y_OFFSET = (TITLE_Y * 3) - 3;
    private final Type type;
    private Text title;
    private List<OrderedText> lines;
    private long startTime;
    private boolean justUpdated;
    private final int width;
    private final boolean hidden;
    private Toast.Visibility visibility = Toast.Visibility.HIDE;

    public MarketRestockToast(Type type, Text title, @Nullable Text description, boolean hidden) {
        this(
                type,
                title,
                getTextAsList(description),
                Math.max(
                        160,
                        30 + Math.max(
                                Constants.client.textRenderer.getWidth(title), description == null ? 0 : Constants.client.textRenderer.getWidth(description)
                        )
                ), hidden
        );
    }

    private MarketRestockToast(Type type, Text title, List<OrderedText> lines, int width, boolean hidden) {
        this.type = type;
        this.title = title;
        this.lines = lines;
        this.width = width;
        this.hidden = hidden;
    }

    private static ImmutableList<OrderedText> getTextAsList(@Nullable Text text) {
        return text == null ? ImmutableList.of() : ImmutableList.of(text.asOrderedText());
    }

    @Override
    public int getWidth() {
        return this.width + 4;
    }

    @Override
    public int getHeight() {
        return 21 + Math.max(this.lines.size(), 1) * LINE_HEIGHT;
    }

    @Override
    public Toast.Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager manager, long time) {
        if (this.justUpdated) {
            this.startTime = time;
            this.justUpdated = false;
        }

        double d = this.type.displayDuration * manager.getNotificationDisplayTimeMultiplier();
        long l = time - this.startTime;
        this.visibility = !this.hidden && l < d ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        context.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURE, 0, 0, this.getWidth(), this.getHeight());

        if (this.lines.isEmpty()) {
            context.drawText(textRenderer, this.title, TEXT_X, TITLE_Y, Colors.WHITE, false);
        } else {
            context.drawText(textRenderer, this.title, TEXT_X, TITLE_Y, Colors.WHITE, false);

            for (int i = 0; i < this.lines.size(); i++) {
                context.drawText(textRenderer, this.lines.get(i), TEXT_X, DESCRIPTION_Y_OFFSET + i * LINE_HEIGHT, Colors.LIGHT_GRAY, false);
            }
        }
    }

    public void setContent(Text title, @Nullable Text description) {
        this.title = title;
        this.lines = getTextAsList(description);
        this.justUpdated = true;
    }

    public Type getType() {
        return this.type;
    }

    public static void add(ToastManager manager, Type type, Text title, @Nullable Text description) {
        manager.add(new MarketRestockToast(type, title, description, false));
    }

    public static void show(ToastManager manager, Type type, Text title, @Nullable Text description) {
        MarketRestockToast systemToast = manager.getToast(MarketRestockToast.class, type);
        if (systemToast == null) {
            add(manager, type, title, description);
        } else {
            systemToast.setContent(title, description);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Type {
        public static final Type MARKET_RESTOCK = new Type(4500L);
        final long displayDuration;

        public Type(long displayDuration) {
            this.displayDuration = displayDuration;
        }
    }
}