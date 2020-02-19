package pt.ist.meic.phylodb.phylogeny.locus.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class GetLocusOutputModel {

	private String id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String description;

	public GetLocusOutputModel() {
	}

	public GetLocusOutputModel(Locus locus) {
		this.id = locus.getId();
		this.description = locus.getDescription();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
