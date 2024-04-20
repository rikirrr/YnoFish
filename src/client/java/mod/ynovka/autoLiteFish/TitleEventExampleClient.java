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
				if (Content.contains("4CB5B6") ||Content.contains("5DD339") || Content.contains("739F60") || Content.contains("5488D6") || Content.contains("4CAF99") || Content.contains("A58F4C") || Content.contains("7DA7CA") || Content.contains("FFFFFF") || Content.contains("FFE58A")) {
					int count1 = Content.split("5DD339", -1).length - 1;
					int count2 = Content.split("5488D6", -1).length - 1;
					int count3 = Content.split("FFFFFF", -1).length - 1;
					int count4 = Content.split("FFE58A", -1).length - 1;
					int count5 = Content.split("7DA7CA", -1).length - 1;
					int count6 = Content.split("A58F4C", -1).length - 1;
					int count7 = Content.split("4CAF99", -1).length - 1;
					int count8 = Content.split("739F60", -1).length - 1;
					int count9 = Content.split("4CB5B6", -1).length - 1;
					if (count9 == 2 || count1 == 2 || count2 == 2 || count8 == 2 || count3 == 2 || count7 == 2 || count4 == 2 || count5 == 2 || count6 == 2) {
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
					System.out.println(Content);

					try {
						Thread.sleep(300); // 1000 миллисекунд = 1 секунда
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
