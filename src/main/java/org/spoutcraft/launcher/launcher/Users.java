package org.spoutcraft.launcher.launcher;

import com.google.gson.JsonSyntaxException;
import net.technicpack.launchercore.install.user.IUserStore;
import net.technicpack.launchercore.install.user.User;
import net.technicpack.launchercore.util.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Users implements IUserStore {
    private String clientToken = UUID.randomUUID().toString();
    private Map<String, User> savedUsers = new HashMap<String, User>();
    private String lastUser;
    private transient File usersFile;

    public Users() {
    }

    public Users(File userFile) {
        this.usersFile = userFile;
    }

    public static Users load() {
        File userFile = new File(Utils.getSettingsDirectory(), "users.json");
        if (!userFile.exists()) {
            Utils.getLogger().log(Level.WARNING, "Unable to load users from " + userFile + " because it does not exist.");
            return new Users(userFile);
        }

        try {
            String json = FileUtils.readFileToString(userFile, Charset.forName("UTF-8"));
            Users newModel = Utils.getGson().fromJson(json, Users.class);
            newModel.setUserFile(userFile);
            return newModel;
        } catch (JsonSyntaxException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to load users from " + userFile);
        } catch (IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to load users from " + userFile);
        }

        return new Users(userFile);
    }

    public void setUserFile(File userFile) {
        this.usersFile = userFile;
    }

    public void save() {
        String json = Utils.getGson().toJson(this);

        try {
            FileUtils.writeStringToFile(usersFile, json, Charset.forName("UTF-8"));
        } catch (IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to save users " + usersFile);
        }
    }

    public void addUser(User user) {
        savedUsers.put(user.getUsername(), user);
        save();
    }

    public void removeUser(String username) {
        savedUsers.remove(username);
        save();
    }

    public User getUser(String accountName) {
        return savedUsers.get(accountName);
    }

    public String getClientToken() {
        return clientToken;
    }

    public Collection<String> getUsers() {
        return savedUsers.keySet();
    }

    public Collection<User> getSavedUsers() {
        return savedUsers.values();
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
        save();
    }

    public String getLastUser() {
        return lastUser;
    }
}
