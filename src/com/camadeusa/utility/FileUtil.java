package com.camadeusa.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class FileUtil {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static String[] getLines(File file) {
        String string;
        if (file != null && file.isFile()) {
            try {
                FileReader reader = new FileReader(file);
                Throwable throwable = null;
                try {
                    BufferedReader buffered = new BufferedReader(reader);
                    ArrayList<String> lines = new ArrayList<String>();
                    while (buffered.ready()) {
                        lines.add(buffered.readLine());
                    }
                    buffered.close();
                    String[] arrstring = lines.toArray(new String[lines.size()]);
                    return arrstring;
                }
                catch (Throwable buffered) {
                    throwable = buffered;
                    throw buffered;
                }
                finally {
                    if (reader != null) {
                        if (throwable != null) {
                            try {
                                reader.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        } else {
                            reader.close();
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        if (file == null) {
            string = "null!";
            throw new IllegalArgumentException("The given File was " + string);
        }
        string = "missing!";
        throw new IllegalArgumentException("The given File was " + string);
    }

    public static File getFile(String dir, String ... subfiles) {
        String filepath = dir;
        for (String subfile : subfiles) {
            filepath = filepath + File.separator + subfile;
        }
        filepath = filepath + File.separator;
        return new File(filepath);
    }

    public static File getFile(String dir, String name) {
        new File(dir).mkdirs();
        File file = new File(dir + name);
        try {
            file.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static boolean doesExist(String url) {
        return new File(url).exists();
    }

    public static void recursiveCopy(File source, File target) {
        if (source.isDirectory()) {
            String[] files;
            if (!target.exists()) {
                target.mkdir();
            }
            for (String file : files = source.list()) {
                File srcFile = new File(source, file);
                File targFile = new File(target, file);
                FileUtil.recursiveCopy(srcFile, targFile);
            }
        } else {
            try {
                int length;
                FileInputStream in = new FileInputStream(source);
                FileOutputStream out = new FileOutputStream(target);
                byte[] buffer = new byte[1024];
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.flush();
                out.close();
            }
            catch (IOException e) {
                throw new RuntimeException(String.format("Could not copy %s to %s!", source.getAbsolutePath(), target.getAbsolutePath()));
            }
        }
    }

    public static boolean recursiveDelete(File base) {
        if (!base.exists()) {
            return true;
        }
        if (base.isDirectory()) {
            for (File subfile : base.listFiles()) {
                if (FileUtil.recursiveDelete(subfile)) continue;
                return false;
            }
        }
        if (!base.delete()) {
            return false;
        }
        return true;
    }
}

