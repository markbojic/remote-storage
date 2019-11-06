package models;

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
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;

import users.AbstractUser;

public class User extends AbstractUser {

	private static final String ACCESS_TOKEN = "Gtb2Dvk8yKAAAAAAAAAAFLHGpRNZA0DT2z1NikOvWDJISMvOzJaSg48W2vzUQ1UI";

	public User(String username, String password) {
		super(username, password);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createUser(String username, String password, boolean[] privs, String root) {
		if (isAdmin()) {
			if (username != null && password != null && privs.length == 4) {

				DbxRequestConfig config = DbxRequestConfig.newBuilder(this.getUsername()).build();
				DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
				ListFolderResult result;
				File c = new File("src" + "/" + "accounts.log");
				try {
					c.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				OutputStream outputStream;
				// boolean exists = false;
				try {
					result = client.files().listFolder(root);
					while (true) {
						for (Metadata metadata : result.getEntries()) {
							if (metadata instanceof FileMetadata && metadata.getName().equals("accounts.log")) {
								outputStream = new FileOutputStream(c);
								metadata = client.files().downloadBuilder(root + "/accounts.log")
										.download(outputStream);// Kopija
								// accounts.log
								// je u src
								outputStream.close();
								BufferedReader reader;
								try {
									reader = new BufferedReader(new FileReader(c.getAbsoluteFile()));
									String line = reader.readLine();
									while (line != null) {
										System.out.println(line);
										String splitter[] = line.split("/");
										if (splitter[0].equalsIgnoreCase(username)) {
											System.out.println("Username taken!");
											Files.deleteIfExists(Paths.get(c.getAbsolutePath()));
											return;
										}
										line = reader.readLine();
									}
									reader.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
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
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					FileOutputStream fos = new FileOutputStream(c, true); // Set to true for append mode
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
					try {
						bw.write(username + "/" + password + "/" + privs[0] + "/" + privs[1] + "/" + privs[2] + "/"
								+ privs[3]);
						bw.newLine();
						bw.close();
						fos.close();
						rewriteAccountLog(c, root);
						System.out.println("User " + username + " created!");
						PrintWriter writer;
						try {
							writer = new PrintWriter(c);
							writer.print("");
							writer.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

			} else {
				System.out.println("Worng user input!");

			}

		} else {
			System.out.println("Only admin can create new users!");
		}

	}

	@Override
	public void deleteUser(String username, String root) {
		// TODO Auto-generated method stub
		if (isAdmin()) {
			DbxRequestConfig config = DbxRequestConfig.newBuilder(this.getUsername()).build();
			DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
			ListFolderResult result;
			File c = new File("src" + "/" + "accounts.log");
			try {
				c.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			OutputStream outputStream;
			boolean exists = false;
			try {
				result = client.files().listFolder(root);
				while (true) {
					for (Metadata metadata : result.getEntries()) {
						if (metadata instanceof FileMetadata && metadata.getName().equals("accounts.log")) {
							outputStream = new FileOutputStream(c);
							metadata = client.files().downloadBuilder(root + "/accounts.log").download(outputStream);// Kopija
							// accounts.log
							// je u src
							outputStream.close();
							BufferedReader reader;
							try {
								reader = new BufferedReader(new FileReader(c.getAbsoluteFile()));
								String line = reader.readLine();
								while (line != null) {
									String splitter[] = line.split("/");
									if (splitter[0].equalsIgnoreCase(username)) {
										exists = true;
									}
									line = reader.readLine();
								}
								reader.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

						}

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
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (exists == false) {
				System.out.println("No user with that name.");
				return;
			}
			List<String> newContent;
			try {
				newContent = Files.lines(c.toPath()).filter(line -> !line.contains(username))
						.collect(Collectors.toList());
				Files.write(c.toPath(), newContent, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
				rewriteAccountLog(c, root);
				PrintWriter writer;
				try {
					writer = new PrintWriter(c);
					writer.print("");
					writer.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				System.out.println("User " + username + " deleted!");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("Only admin can delete users!");
		}

	}

	@Override
	public void listAllUsers(String root) {
		if (isAdmin()) {
			DbxRequestConfig config = DbxRequestConfig.newBuilder(this.getUsername()).build();
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
				result = client.files().listFolder(root);
				while (true) {
					for (Metadata metadata : result.getEntries()) {
						if (metadata instanceof FileMetadata && metadata.getName().equals("accounts.log")) {
							outputStream = new FileOutputStream(c);
							metadata = client.files().downloadBuilder(root + "/accounts.log").download(outputStream);// Kopija
							// accounts.log
							// je u src
							outputStream.close();
							BufferedReader reader;
							try {
								reader = new BufferedReader(new FileReader(c.getAbsoluteFile()));
								String line = reader.readLine();
								while (line != null) {
									System.out.println(line);
									line = reader.readLine();
								}
								reader.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

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
			} catch (IOException e) {
				e.printStackTrace();
			}

			PrintWriter writer;
			try {
				writer = new PrintWriter(c);
				writer.print("");
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Only admin can do this!");
		}

	}

	/**
	 * 
	 * @param newFile new file to replace the old account.log
	 * @param dest    root destination where the new file will be stored
	 */
	public void rewriteAccountLog(File newFile, String dest) {
		DbxRequestConfig config = DbxRequestConfig.newBuilder(this.getUsername()).build();
		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
		try {

			try (InputStream in = new FileInputStream(newFile)) {
				@SuppressWarnings("unused")
				FileMetadata metadata = client.files().uploadBuilder(dest + "/" + "accounts.log")
						.withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
		}
	}

}
