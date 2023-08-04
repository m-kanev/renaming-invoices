package com.renaming.service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    public static List<String> getCompanyNames(String fileName) {
        String companyNamesFile = fileName;
        ArrayList<String> namesList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(companyNamesFile))) {
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


    public static void main(String[] args) {
        String namesDI = "companyNamesInvoices.txt";
        String destZipFilePath = "C:\\Users\\marti\\Documents\\Invoices/Invoices original 06.2023.zip";
        String sourceFolderPath = "DI original";

        List<String> companyNamesDI = getCompanyNames(namesDI);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFilePath))) {
            File sourceFolder = new File(sourceFolderPath);
            File[] files = sourceFolder.listFiles();

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
}

