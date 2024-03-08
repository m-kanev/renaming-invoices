package com.renaming.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) {
        // Do the DI first and fix martin.kanev depending on the PC
        String TYPE = "DI";
//        String TYPE = "Invoices";

//        String MODE = "copy";
        String MODE = "original";
        String DATE = "02.2024";

        //Don't touch anything here
        String namesDI = "companyNames" + TYPE + ".txt";
        // rename martin.kanev to martinkanev depending on the comp
        String destZipFilePath = "/Users/martinkanev/Documents/" + TYPE + "/" + TYPE + "_" + MODE + "_" + DATE + ".zip";
        String sourceFolderPath = "DI " + MODE;


        List<String> companyNamesDI = getCompanyNames(namesDI);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(destZipFilePath)))) {
            File sourceFolder = new File(sourceFolderPath);
            File[] files = sourceFolder.listFiles();
            Arrays.sort(files, Comparator.comparing(File::getName));

            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        String oldName = files[i].getName();
                        String newName = companyNamesDI.get(i) + "-" + oldName;
                        File renamedFile = new File(sourceFolder, newName);

                        if (files[i].renameTo(renamedFile)) {
                            System.out.println(oldName + " renamed to " + newName);
                        } else {
                            System.out.println("Failed to rename " + oldName);
                        }

                        addFileToZip(renamedFile, renamedFile.getName(), zos);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Files renamed and zipped to " + destZipFilePath);
    }

    public static List<String> getCompanyNames(String fileName) {
        ArrayList<String> namesList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                namesList.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return namesList;
    }

    private static void addFileToZip(File file, String filePath, ZipOutputStream zos) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(filePath);
        zos.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }

        fis.close();
        zos.closeEntry();
    }

}

