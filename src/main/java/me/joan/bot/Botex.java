package me.joan.bot;

import me.joan.Main;
import me.joan.bot.command.CommandManager;
import me.joan.utils.PropertiesManager;

import me.joan.utils.Utils;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.*;
import java.time.*;

public class Botex {
    public static String prefix;
    public Main main;
    private JDA jda;
    private BotexListener listener;
    private PropertiesManager propertiesManager;
    private CommandManager commandManager;

    private final String[] channelNames = {"-reglas"};
    private final ArrayList<TextChannel> channels = new ArrayList<>();

    public Botex(Main main, String key) {
        this.main = main;
        this.propertiesManager = new PropertiesManager();
        this.propertiesManager.load(new File("botex.properties"));

        Botex.prefix = this.propertiesManager.getProperty("prefix");
        Botex.prefix = Utils.toUTF(Botex.prefix);

        this.commandManager = new CommandManager();
        this.listener = new BotexListener(this.commandManager);
        this.startBot(propertiesManager.getProperty("api"));
    }

    public void startBot(String key) {
        JDABuilder builder;
        builder = JDABuilder.createDefault(key);
        builder.setActivity(Activity.listening(Botex.prefix));
        builder.addEventListeners(this.listener);

        try {
            jda = builder.build();
            jda.upsertCommand("ping","Will return Pong! with the milisecond delay of the bot").queue();
        } catch (LoginException logex) {
            logex.printStackTrace();
        }

        try {
            jda.awaitReady();
        } catch(InterruptedException intex) {
            intex.printStackTrace();
        }

        for(Guild g : this.jda.getGuilds()) {
            for (TextChannel t : g.getTextChannels()) {
                if (Utils.stringContainsItemFromList(t.getName(), channelNames)) {
                    channels.add(t);
                    //t.sendMessage(getBotString(STRING_ON)).queue(); // Activado
                }
            }
        }

        Main.LOGGER.info(Collections.singletonList(channels));
    }

    public void disableBot() {
        this.jda.shutdown();
    }
}
