package mod.ynovka.autoLiteFish;

import me.shedaniel.autoconfig.AutoConfig;

import mod.ynovka.autoLiteFish.config.AutoLiteFishConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

public class TitleEventExampleClient implements ClientModInitializer {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isCatched = false;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(AutoLiteFishConfig.class, Toml4jConfigSerializer::new);

        ClientPlayConnectionEvents.JOIN.register((ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) -> {
            AutoLiteFishConfig config = AutoConfig.getConfigHolder(AutoLiteFishConfig.class).getConfig();
            if (config.sendFeedbackEnabled) {
                // Получаем имя игрока и IP сервера
                if (client.player != null) {
                    String NickName = client.player.getName().getString();
                    String ServerIP = client.getCurrentServerEntry() != null ? client.getCurrentServerEntry().address : "localhost";

                    // Вызов функции sendFeedBack
                    sendFeedBack(NickName, ServerIP);
                }
            }
        });

        // Register the command
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);

        // Register a callback for the event. Every time the event fires, it runs the method that you register
        TitleMessageEvents.SET_TITLE.register((text, isSubTitle) -> {
            // if the title being set is not a subtitle, and the player exists, add the title to the chat hud
            AutoLiteFishConfig config = AutoConfig.getConfigHolder(AutoLiteFishConfig.class).getConfig();
            if (!isSubTitle && MinecraftClient.getInstance().player != null && config.autoFishEnabled) {

                // get bar content
                String Content;
                if (text.toString().length() > 115 + 88) {
                    Content = text.toString().substring(115, text.toString().length() - 88);
                } else {
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
                    UseRod();
                    if (!isCatched) {
                        isCatched = true;
                    }
                } else if (isCatched && colors.size() != 3) {
                    scheduler.schedule(this::UseRod, 300, TimeUnit.MILLISECONDS);
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

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        AutoLiteFishConfig config = AutoConfig.getConfigHolder(AutoLiteFishConfig.class).getConfig();
        LiteralArgumentBuilder<FabricClientCommandSource> command = ClientCommandManager.literal("ynofish")
                .then(ClientCommandManager.literal("autofish")
                        .then(ClientCommandManager.argument("state", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    builder.suggest("on");
                                    builder.suggest("off");
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String state = StringArgumentType.getString(context, "state");
                                    if ("on".equalsIgnoreCase(state)) {
                                        config.autoFishEnabled = true;
                                        context.getSource().sendFeedback(Text.literal("Auto fishing enabled."));
                                    } else if ("off".equalsIgnoreCase(state)) {
                                        config.autoFishEnabled = false;
                                        context.getSource().sendFeedback(Text.literal("Auto fishing disabled."));
                                    } else {
                                        context.getSource().sendFeedback(Text.literal("Invalid argument. Use 'on' or 'off'."));
                                    }
                                    return 1;
                                })
                        )
                )
                .then(ClientCommandManager.literal("sendfeedback")
                        .then(ClientCommandManager.argument("state", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    builder.suggest("yes");
                                    builder.suggest("no");
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String state = StringArgumentType.getString(context, "state");
                                    if ("yes".equalsIgnoreCase(state)) {
                                        config.sendFeedbackEnabled = true;
                                        context.getSource().sendFeedback(Text.literal("Feedback sending enabled."));
                                    } else if ("no".equalsIgnoreCase(state)) {
                                        config.sendFeedbackEnabled = false;
                                        context.getSource().sendFeedback(Text.literal("Feedback sending disabled."));
                                    } else {
                                        context.getSource().sendFeedback(Text.literal("Invalid argument. Use 'yes' or 'no'."));
                                    }
                                    AutoConfig.getConfigHolder(AutoLiteFishConfig.class).save();
                                    return 1;
                                })
                        )
                );
        dispatcher.register(command);
    }

    public static void sendFeedBack(String NickName, String ServerIP) {
        String Date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM-HH:mm"));

        // Please don't use this webhook :)
        String webhookUrl = "https://discord.com/api/webhooks/1267757270058663978/K-FNOyZHKIpVYPuuCsbRQhOyku-Z5l-zwHrZSjg0rShcb5FF8zSbZWPnl8IKR-qsYA-y";
        String jsonPayload = "{"
                + "\"embeds\":[{"
                + "\"title\":\"\","
                + "\"description\":\"\","
                + "\"color\":5410968,"
                + "\"footer\":{\"text\":\"\"},"
                + "\"author\":{\"name\":\"\"},"
                + "\"fields\":["
                + "{\"name\":\"NickName\",\"value\":\"" + NickName + "\",\"inline\":true},"
                + "{\"name\":\"ServerIP\",\"value\":\"" + ServerIP + "\",\"inline\":false},"
                + "{\"name\":\"Date\",\"value\":\"" + Date + "\",\"inline\":true}"
                + "]"
                + "}],"
                + "\"content\":\"\""
                + "}";

        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Request sent successfully.");
            } else {
                System.out.println("Request failed. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
