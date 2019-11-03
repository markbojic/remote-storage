package models;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
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
			try {
				c.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {

				try (InputStream in = new FileInputStream(c)) {
					FileMetadata metadata = client.files().uploadBuilder(path + "/" + name).uploadAndFinish(in);
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
				Metadata metadata = client.files().delete(path);
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
				FileMetadata metadata = client.files().uploadBuilder(destinationPath + "/" + name).uploadAndFinish(in);

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

}
