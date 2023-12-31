package pt.ist.meic.phylodb.unit.analysis.visualization;

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
import pt.ist.meic.phylodb.analysis.visualization.model.*;
import pt.ist.meic.phylodb.error.ErrorOutputModel;
import pt.ist.meic.phylodb.error.Problem;
import pt.ist.meic.phylodb.io.output.NoContentOutputModel;
import pt.ist.meic.phylodb.io.output.OutputModel;
import pt.ist.meic.phylodb.unit.ControllerTestsContext;
import pt.ist.meic.phylodb.utils.service.Entity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class VisualizationControllerTests extends ControllerTestsContext {

	private static final String PROJECTID =  PROJECT1.getPrimaryKey(), DATASETID = DATASET1.getPrimaryKey().getId(),
			INFERENCEID = INFERENCE1.getPrimaryKey().getId();

	private static Stream<Arguments> getVisualizations_params() {
		String uri = "/projects/%s/datasets/%s/inferences/%s/visualizations";
		Entity<Visualization.PrimaryKey> visualization1 = new Entity<>(new Visualization.PrimaryKey(PROJECTID, DATASETID, INFERENCEID, UUID.randomUUID().toString()), false),
				visualization2 = new Entity<>(new Visualization.PrimaryKey(PROJECTID, DATASETID, INFERENCEID, UUID.randomUUID().toString()), false);
		List<Entity<Visualization.PrimaryKey>> visualizations = new ArrayList<Entity<Visualization.PrimaryKey>>() {{
			add(visualization1);
			add(visualization2);
		}};
		MockHttpServletRequestBuilder req1 = get(String.format(uri, PROJECTID, DATASETID, INFERENCEID)).param("page", "0"),
				req2 = get(String.format(uri, PROJECTID, DATASETID, INFERENCEID)), req3 = get(String.format(uri, PROJECTID, DATASETID, INFERENCEID)).param("page", "-10");
		List<VisualizationOutputModel> result1 = visualizations.stream()
				.map(VisualizationOutputModel::new)
				.collect(Collectors.toList());
		return Stream.of(Arguments.of(req1, visualizations, HttpStatus.OK, result1, null),
				Arguments.of(req1, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req2, visualizations, HttpStatus.OK, result1, null),
				Arguments.of(req2, Collections.emptyList(), HttpStatus.OK, Collections.emptyList(), null),
				Arguments.of(req3, null, HttpStatus.BAD_REQUEST, null, new ErrorOutputModel(Problem.BAD_REQUEST.getMessage())));
	}

	private static Stream<Arguments> getVisualization_params() {
		String uri = "/projects/%s/datasets/%s/inferences/%s/visualizations/%s";
		List<Coordinate> coordinates = new ArrayList<Coordinate>() {{
			add(COORDINATE11);
			add(COORDINATE12);
		}};
		Visualization.PrimaryKey key1 = VISUALIZATION1.getPrimaryKey();
		Visualization visualization = new Visualization(PROJECTID, DATASETID, INFERENCEID, key1.getId(), false, VisualizationAlgorithm.RADIAL, coordinates);
		MockHttpServletRequestBuilder req1 = get(String.format(uri, PROJECTID, DATASETID, INFERENCEID, key1.getId()));
		return Stream.of(Arguments.of(req1, visualization, HttpStatus.OK, new GetVisualizationOutputModel(visualization)),
				Arguments.of(req1, null, HttpStatus.NOT_FOUND, new ErrorOutputModel(Problem.NOT_FOUND.getMessage())));
	}

	private static Stream<Arguments> deleteVisualization_params() {
		String uri = "/projects/%s/datasets/%s/inferences/%s/visualizations/%s";
		Visualization.PrimaryKey key = VISUALIZATION1.getPrimaryKey();
		MockHttpServletRequestBuilder req1 = delete(String.format(uri, PROJECTID, DATASETID, INFERENCEID, key.getId()));
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
	@MethodSource("getVisualizations_params")
	public void getInferences(MockHttpServletRequestBuilder req, List<Entity<Visualization.PrimaryKey>> visualizations, HttpStatus expectedStatus, List<VisualizationOutputModel> expectedResult, ErrorOutputModel expectedError) throws Exception {
		Mockito.when(visualizationService.getVisualizations(any(), any(), any(), anyInt(), anyInt())).thenReturn(Optional.ofNullable(visualizations));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			List<Map<String, Object>> parsed = parseResult(List.class, result);
			assertEquals(expectedResult.size(), parsed.size());
			if (expectedResult.size() > 0) {
				for (int i = 0; i < expectedResult.size(); i++) {
					Map<String, Object> p = parsed.get(i);
					assertEquals(expectedResult.get(i).getId(), p.get("id"));
				}
			}
		} else
			assertEquals(expectedError, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("getVisualization_params")
	public void getVisualization(MockHttpServletRequestBuilder req, Visualization visualization, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(visualizationService.getVisualization(any(), any(), any(), any())).thenReturn(Optional.ofNullable(visualization));
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is2xxSuccessful()) {
			GetVisualizationOutputModel actual = parseResult(GetVisualizationOutputModel.class, result);
			assertEquals(expectedResult, actual);
		} else
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

	@ParameterizedTest
	@MethodSource("deleteVisualization_params")
	public void deleteVisualization(MockHttpServletRequestBuilder req, boolean ret, HttpStatus expectedStatus, OutputModel expectedResult) throws Exception {
		Mockito.when(visualizationService.deleteVisualization(any(), any(), any(), any())).thenReturn(ret);
		MockHttpServletResponse result = executeRequest(req, MediaType.APPLICATION_JSON);
		assertEquals(expectedStatus.value(), result.getStatus());
		if (expectedStatus.is4xxClientError())
			assertEquals(expectedResult, parseResult(ErrorOutputModel.class, result));
	}

}
