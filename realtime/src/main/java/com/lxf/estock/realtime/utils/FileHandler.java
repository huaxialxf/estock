package com.lxf.estock.realtime.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @Title:文件按行处理
 * @Description:
 * @Author : LuXinFeng
 * @Since : 2016年3月21日
 * @Version : 1.0.0
 */
public abstract class FileHandler {
    int n = 0;

    private void doHandleToOutStream(String srcFileName, FileOutputStream fos) throws IOException {
        File srcFile = new File(srcFileName);
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            for (File file : files) {
                doHandleToOutStream(file.getAbsolutePath(), fos);
            }
        } else {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)));
                String line = null;

                while ((line = reader.readLine()) != null) {
                    if (line == null || "".equals(line)) {
                        continue;
                    }
                    String newLine = doHandlerLine(line);
                    if (newLine != null) {
                        fos.write(newLine.getBytes());
                        fos.write("\n".getBytes());
                    }
                    if ((n++) % 100000 == 0) {
                        System.out.println("n:" + n + " " + new Date());
                    }
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

        }
    }

    public void doHandle(String srcFileName, String tarFileName) throws IOException {

        FileOutputStream fos = null;
        try {
            if (tarFileName != null) {
                File tarFile = new File(tarFileName);
                if (tarFile.getParentFile() != null && !tarFile.getParentFile().exists()) {
                    tarFile.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(tarFile);
            }
            doHandleToOutStream(srcFileName, fos);
        } finally {
            if (fos != null) {
                fos.close();

            }
        }
        System.out.println("total:" + n);
    }

    public abstract String doHandlerLine(String line);

    public static void main(String[] args) throws IOException {
        final Set<String> set = new HashSet<String>();
        new FileHandler() {

            @Override
            public String doHandlerLine(String line) {
                String suffix = line.substring(line.indexOf(".") + 1);
                if (!set.contains(suffix)) {
                    set.add(suffix);
                }

                return null;
            }
        }.doHandle("E:/out/register_monitor_cctld/unregistered_init", null);
        for (String string : set) {
            System.out.println(string);
        }
    }

}
