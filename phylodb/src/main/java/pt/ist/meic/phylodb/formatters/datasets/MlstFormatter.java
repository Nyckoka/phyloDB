package pt.ist.meic.phylodb.formatters.datasets;

import pt.ist.meic.phylodb.typing.profile.model.Profile;

import java.util.stream.Stream;

public class MlstFormatter implements DatasetFormatter<Profile> {


	public MlstFormatter() {
	}

	@Override
	public Dataset<Profile> parse(Stream<String> data) {
		return null;
	}

	@Override
	public String format(Dataset<Profile> data) {
		return null;
	}

}
