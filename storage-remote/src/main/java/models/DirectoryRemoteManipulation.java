package models;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.dropbox.core.v2.files.DownloadZipErrorException;
import com.dropbox.core.v2.files.DownloadZipResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import main.App;
import main.SdkUtil;
import specs.DirectoryManipulation;
import users.User;

public class DirectoryRemoteManipulation implements DirectoryManipulation {

	@Override
	public void createDirectory(String name, String path, User user) {
		if (user.getPrivileges()[0]) {

			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
			try {
				client.files().createFolderV2(path + "/" + name);
			} catch (CreateFolderErrorException e1) {
				e1.printStackTrace();
			} catch (DbxException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("User does not have required privilage.");
		}
	}

	@Override
	public void deleteDirectory(String path, User user) {
		if (user.getPrivileges()[1]) {

			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
			try {
				client.files().delete("/" + path);
			} catch (CreateFolderErrorException e1) {
				e1.printStackTrace();
			} catch (DbxException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("User does not have required privilage.");
		}

	}

	private ArrayList<String> allFiles = new ArrayList<String>();
	private ArrayList<String> fileNames = new ArrayList<String>();

	@Override
	public void uploadDirectory(String selectedPath, String destinationPath, User user) {
		if (user.getPrivileges()[2]) {
			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);

			populateDirectoryFilesLists(selectedPath);
			String[] splitter = selectedPath.split("/");
			String directoryName = splitter[splitter.length - 1];
			System.out.println(splitter[0] + "   ");
			createDirectory(directoryName, destinationPath, user);
			String directoryPath = (destinationPath + "/" + directoryName);// Path za novi direktorijum

			for (int i = 0; i < allFiles.size(); i++) {
				try (InputStream in = new FileInputStream(allFiles.get(i))) {
					FileMetadata metadata = client.files().uploadBuilder(directoryPath + "/" + fileNames.get(i))
							.uploadAndFinish(in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("User does not have required privilage.");
		}

	}

	private void populateDirectoryFilesLists(String directoryPath) {
		File dir = new File(directoryPath);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if (child.isFile()) {
					allFiles.add(child.getAbsolutePath());
					fileNames.add(child.getName());
					System.out.println("Added " + child.getName());
				} else {
					if (child.isDirectory()) {
						System.out.println("Found directory -> " + child.getName());
						populateDirectoryFilesLists(child.getAbsolutePath());

					}
				}
			}
		} else {
			// Handle the case where dir is not really a directory.
			// Checking dir.isDirectory() above would not be sufficient
			// to avoid race conditions with another process that deletes
			// directories.
		}
	}

	@Override
	public void downloadDirectory(String selectedPath, String destinationPath, User user) {
		if (user.getPrivileges()[3]) {
			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);

			DbxDownloader<DownloadZipResult> dw = null;
			try {
				dw = client.files().downloadZip("/" + selectedPath); // Bira koji diretorijum skida
				String[] splitter = selectedPath.split("/");
				String fileName = splitter[splitter.length - 1];
				FileOutputStream out1 = new FileOutputStream(destinationPath + "/" + fileName + ".zip");// Gde se skida
																										// zip
				BufferedOutputStream bout = new BufferedOutputStream(out1);
				dw.download(bout);
				bout.close();
				out1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("User does not have required privilage.");
		}

	}

	@Override
	public void listAllFiles(String path) {
		DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
		ListFolderResult result;
		try {
			result = client.files().listFolder(path);
			while (true) {
				for (Metadata metadata : result.getEntries()) {
					System.out.println("->" + metadata.getName());
				}
				if (!result.getHasMore()) {
					break;
				}
				result = client.files().listFolderContinue(result.getCursor());
			}
		} catch (ListFolderErrorException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void listFiles(String path, String extension) {
		DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
		ListFolderResult result;
		try {
			result = client.files().listFolder(path);
			while (true) {
				for (Metadata metadata : result.getEntries()) {
					String[] splitter = metadata.getName().split("[.]");
					if(splitter[splitter.length - 1].equalsIgnoreCase(extension))//Listaj samo ako sadrzi ekstenziju
						System.out.println("->>" + metadata.getPathDisplay());
				}
				if (!result.getHasMore()) {
					break;
				}
				result = client.files().listFolderContinue(result.getCursor());
			}
		} catch (ListFolderErrorException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		}


	}

	@Override
	public void listDirectories(String path) {
		DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
		ListFolderResult result;
		try {
			result = client.files().listFolder(path);
			while (true) {
				for (Metadata metadata : result.getEntries()) {
						if(metadata instanceof FolderMetadata)
							System.out.println("->>>" + metadata.getPathDisplay());
				}
				if (!result.getHasMore()) {
					break;
				}
				result = client.files().listFolderContinue(result.getCursor());
			}
		} catch (ListFolderErrorException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}

}
