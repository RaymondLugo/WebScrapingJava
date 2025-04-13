package com.kanechan.b3webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;



   public class Webscraping {
    
	public static void main(String[] args) {
    	
    String url = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
    String folder = "downloads"; // 
    String zipFileName = "Anexos";
    
    Path[] pdfFiles = new Path[2];    
    
    try {
		
    	if(!Files.exists(Paths.get(folder))) {
    		Files.createDirectories(Paths.get(folder));
    	}
    	
    		Document doc = Jsoup.connect(url).get(); 
    		
    		boolean anexo1Baixado = false;
    		boolean anexo2Baixado = false;
    		
    		for (var link : doc.select("a[href$=.pdf]")) {
    			
    			String fileUrl = link.absUrl("href");
    			
    	if(fileUrl.contains("Anexo_I") && !anexo1Baixado) {
    		   pdfFiles[0] = downloadFile(fileUrl, "Anexo_I.pdf");
    		   anexo1Baixado = true;
    		   
        } else if (fileUrl.contains("Anexo_II") && !anexo2Baixado) {
    	       pdfFiles[1] = downloadFile(fileUrl, "Anexo_II.pdf");
    	       anexo2Baixado = true;
    	    }
    	} 
    		
    	if (pdfFiles[0] != null && pdfFiles[1] != null) {
    		   zipFiles(pdfFiles, folder, zipFileName);
    		   System.out.println("Compactacao concluida");
    		
    	} else {
    		
    		   System.out.println("Nenhum arquivo foi encontrado");
    	}
    	
      } catch (Exception e) {
		System.out.println("Erro ao acessar e/ou processar o site");
	 }
   }

	public static Path downloadFile(String fileUrl, String fileName) {
	
		try {
			
			String home = System.getProperty("user.home");
			Path downloadsFolder = Paths.get(home, "Downloads");
			Path filePath = downloadsFolder.resolve(fileName);
			
		try (InputStream in = new URL (fileUrl).openStream()) {
				
				Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("Baixado: " + filePath.getFileName());
				return filePath;
			}
				
		  } catch (Exception e) {
			System.out.println("Erro ao baixar o arquivo: " + fileUrl + " - " + e.getMessage());	
			return null;			
		}
	  }
	
	 public static void zipFiles(Path[] files, String folder, String zipFileName) {
	  //Compactaçao dos arquivos para Zip
		try {
		   
	   String home = System.getProperty("user.home");
	   Path downloadsFolder = Paths.get(home, "Downloads");
	   Path zipFilePath = downloadsFolder.resolve(zipFileName + ".zip");
	   
	   try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()))) {
		   
		 for (Path file : files){
			 
			 if (file != null) {
				 try (FileInputStream fis = new FileInputStream(file.toFile())){
					 
					 zipOut.putNextEntry(new ZipEntry(file.getFileName().toString()));
					 
					 byte[] buffer = new byte[1024];
					 int length;
					 while ((length = fis.read(buffer)) > 0 ) {
						zipOut.write(buffer, 0, length);
					 }
					 
					 zipOut.closeEntry();
				 }
			 }
		 }
		 
		 System.out.println("Compactaçao concluida: " + zipFileName + ".zip");
		 
	  }
	   
	} catch (Exception e) {
		
		System.out.println("Erro ao compactar arquivos: " + e.getMessage());
	       }
	   }
   }
