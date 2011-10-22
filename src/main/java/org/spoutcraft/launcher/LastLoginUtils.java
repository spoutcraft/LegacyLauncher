package org.spoutcraft.launcher;

import java.io.*;
import java.security.MessageDigest;

public class LastLoginUtils
{
    private static byte[] hashLastLogin(String user, String pass)
    {
        try
        {
            byte[] val = (user.length() + user.toLowerCase() + pass.length() + pass).getBytes();
            return MessageDigest.getInstance("SHA-256").digest(val);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static boolean isLastLogin(String user, String pass, File file)
    {
        try
        {
            FileInputStream in = new FileInputStream(file);
            byte[] fileHash = new byte[in.available()];
            in.read(fileHash);

            byte[] desired = hashLastLogin(user, pass);
            if (desired == null)
            {
                return false;
            }
            if (fileHash.length != desired.length)
            {
                return false;
            }
            for (int i = 0; i < fileHash.length; ++i)
            {
                if (fileHash[i] != desired[i])
                {
                    return false;
                }
            }

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static void setLastLogin(String user, String pass, File file)
    {
        try
        {
            byte[] hash = hashLastLogin(user, pass);
            if (hash == null)
            {
                return;
            }

            if (!file.exists() && !file.createNewFile())
            {
                throw new IOException("target file does not exist and could not be created");
            }

            FileOutputStream out = new FileOutputStream(file, false);
            out.write(hash);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
        }
    }
}
