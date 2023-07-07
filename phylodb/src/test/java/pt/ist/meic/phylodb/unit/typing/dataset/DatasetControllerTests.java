package pt.ist.meic.phylodb.unit.typing.dataset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.CreatedOutputModel;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.typing.dataset.model.DatasetInputModel;
import pt.ist.meic.phylodb.typing.dataset.model.DatasetOutputModel;
import pt.ist.meic.phylodb.typing.dataset.model.GetDatasetOutputModel;
import pt.ist.meic.phylodb.typing.schema.model.Schema;
import pt.ist.meic.phylodb.unit.ControllerTestsContext;
import pt.ist.meic.phylodb.utils.service.VersionedEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class DatasetControllerTests extends ControllerTestsContext {

	private static Stream<Arguments> getDatasets_params() {
		String uri = "/projects/%s/datasets";
		String projectId = UUID.randomUUID().toString(), datasetId = UUID.randomUUID().toString();
		VersionedEntity<Dataset.PrimaryKey> dataset = new VersionedEntity<>(new Dataset.PrimaryKey(projectId, datasetId), 1, false);
		List<VersionedEntity<Dataset.PrimaryKey>> datasets = new ArrayList<VersionedEntity<Dataset.PrimaryKey>>() {{
			add(dataset);
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, projectId)).param("page", "0"),
				req2 = get(String.format(uri, projectId)), req3 = get(String.format(uri, projectId)).param("page", "-10");
		List<DatasetOutputModel> result = datasets.stream()
				.map(DatasetOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, datasets, HttpStatus.OK, result, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, datasets, HttpStatus.OK, result, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getProject_params() {
		String uri = "/projects/%s/datasets/%s";
		String projectId = UUID.randomUUID().toString(), datasetId = UUID.randomUUID().toString();
		VersionedEntity<Schema.PrimaryKey> schemaReference = new VersionedEntity<>(new Schema.PrimaryKey("t", "x"), 1, false);
		Dataset dataset = new Dataset(projectId, datasetId, 1, false, "name1", schemaReference);
		MockHttpServletRequestBuilder req1 = get(String.format(uri, projectId, datasetId)).param("version", "1"),
				req2 = get(String.format(uri, projectId, datasetId));
		return Stream.of(Arguments.of(req1, dataset, HttpStatus.OK, new GetDatasetOutputModel(dataset)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())),
				Arguments.of(req2, dataset, HttpStatus.OK, new GetDatasetOutputModel(dataset)),
				Arguments.of(req2, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> putProject_params() {
		String uri = "/projects/%s/datasets/%s";
		String projectId = UUID.randomUUID().toString(), datasetId = UUID.randomUUID().toString();
		MockHttpServletRequestBuilder req1 = put(String.format(uri, projectId, datasetId));
		DatasetInputModel input1 = new DatasetInputModel(datasetId, "description", "t", "x"),
				input2 = new DatasetInputModel(UUID.randomUUID().toString(), null, null, "x");
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> postProject_params() {
		String uri = "/projects/%s/datasets";
		String projectId = UUID.randomUUID().toString(), datasetId = UUID.randomUUID().toString();
		MockHttpServletRequestBuilder req1 = post(String.format(uri, projectId));
		DatasetInputModel input1 = new DatasetInputModel(datasetId, "description", "t", "x"),
				input2 = new DatasetInputModel(UUID.randomUUID().toString(), null, null, "x");
		return Stream.of(Arguments.of(req1, input1, true, HttpStatus.CREATED, null),
				Arguments.of(req1, input1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())),
				Arguments.of(req1, input2, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())),
				Arguments.of(req1, null, false, HttpStatus.BAD_REQUEST, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> deleteProject_params() {
		String uri = "/projects/%s/datasets/%s";
		String projectId = UUID.randomUUID().toString(), datasetId = UUID.randomUUID().toString();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, projectId, datasetId));
		return Stream.of(Arguments.of(req1, true, HttpStatus.NO_CONTENT, new NoContentOutputModel()),
				Arguments.of(req1, false, HttpStatus.UNAUTHORIZED, new ErrorOutputModel(Problem.UNAUTHORIZED.getMessage())));
	}

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(authenticationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
		Mockito.when(authorizationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
	}

	@ParameterizedTest
	@MethodSource("getDatasets_params")
	public void getDatasets(MockHttpServletRequestBuilder req, List<VersionedEntity<Dataset.PrimaryKey>> datasets, HttpStatus expectedStatus, List<DatasetOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(datasetService.getDatasets(any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(datasets));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if (expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getId(), p.get("id"));
					assertEquals(expectedResult.get(i).getVersion(), Long.parseLong(p.get("version").toString()));
				}
			}
		} else
			assertEquals(expectedError, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getProject_params")
	public void getProject(MockHttpServletRequestBuilder req, Dataset dataset, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(datasetService.getDataset(any(), any(), anyLong())).thenReturn(Optional.ofNullable(dataset));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful())
			assertEquals(expectedResult, parseResult(GetDatasetOutputModel.class, result));
		else
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("putProject_params")
	public void updateProject(MockHttpServletRequestBuilder req, DatasetInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(datasetService.saveDataset(any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("postProject_params")
	public void postProject(MockHttpServletRequestBuilder req, DatasetInputModel input, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(datasetService.saveDataset(any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, input);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			CreatedOutputModel parsed = parseResult(CreatedOutputModel.class, result);
			assertNotNull(parsed.getId());
		} else if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteProject_params")
	public void deleteProject(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(datasetService.deleteDataset(any(), any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

}
