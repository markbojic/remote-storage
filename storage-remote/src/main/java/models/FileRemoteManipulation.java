package models;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.google.gson.stream.JsonWriter;

import main.App;
import main.SdkUtil;
import specs.FileManipulation;
import users.User;

public class FileRemoteManipulation implements FileManipulation {

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.FileManipulation#createFile(java.lang.String, java.lang.String,
	 * users.User)
	 */
	@Override
	public void createFile(String name, String path, User user) {
		if (user.getPrivileges()[0]) {

			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);
			File c = new File("src" + "/" + name);
			String extension = "";

			int i = c.getName().lastIndexOf('.');
			if (i > 0) {
			    extension = c.getName().substring(i+1);
			}
			try {
				c.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {

				try (InputStream in = new FileInputStream(c)) {
					@SuppressWarnings("unused")
					FileMetadata metadata = client.files().uploadBuilder(path + "/" + name).uploadAndFinish(in);
					createMetaFile(user, name, extension, path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
			}
			try {
				Files.deleteIfExists(Paths.get(c.getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("User does not have required privilage.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.FileManipulation#deleteFile(java.lang.String, users.User)
	 */
	@Override
	public void deleteFile(String path, User user) {
		if (user.getPrivileges()[1]) {

			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);

			try {
				@SuppressWarnings({ "unused", "deprecation" })
				Metadata metadata = client.files().delete(path);
				String metaPath = path.substring(0, path.lastIndexOf(".")) + "-meta.json";
				System.out.println(metaPath);
				client.files().delete(metaPath);
			} catch (DeleteErrorException e) {
				e.printStackTrace();
			} catch (DbxException e) {
				e.printStackTrace();
			} 
		} else {
			System.out.println("User does not have required privilage.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.FileManipulation#uploadFile(java.lang.String, java.lang.String,
	 * users.User)
	 */
	@Override
	public void uploadFile(String selectedPath, String destinationPath, User user) {

		if (user.getPrivileges()[2]) {
			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);

			try (InputStream in = new FileInputStream(selectedPath)) {
				File f = new File(selectedPath);
				String name = f.getName();
				String extension = "";
				int i = f.getName().lastIndexOf('.');
				if (i > 0) {
				    extension = f.getName().substring(i+1);
				}
				FileMetadata metadata = client.files().uploadBuilder(destinationPath + "/" + name).uploadAndFinish(in);
				createMetaFile(user, name, extension, destinationPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("User does not have required privilage.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see specs.FileManipulation#downloadFile(java.lang.String, java.lang.String,
	 * users.User)
	 */
	@SuppressWarnings("unused")
	@Override
	public void downloadFile(String selectedPath, String destinationPath, User user) {
		if (user.getPrivileges()[3]) {
			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);

			String[] splitter = selectedPath.split("/");
			String fileName = splitter[splitter.length - 1];
			System.out.println(fileName);
			OutputStream out;
			BufferedOutputStream bout;
			try {
				out = new FileOutputStream(destinationPath + "/" + splitter[splitter.length - 1]);
				bout = new BufferedOutputStream(out);
				try {

					FileMetadata metadata = client.files().downloadBuilder(selectedPath).download(bout);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {

						bout.close();
						out.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("User does not have required privilage.");
		}

	}

	/**
	 * Creates file with meta data for created/uploaded file
	 * 
	 * @param user     Who created/uploaded the file
	 * @param fileName Name of the original file
	 * @param fileType Type(extension) of the original file
	 * @param filePath Path of the directory where file will be stored
	 */
	public void createMetaFile(User user, String fileName, String fileType, String filePath) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());

		int end = fileName.length() - fileType.length() - 1;

		try {

			String json = "src/" + fileName.substring(0, end) + "-meta.json";

			JsonWriter writer = new JsonWriter(new FileWriter(json));
			writer.beginObject();
			writer.name("autor").value(user.getUsername());
			writer.name("file").value(fileName.substring(0, end));
			writer.name("type").value(fileType);
			writer.name("date").value(formatter.format(date));
			writer.endObject();
			writer.close();

			DbxClientV2 client = SdkUtil.createTestDbxClientV2(App.ACCESS_TOKEN);

			try (InputStream in = new FileInputStream(json)) {
				File f = new File(json);
				String name = f.getName();
				FileMetadata metadata = client.files().uploadBuilder(filePath + "/" + name).uploadAndFinish(in);

			} catch (Exception e) {
				e.printStackTrace();
			}

			Files.deleteIfExists(Paths.get(json));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
