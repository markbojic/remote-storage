package main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.dropbox.core.v2.files.DownloadBuilder;
import com.dropbox.core.v2.files.DownloadZipResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import models.DirectoryRemoteManipulation;
import models.FileRemoteManipulation;
import specs.DirectoryManipulation;
import users.User;

public class App {

	public static final String ACCESS_TOKEN = "Gtb2Dvk8yKAAAAAAAAAAFLHGpRNZA0DT2z1NikOvWDJISMvOzJaSg48W2vzUQ1UI";

	public static void main(String[] args) throws DbxException, IOException {
		DbxClientV2 client = SdkUtil.createTestDbxClientV2(ACCESS_TOKEN);

		FullAccount account = client.users().getCurrentAccount();
		System.out.println(account.getName().getDisplayName());

		// Get files and folder metadata from Dropbox root directory
		/*
		 * ListFolderResult result = client.files().listFolder(""); while (true) { for
		 * (Metadata metadata : result.getEntries()) { System.out.println("-" +
		 * metadata.getPathDisplay());
		 * 
		 * }
		 * 
		 * if (!result.getHasMore()) { break; }
		 * 
		 * result = client.files().listFolderContinue(result.getCursor()); }
		 */

		DirectoryRemoteManipulation m = new DirectoryRemoteManipulation();
		FileRemoteManipulation f = new FileRemoteManipulation();
		boolean[] b = { true, true, true, true, true, true, true };
		// TEST ZA KREIRANJE NOVOG
		// DIREKTORIJUMA***********************************************************
		// RADI!
		// m.createDirectory("Novi Folder1", "/Novi Folder", new User("Admin", "pass",
		// b));

		// TEST ZA BRISANJE
		// DIREKTORIJUMA******************************************************************
		// RADI!
		// m.deleteDirectory("test.txt", new User("A", "A", b));

		// TEST ZA UPLOADOVANJE
		// DIREKTORIJUMA**************************************************************
		// RADI, ALI NE UPLOADUJE PODDIREKTORIJUME VEC SAMO FAJLOVE IZ NJIH
		// m.uploadDirectory("C:/New Folder", "", new User("A", "A", b));

		// TEST ZA DOWNLOADOVANJE DIREKTORIJUMA**************************************
		// RADI!
		// m.downloadDirectory("New Folder", "C:/DB Download", new User("A", "A", b));

		// TEST ZA LISTANJE SVIH FAJLOVA********************************* RADI!
		// m.listAllFiles("");

		// TEST ZA LISTANJE FAJLOVA SA EXT*********************************** RADI!
		// m.listFiles("", "txt");

		// TEST ZA LISTANJE DIREKTORIJUMA ************************************ RADI!
		// m.listDirectories("");

		//String directoryPath = "C:/New Folder";
		
		//TEST ZA UPLOAD DIRECTORY FULL*******************************************RADI!
		//uploadD(directoryPath, "");
		
		//TEST ZA CREATE FAJL **********************************************RADI!
		//f.createFile("Novi.zip", "/Test", new User("A", "A", b));
		
		//TEST ZA DELETE FILE*********************************************RADI!
		//f.deleteFile("/test.txt", new User("A", "a", b));
		
		//TEST ZA UPLOAD FAJLOVA *************************************RADI!
		//f.uploadFile("C:/New Folder/Test1.txt", "/New Folder", new User("A", "A", b));
		
		
		//TEST ZA DOWNLOAD *************************************** RADI!
		//f.downloadFile("/OutputFile.json", "C:/New Folder", new User("A", "A", b));
		
		//m.uploadDirectory(directoryPath, "", new User("A", "A", b));

	}


}
