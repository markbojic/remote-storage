package main;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public final class SdkUtil {

    private static final String TEST_CLIENT_IDENTIFIER = "test";

    private SdkUtil() {

    }

    public static DbxClientV2 createTestDbxClientV2(String accessToken) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder(TEST_CLIENT_IDENTIFIER).build();
        return new DbxClientV2(config, accessToken);
    }
}
