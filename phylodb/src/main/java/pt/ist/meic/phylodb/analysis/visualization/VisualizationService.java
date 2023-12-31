package pt.ist.meic.phylodb.analysis.visualization;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ist.meic.phylodb.analysis.inference.model.Inference;
import pt.ist.meic.phylodb.analysis.visualization.model.Visualization;
import pt.ist.meic.phylodb.security.project.model.Project;
import pt.ist.meic.phylodb.typing.dataset.model.Dataset;
import pt.ist.meic.phylodb.utils.service.Entity;
import pt.ist.meic.phylodb.utils.service.UnversionedEntityService;

import java.util.List;
import java.util.Optional;

/**
 * Class that contains operations to manage visualizations
 * <p>
 * The service responsibility is to guarantee that the database state is not compromised and verify all business rules.
 */
@Service
public class VisualizationService extends UnversionedEntityService<Visualization, Visualization.PrimaryKey> {

	private VisualizationRepository visualizationRepository;

	public VisualizationService(VisualizationRepository visualizationRepository) {
		this.visualizationRepository = visualizationRepository;
	}

	/**
	 * Operation to retrieve the resumed information of the requested visualizations
	 *
	 * @param projectId   identifier of the {@link Project project} that contains the dataset containing the visualizations
	 * @param datasetId   identifier of the {@link Dataset dataset} which contains the visualizations
	 * @param inferenceId identifier of the {@link Inference inference}
	 * @param page        number of the page to retrieve
	 * @param limit       number of inferences to retrieve by page
	 * @return an {@link Optional} with a {@link List} of {@link Entity<Visualization.PrimaryKey>}, which is the resumed information of each visualization
	 */
	@Transactional(readOnly = true)
	public Optional<List<Entity<Visualization.PrimaryKey>>> getVisualizations(String projectId, String datasetId, String inferenceId, int page, int limit) {
		return getAllEntities(page, limit, projectId, datasetId, inferenceId);
	}

	/**
	 * Operation to retrieve the requested visualization
	 *
	 * @param projectId       identifier of the {@link Project project} that contains the dataset containing the visualizations
	 * @param datasetId       identifier of the {@link Dataset dataset} which contains the visualizations
	 * @param inferenceId     identifier of the {@link Inference inference}
	 * @param visualizationId identifier of the  {@link Visualization visualization}
	 * @return an {@link Optional} of {@link Visualization}, which is the requested visualization
	 */
	@Transactional(readOnly = true)
	public Optional<Visualization> getVisualization(String projectId, String datasetId, String inferenceId, String visualizationId) {
		return get(new Visualization.PrimaryKey(projectId, datasetId, inferenceId, visualizationId));
	}

	/**
	 * Operation to deprecate a visualization
	 *
	 * @param projectId       identifier of the {@link Project project} that contains the dataset containing the visualizations
	 * @param datasetId       identifier of the {@link Dataset dataset} which contains the visualizations
	 * @param inferenceId     identifier of the {@link Inference inference}
	 * @param visualizationId identifier of the  {@link Visualization visualization}
	 * @return {@code true} if the visualization was deprecated
	 */
	@Transactional
	public boolean deleteVisualization(String projectId, String datasetId, String inferenceId, String visualizationId) {
		return remove(new Visualization.PrimaryKey(projectId, datasetId, inferenceId, visualizationId));
	}

	@Override
	protected Optional<List<Entity<Visualization.PrimaryKey>>> getAllEntities(int page, int limit, Object... params) {
		return visualizationRepository.findAllEntities(page, limit, params[0], params[1], params[2]);
	}

	@Override
	protected Optional<Visualization> get(Visualization.PrimaryKey key) {
		return visualizationRepository.find(key);
	}

	@Override
	protected boolean save(Visualization entity) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	protected boolean remove(Visualization.PrimaryKey key) {
		return visualizationRepository.remove(key);
	}

}
