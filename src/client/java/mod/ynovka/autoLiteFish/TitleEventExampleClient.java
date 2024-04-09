package mod.ynovka.autoLiteFish;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class TitleEventExampleClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Register a callback for the event. every time the event fires, it runs the method that you register
		TitleMessageEvents.SET_TITLE.register((text, isSubTitle) -> {
			// if the title being set is not a subtitle, and the player exists, add the title to the chat hud
			if (!isSubTitle && MinecraftClient.getInstance().player != null) {
				// MinecraftClient.getInstance().player.sendMessage(text);
				// System.out.println(text);
				String Content = text.toString();
				if (Content.contains("5DD339") || Content.contains("5488D6")) {
					int count1 = Content.split("5DD339", -1).length - 1;
					int count2 = Content.split("5488D6", -1).length - 1;
					if (count1 == 2 || count2 == 2) {
						Hand hand = Hand.MAIN_HAND;
                        assert MinecraftClient.getInstance().interactionManager != null;
                        ActionResult actionResult = MinecraftClient.getInstance().interactionManager.interactItem(MinecraftClient.getInstance().player, hand);
						if (actionResult.isAccepted()) {
							if (actionResult.shouldSwingHand()) {
								MinecraftClient.getInstance().player.swingHand(hand);
							}
							MinecraftClient.getInstance().gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
						}
						System.out.println("ПКМ!");
						MinecraftClient.getInstance().player.sendMessage(Text.of("ПКМ!"));
					}
				}
				else {
					try {
						Thread.sleep(500); // 1000 миллисекунд = 1 секунда
						System.out.println(Content);
						Hand hand = Hand.MAIN_HAND;
						assert MinecraftClient.getInstance().interactionManager != null;
						ActionResult actionResult = MinecraftClient.getInstance().interactionManager.interactItem(MinecraftClient.getInstance().player, hand);
						if (actionResult.isAccepted()) {
							if (actionResult.shouldSwingHand()) {
								MinecraftClient.getInstance().player.swingHand(hand);
							}
							MinecraftClient.getInstance().gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}