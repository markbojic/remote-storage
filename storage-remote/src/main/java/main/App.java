package main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Paths;
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
import com.dropbox.core.v2.files.RelocationErrorException;
import com.dropbox.core.v2.users.FullAccount;
import models.DirectoryRemoteImplementation;
import models.FileRemoteImplementation;
import models.User;
import specs.DirectoryManipulation;

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

		DirectoryRemoteImplementation m = new DirectoryRemoteImplementation();
		
		//boolean[] b = { true, true, true, true};
		
		//User u = new User("A", "AA");
		
		//m.initStorage("Novi Storage Buu", u);
		//FileRemoteImplementation f = new FileRemoteImplementation();
		//f.setRoot(m.getRoot());
		///f.setForbiddenExtensions(m.getForbidden());
		//System.out.println(f.getRoot() + " ---- " + f.getForbiddenExtensions()[0]);

		// move("/Test.txt", "/PFFF.txt");
		// TEST ZA KREIRANJE NOVOG
		// DIREKTORIJUMA***********************************************************
		// RADI!
		// m.createDirectory("Novi Folder1", "/Novi Folder", new User("Admin", "pass",
		// b));

		// TEST ZA BRISANJE
		// DIREKTORIJUMA******************************************************************
		// RADI!
		// m.deleteDirectory("/New Folder", new User("A", "A", b));

		// TEST ZA UPLOADOVANJE
		// DIREKTORIJUMA**************************************************************
		// RADI, ALI NE UPLOADUJE PODDIREKTORIJUME VEC SAMO FAJLOVE IZ NJIH
		// m.uploadDirectory("C:/New Folder", "", new User("A", "A", b));

		// TEST ZA DOWNLOADOVANJE DIREKTORIJUMA**************************************
		// RADI!
		// m.downloadDirectory("/New Folder", "C:/DD", new User("A", "A", b));

		// TEST ZA LISTANJE SVIH FAJLOVA********************************* RADI!
		// m.listAllFiles("");

		// TEST ZA LISTANJE FAJLOVA SA EXT*********************************** RADI!
		// m.listFiles("", "txt");

		// TEST ZA LISTANJE DIREKTORIJUMA ************************************ RADI!
		// m.listDirectories("");

		// String directoryPath = "C:/New Folder";

		// TEST ZA UPLOAD DIRECTORY FULL*******************************************RADI!
		// m.uploadDirectory("C:/New Folder 1", "", new User("A", "A", b));

		// TEST ZA CREATE FAJL **********************************************RADI!
		// f.createFile("Novi.zip", "/Test", new User("A", "A", b));

		// TEST ZA DELETE FILE*********************************************RADI!
		// f.deleteFile("/test.txt", new User("A", "a", b));

		// TEST ZA UPLOAD FAJLOVA *************************************RADI!
		// f.uploadFile("C:/New Folder/Test1.txt", "/New Folder", new User("A", "A",
		// b));

		// TEST ZA DOWNLOAD *************************************** RADI!
		// f.downloadFile("/OutputFile.json", "C:/New Folder", new User("A", "A", b));

		// m.uploadDirectory(directoryPath, "", new User("A", "A", b));

		//m.createDirectory("Folder", "", new User("A", "AA", b));
		//f.createMetaFile(new User("A", "A", b), "JTest.txt", "txt", "/New Folder");
		
		//f.uploadFile("C:/New Folder/Test.txt", "", new User("A", "AA", b));
		
		//f.deleteFile("/Test.txt", new User("A", "AA", b));
		//String[] ext = {"rar"};
		
		//m.initStorage("", "Remote Storage", ext, new User("C", "ASS", b));
		
		//User u = new User("C", "ASS");
		//u.setPrivileges(b);
		//u.setAdmin(true);
		//m.initStorage("Remote Storage", ext, u);
		//FileRemoteManipulation f = new FileRemoteManipulation();
		//f.setForbiddenExtensions(ext);
		//f.setRoot(m.getRoot());
		//m.createDirectory("A", "", u);
		//f.setForbiddenExtensions("");
		
		//m.downloadDirectory("New Folder", "C:/New Folder 1", u);
		//m.listDirectories("New Folder");
	//	m.listFiles("New Folder", "txt");
		//m.listAllFiles("New Folder");
		//u.listAllUsers(m.getRoot(), ACCESS_TOKEN);
		//f.downloadFile("r.txt", "C:/New Folder 1", u);
		//f.createFile("Mojfajl.txt", "", u);
		//f.deleteFile("Mojfajl.txt", u);
		//f.uploadFile("C:/New Folder 1/r.txt", "", u);
	//	f.rewriteAccountLog("C:/New Folder/accounts.log",m.getRoot());
	//	System.out.println(m.getRoot());
		//u.createUser("NOVI", "A", b, m.getRoot(),ACCESS_TOKEN);
		//u.createUser("Nikola", "Mightrps", b, m.getRoot(), ACCESS_TOKEN);
	//	u.createUser("Nikola2", "Mightrps", b, m.getRoot(), ACCESS_TOKEN);
	//	u.createUser("Nikola3", "Mightrps", b, m.getRoot(), ACCESS_TOKEN);
	//	u.listAllUsers(m.getRoot(), ACCESS_TOKEN);
		
	//	m.uploadZipDirectory("C:/New Folder", "", u);
		//u.deleteUser("Nikola", m.getRoot(), ACCESS_TOKEN);
		//System.out.println(m.getRoot());
		
		//Test za upload zip multiple
		//ArrayList<String> paths = new ArrayList<String>();
		//paths.add("C:/New Folder/Test.txt");
		//paths.add("C:/New Folder 1/r.txt");
		//f.uploadMultipleFilesZip(paths, "", "Zipperoni", u);
		
	}

	private static void move(String path, String dest) {
		DbxClientV2 client = SdkUtil.createTestDbxClientV2(ACCESS_TOKEN);

		try {
			Metadata metadata = client.files().move(path, dest);
		} catch (RelocationErrorException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}

}
