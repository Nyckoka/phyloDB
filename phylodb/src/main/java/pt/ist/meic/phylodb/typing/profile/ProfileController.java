package pt.ist.meic.phylodb.typing.profile;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.formatters.dataset.profile.ProfilesFormatter;
import pt.ist.meic.phylodb.io.output.FileOutputModel;
import pt.ist.meic.phylodb.security.authorization.Authorized;
import pt.ist.meic.phylodb.security.authorization.Operation;
import pt.ist.meic.phylodb.security.authorization.Role;
import pt.ist.meic.phylodb.typing.profile.model.GetProfileOutputModel;
import pt.ist.meic.phylodb.typing.profile.model.GetProfilesOutputModel;
import pt.ist.meic.phylodb.typing.profile.model.ProfileInputModel;
import pt.ist.meic.phylodb.utils.controller.Controller;

import java.io.IOException;

import static pt.ist.meic.phylodb.utils.db.EntityRepository.CURRENT_VERSION;

@RestController("SequenceTypeController")
@RequestMapping("projects/{project}/datasets/{dataset}/profiles")
public class ProfileController extends Controller {

	private ProfileService service;

	public ProfileController(ProfileService service) {
		this.service = service;
	}

	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<?> getProfiles(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String type
	) {
		return getAll(type, l -> service.getProfiles(projectId, datasetId, page, l),
				p -> new GetProfilesOutputModel(p.getValue()),
				p -> new FileOutputModel(ProfilesFormatter.get(p.getKey().getType().getName()).format(p.getValue(), p.getKey())));
	}

	@Authorized(role = Role.USER, operation = Operation.READ)
	@GetMapping(path = "/{profile}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProfile(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("profile") String profileId,
			@RequestParam(value = "version", defaultValue = CURRENT_VERSION) Long version
	) {
		return get(() -> service.getProfile(projectId, datasetId, profileId, version), GetProfileOutputModel::new, () -> new ErrorOutputModel(Problem.NOT_FOUND));
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/{profile}")
	public ResponseEntity<?> putProfile(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("profile") String profileId,
			@RequestParam(value = "private_alleles", defaultValue = "false") boolean authorized,
			@RequestBody ProfileInputModel input
	) {
		return put(() -> input.toDomainEntity(projectId, datasetId, profileId), p -> service.saveProfile(p, authorized));
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PostMapping(path = "/files")
	public ResponseEntity<?> postProfiles(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "private_alleles", defaultValue = "false") boolean authorized,
			@RequestParam("file") MultipartFile file
	) throws IOException {
		return fileStatus(() -> service.saveProfilesOnConflictSkip(projectId, datasetId, authorized, file));
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@PutMapping(path = "/files")
	public ResponseEntity<?> putProfiles(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@RequestParam(value = "private_alleles", defaultValue = "false") boolean authorized,
			@RequestParam("file") MultipartFile file
	) throws IOException {
		return fileStatus(() -> service.saveProfilesOnConflictUpdate(projectId, datasetId, authorized, file));
	}

	@Authorized(role = Role.USER, operation = Operation.WRITE)
	@DeleteMapping(path = "/{profile}")
	public ResponseEntity<?> deleteProfile(
			@PathVariable("project") String projectId,
			@PathVariable("dataset") String datasetId,
			@PathVariable("profile") String profileId
	) {
		return status(() -> service.deleteProfile(projectId, datasetId, profileId));
	}

}
