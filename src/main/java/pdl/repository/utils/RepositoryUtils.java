package pdl.repository.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import pdl.repository.web.models.Resource;

public class RepositoryUtils {

	public static void storeResource(File file, String repositoryHostname) {
		String url = repositoryHostname + "/api/v1/resources";

		HttpPost post = new HttpPost(url);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addBinaryBody("upfile", file, ContentType.DEFAULT_BINARY, file.getName());
		org.apache.http.HttpEntity entity = builder.build();
		post.setEntity(entity);

		HttpClient client = HttpClientBuilder.create().build();
		try {
			client.execute(post);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File retrieveResource(Resource resource) throws IOException {
		String url = resource.getHost() + "/api/v1/resources/" + resource.getId() + "/content";
		InputStream in = new URL(url).openStream();
		File temp = File.createTempFile("test", resource.getName());
		Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return temp;
	}
}
