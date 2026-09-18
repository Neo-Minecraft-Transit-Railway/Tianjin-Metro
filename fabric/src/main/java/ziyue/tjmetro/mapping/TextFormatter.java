package ziyue.tjmetro.mapping;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import ziyue.tjmetro.mod.config.ConfigClient;

import java.net.URI;
import java.util.function.Function;

/**
 * @since 1.0.0-beta-5
 */
public interface TextFormatter
{
	Function<ConfigClient.Footer, MutableComponent> FOOTER_LINK = footer -> footer.text().get().data.setStyle(
			Style.EMPTY.withColor(ChatFormatting.BLUE).withUnderlined(true).withClickEvent(new ClickEvent.OpenUrl(URI.create(footer.link())))
	);
}
