package mod.ynovka.autoLiteFish;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleEventExampleClient implements ClientModInitializer {
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private boolean isCatched = false;

	@Override
	public void onInitializeClient() {
		// Register a callback for the event. every time the event fires, it runs the method that you register
		TitleMessageEvents.SET_TITLE.register((text, isSubTitle) -> {
			// if the title being set is not a subtitle, and the player exists, add the title to the chat hud
			if (!isSubTitle && MinecraftClient.getInstance().player != null) {

				// get bar content
				String Content;
				if (text.toString().length() > 115 + 88) {
					Content = text.toString().substring(115, text.toString().length() - 88);
				}
				else {
					Content = text.toString();
				}

				// Регулярное выражение для поиска всех значений color
				String regex = "color=#([A-Fa-f0-9]{6})";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(Content);

				// Используем HashSet для хранения уникальных значений color
				HashSet<String> colors = new HashSet<>();

				// Проходим по всем совпадениям и добавляем их в HashSet
				while (matcher.find()) {
					colors.add(matcher.group(1));
				}

				// Если в миниигре 2 цвета, значит нужно нажать ПКМ
				if (colors.size() == 2) {
					System.out.println("catch! using rod.");
					UseRod();
					if (!isCatched) {
						isCatched = true;
					}
				}
				else if (isCatched && colors.size() != 3) {
					System.out.println("game finished, wait 300 millis to use rod.");
					scheduler.schedule(this::UseRod, 300, TimeUnit.MILLISECONDS);
				}
				else {
					System.out.println("Not Game!");
				}
			}
		});
	}

	private void UseRod() {
		Hand hand = Hand.MAIN_HAND;
		assert MinecraftClient.getInstance().interactionManager != null;
		ActionResult actionResult = MinecraftClient.getInstance().interactionManager.interactItem(MinecraftClient.getInstance().player, hand);
		if (actionResult.isAccepted()) {
			if (actionResult.shouldSwingHand()) {
				MinecraftClient.getInstance().player.swingHand(hand);
			}
			MinecraftClient.getInstance().gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
		}
	}
}
