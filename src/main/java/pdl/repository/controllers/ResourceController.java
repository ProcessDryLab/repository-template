package pdl.repository.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import pdl.repository.types.IType;
import pdl.repository.visualizers.IVisualizer;
import pdl.repository.web.models.Resource;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin
public class ResourceController {

	public static final String UPLOAD_PATH = System.getProperty("java.io.tmpdir") + File.separator + "uploads";
	public static final String INFO_FILE_NAME = "info.json";
	public static final String CONTENT_FILE_NAME = "content-";

	static {
		new File(UPLOAD_PATH).mkdirs();
		System.out.println("Using '" + UPLOAD_PATH + "' as upload path");
	}

	@GetMapping("/resources")
	public ResponseEntity<List<Resource>> list() {
		List<Resource> resources = new ArrayList<>();

		ObjectMapper objectMapper = new ObjectMapper();
		for (File child : new File(UPLOAD_PATH).listFiles()) {
			try {
				resources.add(objectMapper.readValue(
						new File(child.getAbsolutePath() + File.separator + INFO_FILE_NAME), Resource.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ResponseEntity.ok(resources);
	}

	@PostMapping("/resources")
	public ResponseEntity<Resource> add(@RequestPart(value = "upfile") MultipartFile upfile) throws Exception {

		UUID id = UUID.randomUUID();
		String path = UPLOAD_PATH + File.separator + id.toString() + File.separator;
		new File(path).mkdirs();

		IType resourceType = IType.construct(upfile.getOriginalFilename());
		Resource r = new Resource(id, upfile.getOriginalFilename(), resourceType.getResourceType(), new Date());

		Files.copy(upfile.getInputStream(), Paths.get(path + CONTENT_FILE_NAME + r.getName()));

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(new File(path + INFO_FILE_NAME), r);

		return ResponseEntity.ok(r);
	}

	@DeleteMapping("/resources/{uuid}")
	public ResponseEntity<Resource> delete(@PathVariable("uuid") String uuid) {
		if (new File(UPLOAD_PATH + File.separator + uuid + File.separator + INFO_FILE_NAME).exists()) {
			if (deleteDirectory(new File(UPLOAD_PATH + File.separator + uuid + File.separator))) {
				return ResponseEntity.ok().build();
			}
		}
		return ResponseEntity.notFound().build();

	}

	@GetMapping("/resources/{uuid}/details")
	public ResponseEntity<Resource> details(@PathVariable("uuid") String uuid) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return ResponseEntity.ok(objectMapper.readValue(
					new File(UPLOAD_PATH + File.separator + uuid + File.separator + INFO_FILE_NAME), Resource.class));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping(value = "/resources/{uuid}/content")
	public @ResponseBody ResponseEntity<org.springframework.core.io.Resource> content(@PathVariable("uuid") String uuid)
			throws IOException {
		if (new File(UPLOAD_PATH + File.separator + uuid + File.separator + INFO_FILE_NAME).exists()) {
			Resource res = new ObjectMapper().readValue(
					new File(UPLOAD_PATH + File.separator + uuid + File.separator + INFO_FILE_NAME), Resource.class);
			org.springframework.core.io.Resource file = new FileSystemResource(
					UPLOAD_PATH + File.separator + uuid + File.separator + CONTENT_FILE_NAME + res.getName());
			return ResponseEntity.ok(file);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping(value = "/resources/{uuid}/view/{visualization-id}")
	public @ResponseBody String view(@PathVariable("uuid") String uuid, @PathVariable("visualization-id") String visId)
			throws Exception {

		if (new File(UPLOAD_PATH + File.separator + uuid + File.separator + INFO_FILE_NAME).exists()) {
			Resource res = new ObjectMapper().readValue(
					new File(UPLOAD_PATH + File.separator + uuid + File.separator + INFO_FILE_NAME), Resource.class);
			File content = new File(
					UPLOAD_PATH + File.separator + uuid + File.separator + CONTENT_FILE_NAME + res.getName());

			IVisualizer visualizer = IVisualizer.construct(res.getType(), visId);
			if (visualizer != null) {
				return visualizer.getVisualization(res, content);
			}
		}
		return "";
	}

	private boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}
}
