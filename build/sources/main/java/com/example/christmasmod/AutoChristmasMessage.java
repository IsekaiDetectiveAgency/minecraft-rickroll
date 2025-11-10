package com.example.christmasmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = AutoChristmasMessage.MODID, version = AutoChristmasMessage.VERSION, name = AutoChristmasMessage.NAME, clientSideOnly = true)
public class AutoChristmasMessage {

    public static final String MODID = "autochristmasmessage";
    public static final String VERSION = "1.0";
    public static final String NAME = "Auto Christmas Message";

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String FILENAME = "christ_messages.txt";

    private static final int TICKS_BETWEEN_MESSAGES = 400; // 20 tick = 1 second

    private String[] messages = new String[0];
    private int messageIndex = 0;
    private int tickCounter = 0;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        loadOrCreateMessages();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
    }

    private void loadOrCreateMessages() {
        File file = new File(mc.mcDataDir, FILENAME);

        if (!file.exists()) {
            PrintWriter pw = null;
            try {
                if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                pw = new PrintWriter(new FileWriter(file));
                pw.println("Merry Christmas everyone :D");
                pw.println("GLHF");
                pw.println("Have an awesome day!");
            } catch (IOException e) {
                System.err.println("[XmasMod] Failed to create " + FILENAME + ": " + e.getMessage());
            } finally {
                if (pw != null) pw.close();
            }
        }

        List<String> list = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) list.add(line);
            }
        } catch (IOException e) {
            System.err.println("[XmasMod] Failed to read " + FILENAME + ": " + e.getMessage());
        } finally {
            if (br != null) try { br.close(); } catch (IOException e) {}
        }

        if (list.isEmpty()) {
            list.add("Merry Christmas everyone :D");
            list.add("GLHF");
        }

        messages = list.toArray(new String[0]);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (messages.length == 0) return;

        tickCounter++;
        if (tickCounter >= TICKS_BETWEEN_MESSAGES) {
            mc.thePlayer.sendChatMessage(messages[messageIndex]);
            messageIndex = (messageIndex + 1) % messages.length;
            tickCounter = 0;
        }
    }
}
