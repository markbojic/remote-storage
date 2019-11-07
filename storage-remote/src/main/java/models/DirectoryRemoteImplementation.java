package models;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import specs.DirectoryManipulation;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.dropbox.core.v2.files.DownloadZipErrorException;
import com.dropbox.core.v2.files.DownloadZipResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;

import common.FileUtil;
import main.App;
import main.SdkUtil;
import users.AbstractUser;

@SuppressWarnings("unused")
//REMOTE!!!!!!
public class DirectoryRemoteImplementation implements DirectoryManipulation {

	/*
	 * @param root
	 */
	private String root;
	private String[] forbidden;//Saamo za uplaod zip
	private static final String ACCESS_TOKEN = "Gtb2Dvk8yKAAAAAAAAAAFLHGpRNZA0DT2z1NikOvWDJISMvOzJaSg48W2vzUQ1UI";
	
	

	public String[] getForbidden() {
		return forbidden;
	}

	public void setForbidden(String[] forbidden) {
		this.forbidden = forbidden;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.DirectoryManipulation#createDirectory(java.lang.String,
	 * java.lang.String, users.AbstractUser)
	 */
	@Override
	public void createDirectory(String name, String path, AbstractUser user) {
		if (user.getPrivileges()[0]) {

			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
			try {
				if(!path.equalsIgnoreCase("")) {
					client.files().createFolderV2(root + "/" + path + "/" + name);
				}
				else
				{
					client.files().createFolderV2(root + "/" + name);
				}
			} catch (CreateFolderErrorException e1) {
				System.out.println("Wrong path!");
			} catch (DbxException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("AbstractUser does not have required privilage.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.DirectoryManipulation#deleteDirectory(java.lang.String,
	 * users.AbstractUser)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void deleteDirectory(String path, AbstractUser user) {
		if (user.getPrivileges()[1]) {

			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
			try {
				if(!path.equalsIgnoreCase(""))
					client.files().delete(root + "/" + path);
			} catch (CreateFolderErrorException e1) {
				e1.printStackTrace();
			} catch (DbxException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("AbstractUser does not have required privilage.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.DirectoryManipulation#uploadDirectory(java.lang.String,
	 * java.lang.String, users.AbstractUser)
	 */
	@Override
	public void uploadDirectory(String selectedPath, String destinationPath, AbstractUser user) {
		if (user.getPrivileges()[2]) {
			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);

			File local = new File(selectedPath);

			String cale = null;

			try {
				if(!destinationPath.equals("")){
				client.files().createFolderV2(destinationPath + "/" + local.getName());
				cale = destinationPath + "/" + local.getName();// F1
				}
				else
				{
					client.files().createFolderV2(root + "/" + local.getName());
					cale = root + "/" + local.getName();// F1
				}
				System.out.println("Added folder " + cale);
			} catch (CreateFolderErrorException e1) {
				e1.printStackTrace();
			} catch (DbxException e1) {
				e1.printStackTrace();
			}

			File[] directoryListing = local.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					if (child.isFile()) {

						try (InputStream in = new FileInputStream(child)) {
							FileMetadata metadata = client.files().uploadBuilder(cale + "/" + child.getName())
									.uploadAndFinish(in);
							FileRemoteImplementation fr = new FileRemoteImplementation();
							fr.setRoot(root);
							fr.createMetaFile((User) user, child.getName(), "b", cale);
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.out.println("Added " + child.getName());
					} else {
						uploadDirectory(child.getAbsolutePath(), cale, user);
					}
				}
			}
		}
		System.out.println("AbstractUser does not have required privilage!");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.DirectoryManipulation#downloadDirectory(java.lang.String,
	 * java.lang.String, users.AbstractUser)
	 */
	@Override
	public void downloadDirectory(String selectedPath, String destinationPath, AbstractUser user) {
		if (user.getPrivileges()[3]) {
			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);

			DbxDownloader<DownloadZipResult> dw = null;
			try {
				dw = client.files().downloadZip(root + "/" + selectedPath); // Bira koji diretorijum skida
				String[] splitter = selectedPath.split("/");
				String fileName = splitter[splitter.length - 1];
				FileOutputStream out1 = new FileOutputStream(destinationPath + "/" + fileName + ".zip");// Gde se skida
																										// zip
				BufferedOutputStream bout = new BufferedOutputStream(out1);
				dw.download(bout);
				bout.close();
				out1.close();
			} catch (Exception e) {
				System.out.println("Invalid path parameter!");
			}
		}
		System.out.println("AbstractUser does not have required privilage!");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.DirectoryManipulation#listAllFiles(java.lang.String)
	 */
	@Override
	public void listAllFiles(String path) {
		DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
		ListFolderResult result;
		try {
			if(path.equalsIgnoreCase(""))
				result = client.files().listFolder(root);
			else
				result = client.files().listFolder(root + "/" + path);
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
			System.out.println("Wrong path!");
		} catch (DbxException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.DirectoryManipulation#listFiles(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void listFiles(String path, String extension) {
		DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
		ListFolderResult result;
		try {
			if(path.equalsIgnoreCase(""))
				result = client.files().listFolder(root);
			else
				result = client.files().listFolder(root + "/" + path);
			while (true) {
				for (Metadata metadata : result.getEntries()) {
					String[] splitter = metadata.getName().split("[.]");
					if (splitter[splitter.length - 1].equalsIgnoreCase(extension))// Listaj samo ako sadrzi ekstenziju
						System.out.println("->>" + metadata.getPathDisplay());
				}
				if (!result.getHasMore()) {
					break;
				}
				result = client.files().listFolderContinue(result.getCursor());
			}
		} catch (ListFolderErrorException e) {
			System.out.println("Wrong path!");
		} catch (DbxException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.DirectoryManipulation#listDirectories(java.lang.String)
	 */
	@Override
	public void listDirectories(String path) {
		DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
		ListFolderResult result;
		try {
			if(path.equalsIgnoreCase(""))
				result = client.files().listFolder(root);
			else
				result = client.files().listFolder(root + "/" + path);
			while (true) {
				for (Metadata metadata : result.getEntries()) {
					if (metadata instanceof FolderMetadata)
						System.out.println("->>>" + metadata.getPathDisplay());
				}
				if (!result.getHasMore()) {
					break;
				}
				result = client.files().listFolderContinue(result.getCursor());
			}
		} catch (ListFolderErrorException e) {
			System.out.println("Wrong path!");
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param storageName         name of the storage you want to access
	 * @param forbiddenExtensions extension that cannot be used in the selected
	 *                            storage
	 * @param user                AbstractUser who accesses the storage with the given name
	 * 
	 *                            The method creates new Storage if a storage with
	 *                            such name does not exist, otherwise the root field
	 *                            will be set to the value of the wanted storage
	 *                            with the given storage name
	 */
	public void makeStorage(String storageName, String[] forbiddenExtensions, AbstractUser user) {

		DbxClientV2 client1 = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
		ListFolderResult result;
		try {
			result = client1.files().listFolder("");
			while (true) {
				for (Metadata metadata : result.getEntries()) {
					if (metadata instanceof FolderMetadata)
						if (metadata.getName().equalsIgnoreCase(storageName)) {
							System.out.println("Storage vec postoji!");
							setRoot("" + "/" + storageName);
							return;
						}
				}
				if (!result.getHasMore()) {
					break;
				}
				result = client1.files().listFolderContinue(result.getCursor());
			}
		} catch (ListFolderErrorException e) {
			System.out.println("Wrong path!");
		} catch (DbxException e) {
			e.printStackTrace();
		}

		try {
			client1.files().createFolderV2("" + "/" + storageName);
		} catch (DbxException e1) {
			e1.printStackTrace();
		}
		//createDirectory(storageName, "", user);// Korisnik mora da ima pristu kreiranju direktorijuma
		setRoot("" + "/" + storageName);
		user.setAdmin(true);

		File fileInfo = new File("src/storage-info.txt");
		try {
			FileOutputStream fos = new FileOutputStream(fileInfo, true);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(user.getUsername());
			bw.newLine();
			for (int i = 0; i < forbiddenExtensions.length; i++) {
				bw.write(forbiddenExtensions[i]);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);

		try (InputStream in = new FileInputStream(fileInfo.getAbsolutePath())) {
			String name = fileInfo.getName();
			FileMetadata metadata = client.files().uploadBuilder(getRoot() + "/" + name).uploadAndFinish(in);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Files.deleteIfExists(Paths.get(fileInfo.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		File fileAccs = new File("src/accounts.log");
		try {
			FileOutputStream fos = new FileOutputStream(fileAccs, true);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(user.getUsername() + "/" + user.getPassword() + "/" + true + "/" + true + "/" + true + "/" + true);
			bw.newLine();
			bw.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (InputStream in = new FileInputStream(fileAccs.getAbsolutePath())) {
			String name = fileAccs.getName();
			FileMetadata metadata = client.files().uploadBuilder(getRoot() + "/" + name).uploadAndFinish(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileAccs);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("static-access")
	@Override
	public void uploadZipDirectory(String path, String destination, AbstractUser user) {

		FileUtil util = new FileUtil();
		String splitter[] = path.split("/");
		String name = splitter[splitter.length - 1];
		util.zipDirectory(new File(path), "src", name);
		FileRemoteImplementation fr = new FileRemoteImplementation();
		fr.setRoot(root);
		fr.setForbiddenExtensions(forbidden);
		fr.uploadFile("src/" + name + ".zip", destination, user);
		try {
			Files.deleteIfExists(Paths.get("src/" + name + ".zip"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void initStorage(String storageName, AbstractUser user) {
		// TODO Auto-generated method stub

					// initRemoteStorage

					if (storageExists(storageName))
					// Ako postoji storage
					{
						// Log in - prolazak korz accounts file i provera da li se poklapa password i
						// username
						DbxRequestConfig config = DbxRequestConfig.newBuilder("testAccoutns").build();
						DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
						ListFolderResult result;
						File c = new File("src" + "/" + "accounts.log");
						try {
							c.createNewFile();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						OutputStream outputStream;
						try {
							result = client.files().listFolder("/" + storageName);
							while (true) {
								for (Metadata metadata : result.getEntries()) {
									if (metadata instanceof FileMetadata && metadata.getName().equals("accounts.log")) {
										outputStream = new FileOutputStream(c);
										metadata = client.files().downloadBuilder("/" + storageName + "/accounts.log")
												.download(outputStream);
										outputStream.close();
									}
								}
								break;
							}
							BufferedReader reader;
							boolean ex = false;
							try {
								reader = new BufferedReader(new FileReader(c.getAbsoluteFile()));
								String line = reader.readLine();
								while (line != null) {
									String splitter[] = line.split("/");
									if (splitter[0].equalsIgnoreCase(user.getUsername())) {
										if (splitter[1].contentEquals(user.getPassword())) {
											System.out.println("Logged in as :" + user.getUsername());
											boolean priv1 = splitter[2].equals("true") ? true : false;
											boolean priv2 = splitter[3].equals("true") ? true : false;
											boolean priv3 = splitter[4].equals("true") ? true : false;
											boolean priv4 = splitter[5].equals("true") ? true : false;
											boolean[] niz = { priv1, priv2, priv3, priv4 };
											user.setPrivileges(niz);//Set privilages of the user that logged in
											setRoot("/" + storageName);//Set root of the chosen storage
											System.out.println("Remote root set to : " + getRoot());
											reader.close();
											ex = true;
											break;
										} else
											if(ex == false) {
												System.out.println("Login failed!");
												reader.close();
												return;
											}
									}
									line = reader.readLine();
								}
								if(!ex) {
									System.out.println("Login failed! No user : " + user.getUsername());
									return;
								}
								reader.close();
								PrintWriter writer;
								writer = new PrintWriter(c);
								writer.print("");
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

						//Get storage info, check if user is Admin and what are the forbidden extensions
						File c1 = new File("src" + "/" + "storage-info.txt");
						try {
							c1.createNewFile();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						try {
							result = client.files().listFolder("/" + storageName);
							while (true) {
								for (Metadata metadata : result.getEntries()) {
									if (metadata instanceof FileMetadata && metadata.getName().equals("storage-info.txt")) {
										outputStream = new FileOutputStream(c1);
										metadata = client.files().downloadBuilder("/" + storageName + "/storage-info.txt")
												.download(outputStream);
										outputStream.close();
									}
								}
								break;
							}
							BufferedReader reader;
							try {
								reader = new BufferedReader(new FileReader(c1.getAbsoluteFile()));
								String line = reader.readLine();
								while (line != null) {
									if (line.trim().equalsIgnoreCase(user.getUsername())) {
										user.setAdmin(true);//If user is admin then set the admin to true
									}
									line = reader.readLine();
								}
								reader.close();

							} catch (IOException e) {
								e.printStackTrace();
							}
							BufferedReader br2;
							try {
								br2 = new BufferedReader(new FileReader(c1.getAbsoluteFile()));
								br2.readLine(); // preskoci prvu liniju
								String line = br2.readLine();
								String extensions = "";
								//Read nad set the forbiddent extensions already existing for the chosen storage
								while (line != null) {
									extensions = extensions + " " + line;
									System.out.println("FE -> " + line);
									line = br2.readLine();
								}
								String[] splitExts = extensions.split(" ");
								this.setForbidden(splitExts);
								br2.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}

							// Files.deleteIfExists(Paths.get(c1.getAbsolutePath()));
							//Empty the used file for storage-info
							PrintWriter writer;
							try {
								writer = new PrintWriter(c1);
								writer.print("");
								writer.close();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} 
					//If the sorage with that name does not exist, then make a new Storage with aall the required files
					else {
						// Pravi novi storage
						System.out.println("Storage does not exist, enter the forbidden extension for the new storage: ");
						boolean p[] = { true, true, true, true };
					//	user = new AbstractUser(username, password, p);
						//Set the user privilages to all and make the user Admin of the new Storage
						user.setAdmin(true);
						user.setPrivileges(p);
						System.out.println("Enter forbidden extensions: ");
						Scanner sc = new Scanner(System.in);
						String extensionsStr = sc.nextLine();
						String[] extensionArray = extensionsStr.split(" ");
						initStorageNew(storageName, user, extensionArray);
						sc.close();
					}
		
	}

	private boolean storageExists(String storageName) {
		DbxRequestConfig config = DbxRequestConfig.newBuilder("test").build();
		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
		ListFolderResult result;
		try {
			result = client.files().listFolder("");
			while (true) {
				for (Metadata metadata : result.getEntries()) {
					if (metadata instanceof FolderMetadata && metadata.getName().equals(storageName)) {
						System.out.println("Storage with name : " + storageName + " exists on dropbox.");
						return true;
					}
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public void initStorageNew(String storageName, AbstractUser user, String extensions[]) {//Log in sa sve inicijalizaciiji0km
		DbxRequestConfig config = DbxRequestConfig.newBuilder("storage").build();
		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
		//Make new sotrage Folder on the dropbox path
		try {
			client.files().createFolderV2("/" + storageName);
			//Set the root field to the new Storage path on dropbox
			setRoot("/" + storageName);
			//Set forbiidenExtension for the new Storage
			setForbidden(extensions);
			System.out.println("Root set to : " + getRoot());
		} catch (DbxException e) {
			e.printStackTrace();
		}
		
		//Make storage-info.txt file, write the extensions here
		File fileInfo = new File("src/storage-info.txt");
		try {
			FileOutputStream fos = new FileOutputStream(fileInfo, true);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(user.getUsername());//Write admin name
			bw.newLine();
			for (int i = 0; i < extensions.length; i++) {//Write extensions list
				bw.write(extensions[i]);
				bw.newLine();
			}
			bw.close();
			System.out.println("User : " + user.getUsername() + " /" + user.getPassword() + " /" + user.getPrivileges() + "created!");
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Upload the storage-info.txt to the dropbox/New Storage
		try (InputStream in = new FileInputStream(fileInfo.getAbsolutePath())) {
			String name = fileInfo.getName();
			FileMetadata metadata = client.files().uploadBuilder(getRoot() + "/" + name).uploadAndFinish(in);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Delete storage-info file in local memory
		try {
			Files.deleteIfExists(Paths.get(fileInfo.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Make accounts.log file
		File fileAccs = new File("src/accounts.log");
		try {
			FileOutputStream fos = new FileOutputStream(fileAccs, true);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(user.getUsername() + "/" + user.getPassword() + "/" + true + "/" + true + "/" + true + "/" + true);
			bw.newLine();
			bw.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Upload accounts.log to the new storage path (dropbox/NewStorage)
		try (InputStream in = new FileInputStream(fileAccs.getAbsolutePath())) {
			String name = fileAccs.getName();
			FileMetadata metadata = client.files().uploadBuilder(getRoot() + "/" + name).uploadAndFinish(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Delete the accounts.log file from local memory
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileAccs);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			Files.deleteIfExists(Paths.get(fileAccs.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
